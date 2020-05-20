package org.onegang.access.web;

import java.util.Collection;

import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.User;
import org.onegang.access.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@PostMapping("/changes")
	public AccessChange getChanges(@RequestBody Collection<User> users) {
		return usersService.computeChanges(users);
	}
	
}
