package org.onegang.access.dao;

import java.util.Collection;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.onegang.access.entity.User;

@Mapper
public interface UserMapper {

	@Select("SELECT u.name AS name, u.active AS active, ur.role AS roles FROM lookup_user u LEFT OUTER JOIN user_role ur ON u.name=ur.user")
	@ResultMap("UserMap")
	Collection<User> selectUsers();
	
}
