package org.onegang.access.service;

import java.io.IOException;
import java.util.Collection;

import org.onegang.access.dao.AccessDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LookupService {
	
	@Autowired
	private AccessDao accessDao;

	public Collection<String> getRoles() throws IOException {
		return accessDao.getRoles();
	}

}
