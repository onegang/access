package org.onegang.access.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.onegang.access.dao.AccessDao;
import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

@Service
public class UsersService {
	
	@Autowired
	private AccessDao accessDao;

	public Collection<User> getUsers() {
		return accessDao.getUsers();
	}
	
	public AccessChange computeChanges(Collection<User> users) {
		Collection<User> originalUsers = accessDao.getUsers(
				users.stream().map(user -> user.getName()).collect(Collectors.toList()));
		
		AccessChange changes = new AccessChange();
		for(User user: users) {
			User originalUser = getUser(user.getName(), originalUsers);
			Collection<String> added = getDiff(originalUser.getRoles(), user.getRoles());
			Collection<String> removed = getDiff(user.getRoles(), originalUser.getRoles());
			if(!added.isEmpty())
				changes.added(user.getName(), added);
			if(!removed.isEmpty())
				changes.removed(user.getName(), removed);
		}
		return changes;
	}

	private Collection<String> getDiff(Collection<String> oldValues, Collection<String> newValues) {
		Collection<String> diff = Sets.newHashSet();
		for(String newValue: newValues) {
			if(!oldValues.contains(newValue)) {
				diff.add(newValue);
			}
		}
		return diff;
	}

	private User getUser(String name, Collection<User> users) {
		for(User user: users) {
			if(user.getName().equals(name))
				return user;
		}
		return null;
	}

}
