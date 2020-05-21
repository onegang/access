package org.onegang.access.entity;

import java.util.Collection;
import java.util.Date;

public class Request {

	private String id;
	
	private String requestor;
	
	private String comments;
	
	private Date effectiveDate;
	
	private Date expiryDate;
	
	private Date submitDate;
	
	private Collection<User> users;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Request [id=" + id + ", requestor=" + requestor + ", comments=" + comments + ", effectiveDate="
				+ effectiveDate + ", expiryDate=" + expiryDate + ", submitDate=" + submitDate + ", users=" + users
				+ "]";
	}
	
}
