package org.onegang.access.entity;

/**
 * Entity used by database
 * 
 * @author TC
 *
 */
public class DBChange {

	private String type;
	
	private String user;
	
	private String role;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
}
