package org.onegang.access.dao;

import java.util.Collection;
import java.util.stream.Collectors;

import org.onegang.access.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccessDao {

	@Autowired
	private UserMapper userMapper;
	
	
	public void insertRole(String role) {
		userMapper.insertRole(role);
	}
	
	public void insertUser(String username, boolean active, Collection<String> roles) {
		User user = new User();
		user.setName(username);
		user.setActive(active);
		userMapper.insertUser(user);
		for(String role: roles)
			userMapper.insertUserRole(username, role);
	}
	
	public Collection<User> getUsers() {
		return userMapper.selectUsers();
	}
	
	public Collection<User> getUsers(Collection<String> names) {
		return getUsers().stream().filter(user -> {
			return names.contains(user.getName());
		}).collect(Collectors.toList());
	}
	
	public Collection<String> getRoles() {
		return userMapper.selectRoles();
	}

}
