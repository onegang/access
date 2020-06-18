package org.onegang.access.web;

import java.util.Collection;

import org.onegang.access.dto.UserInfo;
import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.User;
import org.onegang.access.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UsersController {

	@Autowired
	private UsersService usersService;
	
	@GetMapping
	public Collection<User> getUsers() {
		return usersService.getUsers();
	}
	
	@GetMapping("/{username}")
	public UserInfo getUser(@PathVariable String username) {
		return usersService.getUser(username);
	}
	
	@GetMapping("/approvers")
	public Collection<User> getApprovers() {
		return usersService.getApprovers();
	}
	
	@PostMapping("/changes")
	public AccessChange getChanges(@RequestBody Collection<User> users) {
		return usersService.computeChanges(users);
	}
	
}
