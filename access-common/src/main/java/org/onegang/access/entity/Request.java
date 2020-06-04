package org.onegang.access.entity;

import java.util.Collection;
import java.util.Date;

public class Request {

	private int id;
	
	private Status status;
	
	private String requestor;
	
	private String purpose;
	
	private String comments;
	
	private String manual;
	
	private Date effectiveDate;
	
	private Date expiryDate;
	
	private Date submitDate;
	
	private Date lastModifiedDate;
	
	private Collection<ApprovalUser> supporters;
	
	private Collection<ApprovalUser> approvers;
	
	private Collection<User> users;
	
	private AccessChange changes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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

	public String getManual() {
		return manual;
	}

	public void setManual(String manual) {
		this.manual = manual;
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

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Collection<ApprovalUser> getSupporters() {
		return supporters;
	}

	public void setSupporters(Collection<ApprovalUser> supporters) {
		this.supporters = supporters;
	}

	public Collection<ApprovalUser> getApprovers() {
		return approvers;
	}

	public void setApprovers(Collection<ApprovalUser> approvers) {
		this.approvers = approvers;
	}

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}

	public AccessChange getChanges() {
		return changes;
	}

	public void setChanges(AccessChange changes) {
		this.changes = changes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Request [id=" + id + ", status=" + status + ", requestor=" + requestor + ", purpose=" + purpose
				+ ", comments=" + comments + ", manual=" + manual + ", effectiveDate=" + effectiveDate + ", expiryDate="
				+ expiryDate + ", submitDate=" + submitDate + ", lastModifiedDate=" + lastModifiedDate + ", supporters="
				+ supporters + ", approvers=" + approvers + ", users=" + users + ", changes=" + changes + "]";
	}

}
