package org.onegang.access.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.AccessChange.Change;
import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.entity.User;
import org.onegang.access.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

@Repository
public class AccessDao {
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private RequestMapper requestMapper;
	
	
	public void insertRole(String role) {
		userMapper.insertRole(role);
	}
	
	public void insertUser(String username, boolean active, Collection<String> roles) {
		User user = new User();
		user.setName(username);
		user.setActive(active);
		userMapper.insertUser(user);
		for(String role: roles)
			userMapper.insertUserRole(username, role);
	}
	
	public Collection<User> getUsers() {
		return userMapper.selectUsers();
	}
	
	public Collection<User> getUsers(Collection<String> names) {
		return getUsers().stream().filter(user -> {
			return names.contains(user.getName());
		}).collect(Collectors.toList());
	}
	
	public Collection<String> getRoles() {
		return userMapper.selectRoles();
	}
	
	public Request addRequest(Request request) {
		request.setLastModifiedDate(new Date());
		requestMapper.insertRequest(request);
		for(User user: request.getUsers()) {
			for(String role: user.getRoles())
				requestMapper.insertRequestUser(request.getId(), user.getName(), role);
		}
		if(request.getSupporters()!=null) {
			for(ApprovalUser user: request.getSupporters()) {
				Status status = user.getStatus()==null ? Status.PENDING: user.getStatus();
				requestMapper.insertRequestSupporter(request.getId(), user.getName(), status);
			}
		}
		if(request.getApprovers()!=null) {
			for(ApprovalUser user: request.getApprovers()) {
				Status status = user.getStatus()==null ? Status.PENDING: user.getStatus();
				requestMapper.insertRequestApprover(request.getId(), user.getName(), status);
			}
		}
		if(request.getChanges().getAdded()!=null) {
			for(Change added: request.getChanges().getAdded()) {
				insertRequestChange(added, request.getId(), "ADD");
			}
		}
		if(request.getChanges().getRemoved()!=null) {
			for(Change removed: request.getChanges().getRemoved()) {
				insertRequestChange(removed, request.getId(), "REMOVE");
			}
		}
		return request;
	}
	
	public Request getRequest(int id) {
		Request request = requestMapper.selectRequest(id);
		AccessChange changes = new AccessChange();
		for(UserRole change: requestMapper.selectRequestChanges(id)) {
			if("ADD".equals(change.getType()))
				changes.added(change.getUser(), change.getRole());
			else if("REMOVE".equals(change.getType()))
				changes.removed(change.getUser(), change.getRole());
		}
		request.setChanges(changes);
		
		Collection<User> users = Lists.newArrayList();
		Collection<UserRole> userRoles = requestMapper.selectRequestEffectiveUserRoles(id);
		Map<String, List<String>> userMap = userRoles.stream().collect(
				Collectors.groupingBy(ur -> ur.getUser(), 
						Collectors.mapping(ur -> ur.getRole(), Collectors.toList())));
		for(String username: userMap.keySet()) {
			User user = new User();
			user.setName(username);
			user.setRoles(userMap.get(username));
			users.add(user);
		}
		request.setUsers(users);
		return request;
	}
	
	public Collection<Request> getPendignActionRequests(String submitter) {
		return requestMapper.selectPendingRequests(submitter, Status.PENDING);
	}
	
	public Collection<Request> getOpenedRequests(String submitter) {
		Collection<Request> requests = requestMapper.selectRequests(submitter, Status.APPROVING);
		requests.addAll(requestMapper.selectRequests(submitter, Status.IMPLEMENTING));
		return requests;
	}
	
	public Collection<Request> getClosedRequests(String submitter) {
		Collection<Request> requests = requestMapper.selectRequests(submitter, Status.CANCELLED);
		requests.addAll(requestMapper.selectRequests(submitter, Status.REJECTED));
		requests.addAll(requestMapper.selectRequests(submitter, Status.DONE));
		return requests;
	}
	
	public void updateRequestSupport(Request request, ApprovalUser user) {
		request.setLastModifiedDate(new Date());
		requestMapper.updateRequestSupporter(request.getId(), user.getName(), user.getStatus());
	}
	
	public void updateRequestApprover(Request request, ApprovalUser user) {
		request.setLastModifiedDate(new Date());
		requestMapper.updateRequestApprover(request.getId(), user.getName(), user.getStatus());
	}

	public void updateStatus(Request request) {
		request.setLastModifiedDate(new Date());
		requestMapper.updateStatus(request.getId(), request.getStatus());
	}
	
	private void insertRequestChange(Change change, int requestId, String type) {
		for(String user: change.getUsernames()) {
			for(String role: change.getRoles()) {
				requestMapper.insertRequestChange(requestId, type, user, role);
			}
		}
	}

}
