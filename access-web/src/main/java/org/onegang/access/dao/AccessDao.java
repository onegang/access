package org.onegang.access.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.AccessChange.Change;
import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Attachment;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.entity.User;
import org.onegang.access.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Repository
public class AccessDao {
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private RequestMapper requestMapper;
	
	@Value("${app-attachments-path}")
	private String attachmentsPath;
	
	
	public void insertRole(String role, String approvingRole) {
		userMapper.insertRole(role);
		if(approvingRole!=null)
			userMapper.insertApprovingRole(role, approvingRole);
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
	
	public User getUser(String username) {
		return userMapper.selectUser(username);
	}
	
	public Collection<User> getUsers(Collection<String> names) {
		return getUsers().stream().filter(user -> {
			return names.contains(user.getName());
		}).collect(Collectors.toList());
	}
	
	public Collection<User> getUsersOfRole(String role) {
		return getUsers().stream().filter(user -> {
			return user.getRoles().contains(role);
		}).collect(Collectors.toList());
	}
	
	public Collection<String> getRoles() {
		return userMapper.selectRoles();
	}
	
	public Collection<ApprovalUser> getApprovingUsers(String role) {
		return userMapper.selectApprovalUsers(role);
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
		
		for(Attachment attachment: request.getAttachments()) {
			attachment.setLink("/api/request/"+id+"/attachments/"+attachment.getFilename());
		}
		
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

	public Collection<Request> getUserAccessRequests(String user) {
		return requestMapper.selectUserRequests(user);
	}
	
	public Collection<Request> getPendingActionRequests(String submitter) {
		return sortByLastModified(requestMapper.selectApprovalRequestsOpened(submitter, Status.PENDING));
	}
	
	public Collection<Request> getOpenedRequests(String submitter) {
		Collection<Request> requests = requestMapper.selectRequests(submitter, Status.APPROVING);
		requests.addAll(requestMapper.selectRequests(submitter, Status.APPROVED));
		requests.addAll(requestMapper.selectRequests(submitter, Status.IMPLEMENTING));
		requests.addAll(requestMapper.selectApprovalRequestsOpened(submitter, Status.PENDING));
		requests = sortByLastModified(requests);
		return requests;
	}

	public Collection<Request> getClosedRequests(String submitter) {
		Collection<Request> requests = requestMapper.selectRequests(submitter, Status.CANCELLED);
		requests.addAll(requestMapper.selectRequests(submitter, Status.REJECTED));
		requests.addAll(requestMapper.selectRequests(submitter, Status.DONE));
		requests.addAll(requestMapper.selectApprovalRequestsClosed(submitter, Status.APPROVED));
		requests.addAll(requestMapper.selectApprovalRequestsClosed(submitter, Status.REJECTED));
		requests = sortByLastModified(requests);
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

	public void uploadAttachment(int requestId, String filename, InputStream input) throws IOException {
		requestMapper.insertRequestAttachment(requestId, filename);
		
		File folder = new File(attachmentsPath, String.valueOf(requestId));
		folder.mkdirs();
		try(OutputStream output = new FileOutputStream(new File(folder, filename))) {
			IOUtils.copy(input, output);
		}
	}
	
	public Resource downloadAttachment(int requestId, String filename) throws IOException {
		Path storage = Paths.get(attachmentsPath+"/"+requestId).toAbsolutePath().normalize();
		Path path = storage.resolve(filename).normalize();
		try {
			Resource resource = new UrlResource(path.toUri());
			if(resource.exists()) {
	            return resource;
	        } else {
	            throw new IOException("File not found for request " + requestId + ": " + filename);
	        }
		} catch (MalformedURLException e) {
			throw new IOException("Unable to download file " + filename + " of request " + requestId, e);
		}
	}
	
	private void insertRequestChange(Change change, int requestId, String type) {
		for(String user: change.getUsernames()) {
			for(String role: change.getRoles()) {
				requestMapper.insertRequestChange(requestId, type, user, role);
			}
		}
	}
	
	private Collection<Request> sortByLastModified(Collection<Request> requests) {
		List<Request> sorted = Lists.newArrayList(Sets.newHashSet(requests)); //make unique
		Collections.sort(sorted, new Comparator<Request>() {
			@Override
			public int compare(Request o1, Request o2) {				
				return o1.getLastModifiedDate().compareTo(o2.getLastModifiedDate());
			}			
		});
		return sorted;
	}

}
