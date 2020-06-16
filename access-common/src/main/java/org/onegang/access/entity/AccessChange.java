package org.onegang.access.entity;

import java.util.Collection;

import com.google.common.collect.Lists;

public class AccessChange {

	private Collection<Change> added = Lists.newArrayList();
	
	private Collection<Change> removed = Lists.newArrayList();
	
	
	public Collection<Change> getAdded() {
		return added;
	}

	public Collection<Change> getRemoved() {
		return removed;
	}

	public void added(String user, Collection<String> roles) {
		added(Lists.newArrayList(user), roles);
	}
	
	public void added(String user, String role) {
		added(Lists.newArrayList(user), Lists.newArrayList(role));
	}
	
	public void added(Collection<String> users, String role) {
		added(users, Lists.newArrayList(role));
	}
	
	public void added(Collection<String> users, Collection<String> roles) {
		added.add(new Change(users, roles));
	}
	
	public void removed(String user, Collection<String> roles) {
		removed(Lists.newArrayList(user), roles);
	}
	
	public void removed(String user, String role) {
		removed(Lists.newArrayList(user), Lists.newArrayList(role));
	}
	
	public void removed(Collection<String> users, String role) {
		removed(users, Lists.newArrayList(role));
	}
	
	public void removed(Collection<String> users, Collection<String> roles) {
		removed.add(new Change(users, roles));
	}
	
	
	public static class Change {
	
		private Collection<String> usernames;
	
		private Collection<String> roles;
		
		public Change() {
			super();
		}
		
		public Change(Collection<String> usernames, Collection<String> roles) {
			super();
			this.usernames = usernames;
			this.roles = roles;
		}

		public Collection<String> getUsernames() {
			return usernames;
		}
		
		public void setUsernames(Collection<String> usernames) {
			this.usernames = usernames;
		}

		public Collection<String> getRoles() {
			return roles;
		}

		public void setRoles(Collection<String> roles) {
			this.roles = roles;
		}

	}
}
