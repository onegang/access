package org.onegang.access.email;


import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailConsumer.class);
	
	@Value("${app-url}")
	private String appUrl;

	@KafkaListener(
			  topics = "ACCESS_APPROVAL", 
			  containerFactory = "kafkaListenerContainerFactory")
	public void listen(Request request) {
		LOGGER.info("Received request: {} ", request);
		if(Status.APPROVING==request.getStatus()) {
			informApproving(request);
		} else if(Status.APPROVED==request.getStatus()) {
			informApproved(request);
		} else if(Status.REJECTED==request.getStatus()) {
			informRejected(request);
		} else if(Status.CANCELLED==request.getStatus()) {
			informCancelled(request);
		} else {
			LOGGER.error("Unknown status {} for request {}", request.getStatus(), request.getId());
		}
	}

	private void informApproving(Request request) {
		for(ApprovalUser user: request.getSupporters()) {
			if(Status.PENDING==user.getStatus()) {
				email(request, user.getName(), "There is a request pending your support");
			}
		}
		for(ApprovalUser user: request.getApprovers()) {
			if(Status.PENDING==user.getStatus()) {
				email(request, user.getName(), "There is a request pending your approval");
			}
		}
	}
	
	private void informCancelled(Request request) {
		for(ApprovalUser user: request.getSupporters()) {
			email(request, user.getName(), "Request is cancelled");
		}
		for(ApprovalUser user: request.getSupporters()) {
			email(request, user.getName(), "Request is cancelled");
		}
	}
	
	private void informApproved(Request request) {
		email(request, request.getRequestor(), "Request is approved. Pending implementation...");
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
		email(request, request.getRequestor(), "Request is rejected by " + rejectedUser);
	}
	
	private void email(Request request, String user, String msg) {
		String url = toURL(request);
		LOGGER.info("User {}: {}: {}", user, msg, url);
	}

	private String toURL(Request request) {
		return appUrl + "/requests/" + request.getId();
	}
	
}
