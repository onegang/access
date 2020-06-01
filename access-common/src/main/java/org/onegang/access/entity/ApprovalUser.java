package org.onegang.access.entity;

public class ApprovalUser {

	private String name;
	
	private Status status;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ApprovalUser [name=" + name + ", status=" + status + "]";
	}
	
}
