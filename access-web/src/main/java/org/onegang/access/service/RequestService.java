package org.onegang.access.service;

import java.util.Collection;

import org.onegang.access.dao.AccessDao;
import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);
	
	private static final String MOCK_USER = "Alden Page, 10078";
	
	@Autowired
	private AccessDao accessDao;
	
	@Autowired
	private UsersService userService;
	

	public Collection<Request> getRequests() {
		return accessDao.getRequests(MOCK_USER);
	}

	public Request submitRequest(Request request) {
		LOGGER.info("Submitting request: {}", request.toString());
		AccessChange changes = userService.computeChanges(request.getUsers());
		
		if(request.getPurpose()==null)
			request.setPurpose("New request");
		request.setStatus(Status.APPROVING);
		request.setRequestor(MOCK_USER); //TODO replace when auth is in place
		request.setChanges(changes);
		request = accessDao.addRequest(request);
		return request;
	}

}
