package org.onegang.access.email;


import java.util.List;

import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ImplementationConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ImplementationConsumer.class);
	
	@Value("${app-url}")
	private String appUrl;
	
	@Value("#{T(java.util.Arrays).asList('${app-implementors-email:}')}")
	private List<String> implementatorEmails;
	

	@KafkaListener(
			  topics = "ACCESS_IMPLEMENTATION", 
			  containerFactory = "kafkaListenerContainerFactory")
	public void listen(Request request) {
		LOGGER.debug("Received request: {} ", request);
		if(Status.IMPLEMENTING==request.getStatus()) {
			String msg = request.getManual();
			if(!Utils.isEmpty(msg)) {
				for(String email: implementatorEmails) {
					email(request, email, "Request SR-"+request.getId()+" needs your action: " + msg);
				}
			}
		} else {
			LOGGER.error("Non-approved request routed for implementation: {}", request.getId());
		}
	}
	
	private void email(Request request, String user, String msg) {
		String url = toURL(request);
		LOGGER.info("{}: {}: {}", user, msg, url);
	}

	private String toURL(Request request) {
		return appUrl + "/page/requests/" + request.getId();
	}
	
}
