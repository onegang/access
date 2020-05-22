package org.onegang.access;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.onegang.access.dao.AccessDao;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.entity.User;
import org.onegang.access.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
	
	private static final String MOCK_USER = "Alden Page, 10078";
	
	@Autowired
	private AccessDao accessDao;
	
	@Autowired
	private UsersService usersService;

	@Override
	public void run(String... args) {
		LOGGER.info("Loading data...");
		try {
			List<String> userRoles = Lists.newArrayList(insertRoles());
			insertUsers(userRoles);
			insertRequests();
		} catch(Exception ex) {
			LOGGER.error("Unable to load the mockup data fully", ex);
		}
		LOGGER.info("Loaded!");
	}

	private Collection<String> insertRoles() throws IOException {
		Collection<String> userRoles = Lists.newArrayList("Administrator", "Analyst", "Researcher", "Developer", "Devops01", "Devops02", "Devops03", "Manager", "Chief Developer", "Snr Developer");
		Collection<String> roles = Sets.newHashSet(userRoles);
		roles.addAll(FileUtils.readLines(new File(
				getClass().getClassLoader().getResource("mock/iam-roles.txt").getFile()), "utf-8"));
		for(String role: roles)
			accessDao.insertRole(role);
		return userRoles;
	}
	
	private void insertUsers(List<String> allRoles) throws IOException {
		List<String> usernames = FileUtils.readLines(new File(
				getClass().getClassLoader().getResource("mock/users.txt").getFile()), "utf-8");
		for(String user: usernames) {
			int id = getID(user);
			String name = user + ", " + id;
			Collection<String> roles = getRoles(user, allRoles);
			boolean active = id > 2300;
			accessDao.insertUser(name, active, roles);
		}
	}
	
	private int getID(String name) {
		return Math.abs(name.hashCode() / 100000);
	}
	
	private Collection<String> getRoles(String name, List<String> allRoles) {
		Collection<String> roles = Lists.newArrayList();
		for(int i=0; i<allRoles.size(); i++) {
			if(name.length()%(i+3)==0 ||
				name.hashCode()%(i+3)==0) {
				roles.add(allRoles.get(i));
			}
		}
		return roles;
	}
	
	private void insertRequests() {
		Request request  = new Request();
		request.setStatus(Status.DONE);
		request.setPurpose("Grant ECS to developer");
		request.setEffectiveDate(new Date());
		request.setRequestor(MOCK_USER);
		request.setUsers(Lists.newArrayList(
			new User("Amiah Bridges, 4586", Lists.newArrayList("Developer", "Administrator", "AmazonECS_FullAccess"), true),
			new User("Nathalie Patterson, 11639", Lists.newArrayList("Developer", "Administrator", "Devops03", "AmazonECS_FullAccess"), true)));
		request.setChanges(usersService.computeChanges(request.getUsers()));
		accessDao.addRequest(request);
		
		Request request2  = new Request();
		request2.setStatus(Status.APPROVING);
		request2.setPurpose("Revoke Manager from Cade Haley due to transfer");
		request2.setEffectiveDate(new Date());
		request2.setRequestor(MOCK_USER);
		request2.setUsers(Lists.newArrayList(
			new User("Cade Haley, 8943", Lists.newArrayList("Researcher"), true)));
		request2.setChanges(usersService.computeChanges(request2.getUsers()));
		accessDao.addRequest(request2);
	}

}
