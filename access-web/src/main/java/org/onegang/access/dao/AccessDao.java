package org.onegang.access.dao;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.onegang.access.entity.User;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

@Repository
public class AccessDao {

	private static final String[] NAMES = {"Jon Stark",
			"Anthony Hubbard",
			"Trevor Turner",
			"Allan Mcknight",
			"Irwin Pruitt",
			"Brandi Mendoza",
			"Maryann Bright",
			"Bernard Cohen",
			"Clare Ewing",
			"Johnnie Santana",
			"Sara Frey",
			"Sharron Terry",
			"Aline Snow",
			"Noah Fowler",
			"Stacy Sims",
			"Elisa Mathews",
			"Dena Meyer",
			"Gordon Pace",
			"Lesley Phillips",
			"Sammy Barr",
			"Reed Bond",
			"Mel Villegas",
			"Marguerite Golden",
			"Tommy Levy",
			"Caroline Porter",
			"Sheila Garcia",
			"Kerry Hahn",
			"Berta Meadows",
			"Bill Norman",
			"Constance Schroeder",
			"Grady Lamb",
			"Reba Hall",
			"Andres Espinoza",
			"Brandie Hudson",
			"Kathie Bennett",
			"Lillie Callahan",
			"Muriel Hayes",
			"Stacey Nixon",
			"Erma Cunningham",
			"Antonio Fisher",
			"Barbra Hart",
			"Leann Maddox",
			"Christina Cameron",
			"Ambrose Barrett",
			"Chrystal Ruiz",
			"Galen Osborne",
			"Brittany Neal",
			"Tracey Higgins",
			"Jackie Guzman",
			"Martin Dalton"};
	
	private static final String[] ROLES = {"Administrator", "Analyst", "Researcher", "Developer", "Devops01", "Devops02", "Devops03"};
	
	private List<String> roles;
	
	public Collection<User> getUsers() {
		List<User> users = Lists.newArrayList();
		for(String user: NAMES) {
			int id = getID(user);
			String name = user + ", " + id;
			Collection<String> roles = getRoles(user);
			boolean active = id > 1000;
			users.add(new User(name, roles, active));
		}
		return users;
	}
	
	public Collection<String> getRoles() throws IOException {
		if(roles==null) {
			roles = Lists.newArrayList(ROLES);
			roles.addAll(FileUtils.readLines(new File(
					getClass().getClassLoader().getResource("iam-roles.txt").getFile()), "utf-8"));
			Collections.sort(roles);
		}
		return roles;
	}
	
	private Collection<String> getRoles(String name) {
		Collection<String> roles = Lists.newArrayList();
		for(int i=0; i<ROLES.length; i++) {
			if(name.length()%(i+3)==0 ||
				name.hashCode()%(i+3)==0) {
				roles.add(ROLES[i]);
			}
		}
		return roles;
	}

	private int getID(String name) {
		return Math.abs(name.hashCode() / 100000);
	}

}
