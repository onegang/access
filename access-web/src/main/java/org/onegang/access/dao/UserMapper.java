package org.onegang.access.dao;

import java.util.Collection;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.onegang.access.entity.User;

@Mapper
public interface UserMapper {

	@Select("SELECT u.name AS name, u.active AS active, ur.role AS roles "
			+ "FROM lookup_user u "
			+ "LEFT OUTER JOIN user_role ur ON u.name=ur.user")
	@ResultMap("UserMap")
	Collection<User> selectUsers();
	
	@Select("SELECT u.name AS name, u.active AS active, ur.role AS roles "
			+ "FROM lookup_user u "
			+ "LEFT OUTER JOIN user_role ur ON u.name=ur.user "
			+ "WHERE u.name=#{username}")
	@ResultMap("UserMap")
	User selectUser(String username);
	
	@Select("SELECT * FROM lookup_role")
	Collection<String> selectRoles();
	
	@Insert("INSERT INTO lookup_user(name,active) VALUES(#{name}, #{active})")
	void insertUser(User user);
	
	@Insert("INSERT INTO lookup_role(name) VALUES(#{role})")
	void insertRole(String role);
	
	@Insert("INSERT INTO user_role(user,role) VALUES(#{user}, #{role})")
	void insertUserRole(String user, String role);
	
}
