package org.onegang.access.service;

import java.util.Collection;

import org.onegang.access.KafkaConfig;
import org.onegang.access.dao.AccessDao;
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
	

	public Collection<Request> getRequests() {
		return accessDao.getRequests(userService.getCurrentUser());
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
	
	private void sendApprovalMessage(Request msg) {
		if(hasKafka())
			kafkaTemplate.send(KafkaConfig.TOPIC_APPROVAL, msg);
		else
			LOGGER.warn("No Kafka. Request {} is not broadcast to everyone", msg.getId());
	}
	
	private boolean hasKafka() {
		return activeProfiles.contains("kafka");
	}

	public Collection<String> getRequestActions(int id) {
//		if(true)
//			return Lists.newArrayList("Cancel", "Approve", "Reject");
		
		Collection<String> actions = Sets.newHashSet();
		Request request = getRequest(id);
		String me = userService.getCurrentUser();
		for(ApprovalUser user: request.getSupporters()) {
			if(user.getName().equals(me) && Status.PENDING==user.getStatus()) {
				actions.add("Approve");
				actions.add("Reject");
				break;
			}
		}
		for(ApprovalUser user: request.getApprovers()) {
			if(user.getName().equals(me) && Status.PENDING==user.getStatus()) {
				actions.add("Approve");
				actions.add("Reject");
				break;
			}
		}
		if(request.getRequestor().equals(me) && request.getStatus()==Status.APPROVING) {
			actions.add("Cancel");
		}
		return actions;
	}

}
