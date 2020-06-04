package org.onegang.access.implementor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.entity.User;
import org.onegang.access.entity.UserRole;
import org.onegang.access.implementor.dao.RequestMapper;
import org.onegang.access.kafka.Topics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

//monitors and applies the implementation
@Component
public class ImplementationStarter {
	
	@Autowired
	private RequestMapper requestMapper;
	
	@Autowired
	private KafkaTemplate<String, Request> kafkaTemplate;

	@Scheduled(cron = "5 0 0 * * ?") //runs every 12.05am
	public void startImplementation() {
		for(Request request: requestMapper.selectEffectiveApprovedRequest()) {
			request.setStatus(Status.IMPLEMENTING);
			requestMapper.updateStatus(request.getId(), Status.IMPLEMENTING);
			
			Request fullRequest = getRequest(request.getId());
			//TODO Issue-27: apply changes to current user access
			kafkaTemplate.send(Topics.IMPLEMENT, fullRequest);
		}
	}
	
	private Request getRequest(int id) {
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
	
}
