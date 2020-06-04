package org.onegang.access.implementor;


import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ImplementationConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ImplementationConsumer.class);


	@KafkaListener(
			  topics = "ACCESS_IMPLEMENTATION", 
			  containerFactory = "kafkaListenerContainerFactory")
	public void listen(Request request) {
		LOGGER.info("Received request: {} ", request);
		if(Status.IMPLEMENTING==request.getStatus()) {
			if(isManual(request)) {
				LOGGER.info("Skipping manual implementation for request {}", request.getId());
			} else {
				//TODO
				implementChanges(request);
			}
		} else {
			LOGGER.error("Non-approved request routed for implementation: {}", request.getId());
		}
	}
	
	private void implementChanges(Request request) {
		LOGGER.info("Implementing request {}", request.getId());
		//TODO
	}

	private boolean isManual(Request request) {
		return !Utils.isEmpty(request.getManual());
	}

}
