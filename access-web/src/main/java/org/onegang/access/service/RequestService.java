package org.onegang.access.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import org.onegang.access.ActionDeniedException;
import org.onegang.access.ValidationException;
import org.onegang.access.dao.AccessDao;
import org.onegang.access.dto.Action;
import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.AccessChange.Change;
import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.kafka.Topics;
import org.onegang.access.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

@Service
public class RequestService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);
	
	@Autowired
	private AccessDao accessDao;
	
	@Autowired
	private UsersService userService;
	
	@Autowired
	private KafkaTemplate<String, Request> kafkaTemplate;
	
	@Value("${spring.profiles.active:None}")
	private String activeProfiles;
	

	public Collection<Request> getRequests(String filter) {
		if("OPENED".equals(filter))
			return accessDao.getOpenedRequests(userService.getCurrentUser());
		else if("CLOSED".equals(filter))
			return accessDao.getClosedRequests(userService.getCurrentUser());
		else if("FOR_ACTION".equals(filter))
			return accessDao.getPendingActionRequests(userService.getCurrentUser());
		else
			throw new IllegalArgumentException("Unknown filter found: " + filter);
	}

	public Request getRequest(int id) {
		Request request = accessDao.getRequest(id);
		request.setChanges(userService.combineChanges(request.getChanges()));
		return request;
	}
	
	public Collection<ApprovalUser> getApprovers(Request request) throws ValidationException {
		if(!Utils.isEmpty(request.getApprovers()))
			return request.getApprovers();
		
		//otherwise infer from the roles...
		AccessChange changes = userService.computeChanges(request.getUsers());
		Collection<String> changedRoles = Sets.newHashSet();
		for(Change change: changes.getAdded()) {
			changedRoles.addAll(change.getRoles());
		}
		for(Change change: changes.getRemoved()) {
			changedRoles.addAll(change.getRoles());
		}
		
		if(Utils.isEmpty(request.getManual()) && Utils.isEmpty(changedRoles))
			throw new ValidationException("No changes specified. Please state the required access to add/remove and try again.");
		
		Collection<ApprovalUser> approvers = Sets.newHashSet();
		for(String role: changedRoles) {
			Collection<ApprovalUser> roleApprovers =  accessDao.getApprovingUsers(role);
			LOGGER.info("Approvers for role {}: {}", role, roleApprovers.toString());
			if(Utils.isEmpty(roleApprovers))
				throw new ValidationException("Role " + role + " does not have any valid approvers. Please contact the Administrators.");
			approvers.addAll(roleApprovers);
		}
		
		if(Utils.isEmpty(approvers)) {
			throw new ValidationException("No approvers specified. Please review and add approvers.");
		}
		
		return approvers;
	}

	public Request submitRequest(Request request) throws ValidationException {
		LOGGER.info("Submitting request: {}", request.toString());
		AccessChange changes = userService.computeChanges(request.getUsers());
		
		if(request.getPurpose()==null)
			request.setPurpose("New request");
		if(Utils.isEmpty(request.getApprovers())) {
			request.setApprovers(getApprovers(request));
		}
		request.setStatus(Status.APPROVING);
		for(ApprovalUser user: request.getSupporters())
			user.setStatus(Status.PENDING);
		for(ApprovalUser user: request.getApprovers())
			user.setStatus(Status.PENDING);
		request.setRequestor(userService.getCurrentUser());
		request.setChanges(changes);
		request = accessDao.addRequest(request);
		sendApprovalMessage(request);
		return request;
	}
	
	public void uploadAttachment(int requestId, String filename, InputStream inputStream) throws IOException {
		LOGGER.info("Uploading {} into request {}...", filename, requestId);
		accessDao.uploadAttachment(requestId, filename, inputStream);
		LOGGER.info("Uploaded {} into request {}", filename, requestId);
	}
	
	public Resource downloadAttachment(int requestId, String filename) throws IOException {
		LOGGER.info("Downloading {} of request {}...", filename, requestId);
		return accessDao.downloadAttachment(requestId, filename);
	}
	
	public Collection<Action> getRequestActions(int id) {	
		Collection<Action> actions = Sets.newHashSet();
		Request request = getRequest(id);
		String me = userService.getCurrentUser();
		
		if(request.getStatus()==Status.APPROVING) {
			for(ApprovalUser user: request.getSupporters()) {
				if(user.getName().equals(me) && Status.PENDING==user.getStatus()) {
					actions.add(Action.Approve);
					actions.add(Action.Reject);
					break;
				}
			}
			for(ApprovalUser user: request.getApprovers()) {
				if(user.getName().equals(me) && Status.PENDING==user.getStatus()) {
					actions.add(Action.Approve);
					actions.add(Action.Reject);
					break;
				}
			}
			if(request.getRequestor().equals(me)) {
				actions.add(Action.Cancel);
			}
		}
		return actions;
	}

	public Request doAction(int id, Action action) throws ActionDeniedException {
		validate(id, action);
		LOGGER.info("Performing action {} on request {}", action, id);
		Request request = getRequest(id);
		if(Action.Cancel==action) {
			return doCancel(request);
		} else if(Action.Approve==action) {
			return doApprove(request);
		} else if(Action.Reject==action) {
			return doReject(request);
		}
		throw new IllegalArgumentException("Unknown action: " + action);
	}
	
	private Request doReject(Request request) {
		String me = userService.getCurrentUser();
		request.setStatus(Status.REJECTED);
		for(ApprovalUser user: request.getSupporters()) {
			if(me.equals(user.getName())) {
				user.setStatus(Status.REJECTED);
				accessDao.updateRequestSupport(request, user);
			}
		}
		for(ApprovalUser user: request.getApprovers()) {
			if(me.equals(user.getName())) {
				user.setStatus(Status.REJECTED);
				accessDao.updateRequestApprover(request, user);
			}
		}
		accessDao.updateStatus(request);
		sendApprovalMessage(request);
		return request;
	}

	private Request doApprove(Request request) {
		String me = userService.getCurrentUser();
		for(ApprovalUser user: request.getSupporters()) {
			if(me.equals(user.getName())) {
				user.setStatus(Status.APPROVED);
				accessDao.updateRequestSupport(request, user);
			}
		}
		for(ApprovalUser user: request.getApprovers()) {
			if(me.equals(user.getName())) {
				user.setStatus(Status.APPROVED);
				accessDao.updateRequestApprover(request, user);
			}
		}
		//checks if all approved
		boolean approved = true;
		for(ApprovalUser user: request.getSupporters()) {
			approved = approved && user.getStatus()==Status.APPROVED;
		}
		for(ApprovalUser user: request.getApprovers()) {
			approved = approved && user.getStatus()==Status.APPROVED;
		}
		
		if(approved) {
			request.setStatus(Status.APPROVED);
			accessDao.updateStatus(request);
			sendApprovalMessage(request);
			
			//now iff the effective date has passed (due to delays...)
			//immediately change to implementing
			if(isEffective(request)) {
				request.setStatus(Status.IMPLEMENTING);
				accessDao.updateStatus(request);
				sendImplementingMessage(request);
			}
		}
		return request;
	}

	private boolean isEffective(Request request) {
		Date now = new Date();
		return now.after(request.getEffectiveDate());
	}

	private Request doCancel(Request request) {
		request.setStatus(Status.CANCELLED);
		accessDao.updateStatus(request);
		sendApprovalMessage(request);
		return request;
	}

	private void validate(int id, Action action) throws ActionDeniedException {
		Collection<Action> actions = getRequestActions(id);
		if(!actions.contains(action)) {
			throw new ActionDeniedException("You are not allowed to " + action + " this request: SR-" + id);
		}
	}

	private void sendApprovalMessage(Request msg) {
		if(hasKafka())
			kafkaTemplate.send(Topics.APPROVAL, msg);
		else
			LOGGER.warn("No Kafka. Request {} is not broadcast to everyone", msg.getId());
	}
	
	private void sendImplementingMessage(Request msg) {
		if(hasKafka())
			kafkaTemplate.send(Topics.IMPLEMENT, msg);
		else
			LOGGER.warn("No Kafka. Request {} is not broadcast to everyone", msg.getId());
	}
	
	private boolean hasKafka() {
		return activeProfiles.contains("kafka");
	}

}
