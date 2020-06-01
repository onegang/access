package org.onegang.access.email;


import org.onegang.access.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailConsumer.class);

	@KafkaListener(
			  topics = "ACCESS_APPROVAL", 
			  containerFactory = "kafkaListenerContainerFactory")
	public void listen(Request message) {
		LOGGER.info("Received Messasge: {} ", message);
	}
	
}
