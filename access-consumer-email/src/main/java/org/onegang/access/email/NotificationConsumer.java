package org.onegang.access.email;


import java.util.Collection;
import java.util.List;

import org.onegang.access.email.dao.UserMapper;
import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.entity.User;
import org.onegang.access.kafka.Topics;
import org.onegang.access.utils.Utils;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Value("${email-host}")
	private String emailHost;
	
	@Value("${email-port}")
	private int emailPort;
	
	@Value("${email-system}")
	private String systemEmail;
	
	@Value("${email-system-password}")
	private String systemPassword;
	
	@Autowired
	private UserMapper userMapper;
	

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
				email(request, email, msg, true);
			}
		}
	}

	private void informImplemented(Request request) {
		email(request, request.getRequestor(), "Your request SR-"+request.getId()+ " is implemented.", false);
		//also inform the affected users
		Collection<String> users = Sets.newHashSet();
		request.getUsers().stream().forEach(user -> {
			users.add(user.getName());
		});
		for(String user: users) {
			email(request, user, "Your access is changed by Request SR-"+request.getId(), false);
		}
	}

	private void informApproving(Request request) {
		for(ApprovalUser user: request.getSupporters()) {
			if(Status.PENDING==user.getStatus()) {
				email(request, user.getName(), "There is a request SR-"+request.getId()+" pending your support", true);
			}
		}
		for(ApprovalUser user: request.getApprovers()) {
			if(Status.PENDING==user.getStatus()) {
				email(request, user.getName(), "There is a request SR-"+request.getId()+" pending your approval", true);
			}
		}
	}
	
	private void informCancelled(Request request) {
		for(ApprovalUser user: request.getSupporters()) {
			email(request, user.getName(), "Request SR-"+request.getId()+" is cancelled", false);
		}
		for(ApprovalUser user: request.getApprovers()) {
			email(request, user.getName(), "Request SR-"+request.getId()+" is cancelled", false);
		}
	}
	
	private void informApproved(Request request) {
		email(request, request.getRequestor(), "Your request SR-"+request.getId()+" is approved. Pending implementation...", false);
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
		email(request, request.getRequestor(), "Your request SR-"+request.getId()+" is rejected by " + rejectedUser, false);
	}
	
	private void email(Request request, String user, String msg, boolean replyBot) {
		String url = toURL(request);
		String emailAddress = getEmail(user);
		LOGGER.info("User {}: {}: {}", user, msg, url);
		String subject = EmailConstants.EMAIL_PREFIX+"[SR-"+request.getId()+"] "+msg;
		String content = user+",\n\n"+msg+
				"\n\nRequest: "+request.getPurpose()+
				"\nEffective date: "+Utils.formatDate(request.getEffectiveDate(), "yyyy-MM-dd")+
				"\n\nView the request here: "+url;
		
		if(replyBot) {
			content += "\n\n============="
					+"\nReply OK, Approved, Done, Implmented to directly act on this request.";
		}
		
		Email email = EmailBuilder.startingBlank()
			.from("Access System", systemEmail)
			.to(user, emailAddress)
			.withSubject(subject)
		    .withPlainText(content)
		    .buildEmail();
		Mailer mailer = MailerBuilder
		          .withSMTPServer(emailHost, emailPort, systemEmail, systemPassword)
		          .buildMailer();
		mailer.sendMail(email);
		LOGGER.info("Emailed to {}", emailAddress);
	}

	private String toURL(Request request) {
		return appUrl + "/page/requests/" + request.getId();
	}
	
	private String getEmail(String username) {
		User user = userMapper.selectUserByName(username);
		if(Utils.isEmpty(user.getEmail())) {
			return systemEmail; //XXX for testing
		}
		return user.getEmail();
	}
	
}
