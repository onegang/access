package org.onegang.access.email.dao;

import java.util.Date;

import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.kafka.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

//TODO this is a pure copy paste from access-web RequestService...
//think about how to reuse it.
@Component
public class RequestService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);
	
	@Autowired
	private RequestMapper requestMapper;
	
	@Autowired
	private KafkaTemplate<String, Request> kafkaTemplate;
	
	
	public Request doReject(int id, String username) {
		LOGGER.info("Rejecting request {} by user {}", id, username);
		Request request = requestMapper.selectRequest(id);
		request.setStatus(Status.REJECTED);
		for(ApprovalUser user: request.getSupporters()) {
			if(username.equals(user.getName())) {
				user.setStatus(Status.REJECTED);
				updateRequestSupport(request, user);
			}
		}
		for(ApprovalUser user: request.getApprovers()) {
			if(username.equals(user.getName())) {
				user.setStatus(Status.REJECTED);
				updateRequestApprover(request, user);
			}
		}
		updateStatus(request);
		sendApprovalMessage(request);
		return request;
	}

	public Request doApprove(int id, String username) {
		LOGGER.info("Approving request {} by user {}", id, username);
		Request request = requestMapper.selectRequest(id);
		for(ApprovalUser user: request.getSupporters()) {
			if(username.equals(user.getName())) {
				user.setStatus(Status.APPROVED);
				updateRequestSupport(request, user);
			}
		}
		for(ApprovalUser user: request.getApprovers()) {
			if(username.equals(user.getName())) {
				user.setStatus(Status.APPROVED);
				updateRequestApprover(request, user);
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
			updateStatus(request);
			sendApprovalMessage(request);
			
			//now iff the effective date has passed (due to delays...)
			//immediately change to implementing
			if(isEffective(request)) {
				request.setStatus(Status.IMPLEMENTING);
				updateStatus(request);
				sendImplementingMessage(request); //TODO need to grab the changes
			}
		}
		return request;
	}
	
	private boolean isEffective(Request request) {
		Date now = new Date();
		return now.after(request.getEffectiveDate());
	}
	
	private void updateRequestSupport(Request request, ApprovalUser user) {
		request.setLastModifiedDate(new Date());
		requestMapper.updateRequestSupporter(request.getId(), user.getName(), user.getStatus());
	}
	
	private void updateRequestApprover(Request request, ApprovalUser user) {
		request.setLastModifiedDate(new Date());
		requestMapper.updateRequestApprover(request.getId(), user.getName(), user.getStatus());
	}
	
	private void updateStatus(Request request) {
		request.setLastModifiedDate(new Date());
		requestMapper.updateStatus(request.getId(), request.getStatus());
	}
	
	private void sendApprovalMessage(Request msg) {
		kafkaTemplate.send(Topics.APPROVAL, msg);
	}
	
	private void sendImplementingMessage(Request msg) {
		kafkaTemplate.send(Topics.IMPLEMENT, msg);
	}
	
}
