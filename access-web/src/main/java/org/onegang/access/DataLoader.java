package org.onegang.access;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.onegang.access.dao.AccessDao;
import org.onegang.access.dto.SecurityConstants;
import org.onegang.access.entity.AccessChange;
import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.entity.User;
import org.onegang.access.service.UsersService;
import org.onegang.access.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	private static final String ROLE_APPROVERS = "Approvers";
	
	private static final String ROLE_DEVOPS_APPROVERS = "Devops Approvers";
	
	@Autowired
	private AccessDao accessDao;
	
	@Autowired
	private UsersService usersService;
	
	@Value("${app-attachments-path}")
	private String attachmentsPath;

	@Override
	public void run(String... args) {
		LOGGER.info("Loading data...");
		try {
			List<String> userRoles = Lists.newArrayList(insertRoles());
			insertUsers(userRoles);
			insertRequests();
			insertMockUserRequests();
			
			deleteAttachments();
		} catch(Exception ex) {
			LOGGER.error("Unable to load the mockup data fully", ex);
		}
		LOGGER.info("Loaded!");
	}

	private void deleteAttachments() throws IOException {
		FileUtils.deleteDirectory(new File(attachmentsPath));
	}

	private Collection<String> insertRoles() throws IOException {
		Collection<String> userRoles = Lists.newArrayList("Administrator", "Analyst", "Researcher", "Developer", "Devops01", "Devops02", "Devops03", "Manager", "Chief Developer", "Snr Developer");
		Collection<String> roles = Sets.newHashSet(userRoles);
		roles.addAll(FileUtils.readLines(new File(
				getClass().getClassLoader().getResource("mock/iam-roles.txt").getFile()), "utf-8"));
		accessDao.insertRole(SecurityConstants.APPROVAL_ROLE, SecurityConstants.APPROVAL_ROLE);
		accessDao.insertRole(ROLE_APPROVERS, ROLE_APPROVERS);
		accessDao.insertRole(ROLE_DEVOPS_APPROVERS, ROLE_APPROVERS);
		for(String role: roles) {
			if(role.contains("AWS") || role.contains("Devops"))
				accessDao.insertRole(role, ROLE_DEVOPS_APPROVERS);
			else
				accessDao.insertRole(role, ROLE_APPROVERS);
		}
		return userRoles;
	}
	
	private void insertUsers(List<String> allRoles) throws IOException {
		List<String> usernames = FileUtils.readLines(new File(
				getClass().getClassLoader().getResource("mock/users.txt").getFile()), "utf-8");
		for(String user: usernames) {
			int id = getID(user);
			String name = user + ", " + id;
			Collection<String> roles = getRoles(user, allRoles);
			if("Armani Dominguez, 17850".equals(name) ||
				"Britney Vasquez, 3236".equals(name))
				roles.add(ROLE_APPROVERS);
			if("Amina Burch, 19822".equals(name))
				roles.add(ROLE_DEVOPS_APPROVERS);
			if("Allie Hickman, 4439".equals(name) ||
				"Amiyah Norton, 4021".equals(name) ||
				"Ariella Solis, 3629".equals(name) ||
				"Brittany Neal, 9964".equals(name) ||
				"Elisa Mathews, 3771".equals(name) ||
				"Jadon Barnett, 2302".equals(name))
				roles.add(SecurityConstants.APPROVAL_ROLE);
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
		request.setEffectiveDate(Utils.addDays(new Date(), -50));
		request.setRequestor(MOCK_USER);
		request.setSupporters(toApprovalUser("Bianca Key, 18691", Status.APPROVED));
		request.setUsers(Lists.newArrayList(
			new User("Amiah Bridges, 4586", Lists.newArrayList("Developer", "Administrator", "AmazonECS_FullAccess"), true),
			new User("Nathalie Patterson, 11639", Lists.newArrayList("Developer", "Administrator", "Devops03", "AmazonECS_FullAccess"), true)));
		request.setChanges(usersService.computeChanges(request.getUsers()));
		accessDao.addRequest(request);
		
		request  = new Request();
		request.setStatus(Status.REJECTED);
		request.setPurpose("Grant Devops02 to Adolfo Brewer");
		request.setEffectiveDate(Utils.addDays(new Date(), -10));
		request.setRequestor(MOCK_USER);
		request.setSupporters(toApprovalUser("Bianca Key, 18691", Status.APPROVED));
		request.setApprovers(toApprovalUser("Carla Kane, 14760", Status.REJECTED));
		request.setUsers(Lists.newArrayList(
			new User("Adolfo Brewer, 16202", Lists.newArrayList("Devops02"), true)));
		request.setChanges(usersService.computeChanges(request.getUsers()));
		accessDao.addRequest(request);
		
		request  = new Request();
		request.setStatus(Status.APPROVING);
		request.setPurpose("Revoke Manager from Cade Haley due to transfer");
		request.setEffectiveDate(Utils.addDays(new Date(), 7));
		request.setRequestor(MOCK_USER);
		request.setSupporters(toApprovalUser("Bianca Key, 18691", Status.APPROVED));
		request.setApprovers(toApprovalUser("Carla Kane, 14760", Status.PENDING));
		request.setUsers(Lists.newArrayList(
			new User("Cade Haley, 8943", Lists.newArrayList("Researcher"), true)));
		request.setChanges(usersService.computeChanges(request.getUsers()));
		accessDao.addRequest(request);
		
		request  = new Request();
		request.setStatus(Status.APPROVING);
		request.setPurpose("Grant Auditor to Ahmed Humphrey");
		request.setManual("Please grant Auditor rights to him.");
		request.setEffectiveDate(Utils.addDays(new Date(), 7));
		request.setRequestor(MOCK_USER);
		request.setSupporters(toApprovalUser("Bianca Key, 18691", Status.PENDING));
		request.setUsers(Lists.newArrayList(
			new User("Ahmed Humphrey, 15697", Lists.newArrayList("Devops01", "Administrator"), true)));
		request.setChanges(usersService.computeChanges(request.getUsers()));
		accessDao.addRequest(request);
		
		request  = new Request();
		request.setStatus(Status.APPROVING);
		request.setPurpose("Grant Researcher to Adolfo Brewer");
		request.setEffectiveDate(Utils.addDays(new Date(), 10));
		request.setRequestor("Adolfo Brewer, 16202");
		request.setSupporters(toApprovalUser(MOCK_USER, Status.PENDING));
		request.setUsers(Lists.newArrayList(
			new User("Adolfo Brewer, 16202", Lists.newArrayList("Researcher"), true)));
		request.setChanges(usersService.computeChanges(request.getUsers()));
		accessDao.addRequest(request);
		
		request  = new Request();
		request.setStatus(Status.APPROVING);
		request.setPurpose("Revoke Administrator from Alana Humphrey");
		request.setEffectiveDate(Utils.addDays(new Date(), 10));
		request.setRequestor("Adolfo Brewer, 16202");
		request.setSupporters(toApprovalUser(MOCK_USER, Status.PENDING));
		request.setApprovers(toApprovalUser("Bill Norman, 4890", Status.PENDING));
		request.setUsers(Lists.newArrayList(
			new User("Alana Humphrey, 4439", Lists.newArrayList("Devops01"), true)));
		request.setChanges(usersService.computeChanges(request.getUsers()));
		accessDao.addRequest(request);
		
		request  = new Request();
		request.setStatus(Status.APPROVING);
		request.setPurpose("Grant Devops03 to Aiyana Barber");
		request.setEffectiveDate(Utils.addDays(new Date(), 10));
		request.setRequestor("Aiyana Barber, 8769");
		request.setSupporters(toApprovalUser(MOCK_USER, Status.PENDING));
		request.setApprovers(toApprovalUser(MOCK_USER, Status.PENDING));
		request.setUsers(Lists.newArrayList(
			new User("Aiyana Barber, 8769", Lists.newArrayList("Devops03"), true)));
		request.setChanges(usersService.computeChanges(request.getUsers()));
		accessDao.addRequest(request);
	}
	
	private void insertMockUserRequests() {		
		Request request  = new Request();
		request.setStatus(Status.DONE);
		request.setPurpose("Revoke Devops for Alden");
		request.setEffectiveDate(Utils.addDays(new Date(), -112));
		request.setRequestor("Adriel Lamb, 19732");
		request.setApprovers(toApprovalUser("Amina Burch, 19822", Status.APPROVED));
		request.setUsers(Lists.newArrayList(
			new User(MOCK_USER, Lists.newArrayList("Researcher"), true)));
		AccessChange changes = new AccessChange();
		changes.removed(MOCK_USER, Lists.newArrayList("Devops01", "Devops03"));
		request.setChanges(changes);
		accessDao.addRequest(request);
		
		request  = new Request();
		request.setStatus(Status.DONE);
		request.setPurpose("Grant Manager to Alden Page");
		request.setEffectiveDate(Utils.addDays(new Date(), -100));
		request.setRequestor(MOCK_USER);
		request.setApprovers(toApprovalUser("Amina Burch, 19822", Status.APPROVED));
		request.setUsers(Lists.newArrayList(
			new User(MOCK_USER, Lists.newArrayList("Manager", "Researcher"), true)));
		changes = new AccessChange();
		changes.added(MOCK_USER, Lists.newArrayList("Manager"));
		request.setChanges(changes);
		accessDao.addRequest(request);
	}
	
	private Collection<ApprovalUser> toApprovalUser(String name, Status status) {
		ApprovalUser user = new ApprovalUser();
		user.setName(name);
		user.setStatus(status);
		return Lists.newArrayList(user);
	}

}
