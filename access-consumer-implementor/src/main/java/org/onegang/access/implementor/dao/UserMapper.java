package org.onegang.access.implementor.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
	
	@Insert("INSERT INTO user_role(user,role) VALUES(#{user}, #{role})")
	void insertUserRole(String user, String role);
	
	@Delete("DELETE FROM user_role WHERE user=#{user} AND role=#{role}")
	void deleteUserRole(String user, String role);
	
}
