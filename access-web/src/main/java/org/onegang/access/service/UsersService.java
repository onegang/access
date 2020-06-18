package org.onegang.access.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.onegang.access.dao.AccessDao;
import org.onegang.access.dto.SecurityConstants;
import org.onegang.access.dto.UserInfo;
import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.User;
import org.onegang.access.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class UsersService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class);
	
	private static final String MOCK_USER = "Alden Page, 10078";

	@Autowired
	private AccessDao accessDao;
	
	
	public String getCurrentUser() {
		return MOCK_USER; //TODO replace when auth is in place
	}

	public Collection<User> getUsers() {
		return accessDao.getUsers();
	}
	
	public Collection<User> getApprovers() {
		return accessDao.getUsersOfRole(SecurityConstants.APPROVAL_ROLE);
	}
	
	public UserInfo getUser(String username) {
		if("me".equalsIgnoreCase(username)) {
			username = getCurrentUser();
		}
		UserInfo info = new UserInfo();
		info.setUser(accessDao.getUser(username));
		info.setRequests(accessDao.getUserAccessRequests(username));
		return info;
	}
	
	public AccessChange computeChanges(Collection<User> users) {
		LOGGER.debug("Computing changes for users...");
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
		changes = combineChanges(changes);
		return changes;
	}

	public AccessChange combineChanges(AccessChange changes) {
		// do a simple amalgation: if the roles are same, merge the users!
		//then if the users are the same, merge the roles!
		AccessChange userMerged = new AccessChange();
		Map<String, List<AccessChange.Change>> added = changes.getAdded().stream().collect(
				Collectors.groupingBy(change -> change.getRoles().toString()));
		for(List<AccessChange.Change> change: added.values()) {
			if(change.size()==1)
				userMerged.added(Utils.first(change).getUsernames(), Utils.first(change).getRoles());
			else {
				Collection<String> users = change.stream().map(c -> 
					Utils.first(c.getUsernames())).collect(Collectors.toSet());
				userMerged.added(users, Utils.first(change).getRoles());
			}
		}
		
		Map<String, List<AccessChange.Change>> removed = changes.getRemoved().stream().collect(
				Collectors.groupingBy(change -> change.getRoles().toString()));
		for(List<AccessChange.Change> change: removed.values()) {
			if(change.size()==1)
				userMerged.removed(Utils.first(change).getUsernames(), Utils.first(change).getRoles());
			else {
				Collection<String> users = change.stream().map(c -> 
					Utils.first(c.getUsernames())).collect(Collectors.toSet());
				userMerged.removed(users, Utils.first(change).getRoles());
			}
		}
		
		AccessChange rolesMerged = new AccessChange();
		added = userMerged.getAdded().stream().collect(
				Collectors.groupingBy(change -> change.getUsernames().toString()));
		for(List<AccessChange.Change> change: added.values()) {
			if(change.size()==1)
				rolesMerged.added(Utils.first(change).getUsernames(), Utils.first(change).getRoles());
			else {
				Collection<String> roles = change.stream().map(c -> 
					Utils.first(c.getRoles())).collect(Collectors.toSet());
				rolesMerged.added(Utils.first(change).getUsernames(), roles);
			}
		}		
		removed = userMerged.getRemoved().stream().collect(
				Collectors.groupingBy(change -> change.getUsernames().toString()));
		for(List<AccessChange.Change> change: removed.values()) {
			if(change.size()==1)
				rolesMerged.removed(Utils.first(change).getUsernames(), Utils.first(change).getRoles());
			else {
				Collection<String> roles = change.stream().map(c -> 
					Utils.first(c.getRoles())).collect(Collectors.toSet());
				rolesMerged.removed(Utils.first(change).getUsernames(), roles);
			}
		}
		return sort(rolesMerged);
	}
	
	private AccessChange sort(AccessChange changes) {
		for(AccessChange.Change change: changes.getAdded()) {
			change.setUsernames(Utils.sort(change.getUsernames()));
			change.setRoles(Utils.sort(change.getRoles()));	
		}
		for(AccessChange.Change change: changes.getRemoved()) {
			change.setUsernames(Utils.sort(change.getUsernames()));
			change.setRoles(Utils.sort(change.getRoles()));	
		}
		return changes;
	}
	

	private Collection<String> getDiff(Collection<String> oldValues, Collection<String> newValues) {
		List<String> diff = Lists.newArrayList();
		for(String newValue: newValues) {
			if(!oldValues.contains(newValue)) {
				diff.add(newValue);
			}
		}
		Collections.sort(diff);
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
