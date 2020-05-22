package org.onegang.access.service;

import java.util.Collection;

import org.onegang.access.dao.AccessDao;
import org.onegang.access.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);
	
	private static final String MOCK_USER = "Alden Page";
	
	@Autowired
	private AccessDao accessDao;
	

	public Collection<Request> getRequests() {
		return accessDao.getRequests(MOCK_USER);
	}

	public Request submitRequest(Request request) {
		LOGGER.info("Submitting request: {}", request.toString());
		request.setRequestor(MOCK_USER); //TODO replace when auth is in place
		accessDao.addRequest(request);
		return request;
	}

}
