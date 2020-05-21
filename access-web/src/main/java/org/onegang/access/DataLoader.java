package org.onegang.access;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.onegang.access.dao.AccessDao;
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
	
	@Autowired
	private AccessDao accessDao;

	@Override
	public void run(String... args) {
		LOGGER.info("Loading data...");
		try {
			List<String> userRoles = Lists.newArrayList(insertRoles());
			insertUsers(userRoles);
		} catch(Exception ex) {
			LOGGER.error("Unable to load the mockup data fully", ex);
		}
		LOGGER.info("Loaded!");
	}
	
	private Collection<String> insertRoles() throws IOException {
		Collection<String> userRoles = Lists.newArrayList("Administrator", "Analyst", "Researcher", "Developer", "Devops01", "Devops02", "Devops03");
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
			boolean active = id > 1000;
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

}
