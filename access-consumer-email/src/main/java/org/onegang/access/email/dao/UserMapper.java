package org.onegang.access.email.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.onegang.access.entity.User;

@Mapper
public interface UserMapper {

	@Select("SELECT * FROM lookup_user WHERE name=#{username}")
	User selectUser(String username);
	
}
