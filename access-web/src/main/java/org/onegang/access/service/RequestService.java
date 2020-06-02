package org.onegang.access.service;

import java.util.Collection;

import org.onegang.access.ActionDeniedException;
import org.onegang.access.KafkaConfig;
import org.onegang.access.dao.AccessDao;
import org.onegang.access.dto.Action;
import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
			return accessDao.getPendignActionRequests(userService.getCurrentUser());
		else
			throw new IllegalArgumentException("Unknown filter found: " + filter);
	}

	public Request getRequest(int id) {
		Request request = accessDao.getRequest(id);
		request.setChanges(userService.combineChanges(request.getChanges()));
		return request;
	}

	public Request submitRequest(Request request) {
		LOGGER.info("Submitting request: {}", request.toString());
		AccessChange changes = userService.computeChanges(request.getUsers());
		
		if(request.getPurpose()==null)
			request.setPurpose("New request");
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
	
	public Collection<Action> getRequestActions(int id) {
//		if(true)
//			return Lists.newArrayList("Cancel", "Approve", "Reject");
		
		Collection<Action> actions = Sets.newHashSet();
		Request request = getRequest(id);
		String me = userService.getCurrentUser();
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
		if(request.getRequestor().equals(me) && request.getStatus()==Status.APPROVING) {
			actions.add(Action.Cancel);
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
			request.setStatus(Status.IMPLEMENTING);
			accessDao.updateStatus(request);
			sendApprovalMessage(request);
		}
		return request;
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
			kafkaTemplate.send(KafkaConfig.TOPIC_APPROVAL, msg);
		else
			LOGGER.warn("No Kafka. Request {} is not broadcast to everyone", msg.getId());
	}
	
	private boolean hasKafka() {
		return activeProfiles.contains("kafka");
	}

}
