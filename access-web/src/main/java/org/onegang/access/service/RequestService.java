package org.onegang.access.service;

import java.util.Collection;

import org.onegang.access.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);
	

	public Collection<Request> getRequests() {
		// TODO Auto-generated method stub
		return null;
	}

	public Request submitRequest(Request request) {
		LOGGER.info("SUbmitting request: {}", request.toString());
		return request;
	}

}
