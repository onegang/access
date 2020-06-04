package org.onegang.access.implementor;


import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.AccessChange.Change;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.implementor.dao.RequestMapper;
import org.onegang.access.implementor.dao.UserMapper;
import org.onegang.access.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ImplementationConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ImplementationConsumer.class);

	@Autowired
	private RequestMapper requestMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private KafkaTemplate<String, Request> kafkaTemplate;

	@KafkaListener(
			  topics = "ACCESS_IMPLEMENTATION", 
			  containerFactory = "kafkaListenerContainerFactory")
	public void listen(Request request) {
		LOGGER.debug("Received request: {} ", request);
		if(Status.IMPLEMENTING==request.getStatus()) {
			if(isManual(request))
				LOGGER.info("Skipping manual implementation for request {}", request.getId());
			else
				implementChanges(request);
		} else {
			LOGGER.error("Non-implementing request routed for implementation: {}", request.getId());
		}
	}
	
	private void implementChanges(Request request) {
		LOGGER.info("Implementing request {}...", request.getId());
		AccessChange changes = request.getChanges();
		for(Change added: changes.getAdded()) {
			for(String user: added.getUsernames())
				for(String role: added.getRoles())
					userMapper.insertUserRole(user, role);
		}
		for(Change removed: changes.getRemoved()) {
			for(String user: removed.getUsernames())
				for(String role: removed.getRoles())
					userMapper.deleteUserRole(user, role);
		}
		request.setStatus(Status.DONE);
		requestMapper.updateStatus(request.getId(), Status.DONE);
		LOGGER.info("Sending DONE to approval workflow: {}", request);
		kafkaTemplate.send("ACCESS_APPROVAL", request);
		LOGGER.info("Implemented request {}", request.getId());
	}

	private boolean isManual(Request request) {
		return !Utils.isEmpty(request.getManual());
	}

}
