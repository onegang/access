package org.onegang.access.entity;

import java.util.Collection;

public class User {

	private String name;
	
	private Collection<String> roles;
	
	private boolean active;
	
	private boolean selected = false; //Only used by UI
	
	
	public User() {
	
	}

	public User(String name, Collection<String> roles, boolean active) {
		super();
		this.name = name;
		this.roles = roles;
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<String> getRoles() {
		return roles;
	}

	public void setRoles(Collection<String> roles) {
		this.roles = roles;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", roles=" + roles + "]";
	}
	
}
