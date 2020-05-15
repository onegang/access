package org.onegang.access.service;

import java.util.Collection;

import org.onegang.access.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
	
	@Autowired
	private UsersDao usersDao;

	public Collection<User> getUsers() {
		return usersDao.getUsers();
	}

}
