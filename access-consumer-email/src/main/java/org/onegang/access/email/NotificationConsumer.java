package org.onegang.access.email;


import java.util.Collection;
import java.util.List;

import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.kafka.Topics;
import org.onegang.access.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

@Component
public class NotificationConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);
	
	@Value("${app-url}")
	private String appUrl;
	
	@Value("#{T(java.util.Arrays).asList('${app-implementors-email:}')}")
	private List<String> implementatorEmails;

	@KafkaListener(
			  topics = Topics.APPROVAL, 
			  containerFactory = "kafkaListenerContainerFactory")
	public void listen(Request request) {
		LOGGER.debug("Received request: {} ", request);
		if(Status.APPROVING==request.getStatus()) {
			informApproving(request);
		} else if(Status.APPROVED==request.getStatus()) {
			informApproved(request);
			informManual(request);
		} else if(Status.REJECTED==request.getStatus()) {
			informRejected(request);
		} else if(Status.CANCELLED==request.getStatus()) {
			informCancelled(request);
		} else if(Status.DONE==request.getStatus()) { 
			informImplemented(request);
		} else {
			LOGGER.error("Unknown status {} for request {}", request.getStatus(), request.getId());
		}
	}
	
	private void informManual(Request request) {
		String manual = request.getManual();
		if(!Utils.isEmpty(manual)) {
			String msg = "Request SR-"+request.getId()+" needs your action ";
			msg += "(effective date: "+ Utils.formatDate(request.getEffectiveDate(), "yyyy-MM-dd") + "): " + manual;
			for(String email: implementatorEmails) {
				email(request, email, msg);
			}
		}
	}

	private void informImplemented(Request request) {
		email(request, request.getRequestor(), "Your request SR-"+request.getId()+ " is implemented.");
		//also inform the affected users
		Collection<String> users = Sets.newHashSet();
		request.getUsers().stream().forEach(user -> {
			users.add(user.getName());
		});
		for(String user: users) {
			email(request, user, "Your access is changed by Request SR-"+request.getId());
		}
	}

	private void informApproving(Request request) {
		for(ApprovalUser user: request.getSupporters()) {
			if(Status.PENDING==user.getStatus()) {
				email(request, user.getName(), "There is a request SR-"+request.getId()+" pending your support");
			}
		}
		for(ApprovalUser user: request.getApprovers()) {
			if(Status.PENDING==user.getStatus()) {
				email(request, user.getName(), "There is a request SR-"+request.getId()+" pending your approval");
			}
		}
	}
	
	private void informCancelled(Request request) {
		for(ApprovalUser user: request.getSupporters()) {
			email(request, user.getName(), "Request SR-"+request.getId()+" is cancelled");
		}
		for(ApprovalUser user: request.getApprovers()) {
			email(request, user.getName(), "Request SR-"+request.getId()+" is cancelled");
		}
	}
	
	private void informApproved(Request request) {
		email(request, request.getRequestor(), "Your request SR-"+request.getId()+" is approved. Pending implementation...");
	}
	
	private void informRejected(Request request) {
		String rejectedUser = "";
		for(ApprovalUser user: request.getSupporters()) {
			if(Status.REJECTED==user.getStatus()) {
				rejectedUser = user.getName();
				break;
			}
		}
		for(ApprovalUser user: request.getApprovers()) {
			if(Status.REJECTED==user.getStatus()) {
				rejectedUser = user.getName();
				break;
			}
		}
		email(request, request.getRequestor(), "Your request SR-"+request.getId()+" is rejected by " + rejectedUser);
	}
	
	private void email(Request request, String user, String msg) {
		String url = toURL(request);
		LOGGER.info("User {}: {}: {}", user, msg, url);
	}

	private String toURL(Request request) {
		return appUrl + "/page/requests/" + request.getId();
	}
	
}
