package org.onegang.access.dto;

import java.util.Collection;

import org.onegang.access.entity.Request;
import org.onegang.access.entity.User;

public class UserInfo {

	private User user;
	
	private Collection<Request> requests;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Collection<Request> getRequests() {
		return requests;
	}

	public void setRequests(Collection<Request> requests) {
		this.requests = requests;
	}
	
}
