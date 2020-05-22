package org.onegang.access.dao;

import java.util.Collection;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.onegang.access.entity.Request;

@Mapper
public interface RequestMapper {

	@Select("SELECT * FROM request WHERE requestor=#{submitter}")
	Collection<Request> selectRequests(String submitter);
	
	@Insert("INSERT INTO request(requestor, status, effectiveDate, expiryDate, submitDate, "
			+ "lastModifiedDate, purpose, comments) VALUES("
			+ "#{requestor}, #{status}, #{effectiveDate}, #{expiryDate}, CURRENT_TIMESTAMP(), "
			+ "CURRENT_TIMESTAMP(), #{purpose}, #{comments})")
	@Options(useGeneratedKeys=true, keyProperty="id")
	void insertRequest(Request request);
	
	@Insert("INSERT INTO request_user(requestId, user, role) VALUES("
			+ "#{requestId}, #{user}, #{role})")
	void insertRequestUser(int requestId, String user, String role);
	
	@Insert("INSERT INTO request_change(requestId, type, user, role) VALUES("
			+ "#{requestId}, #{type}, #{user}, #{role})")
	void insertRequestChange(int requestId, String type, String user, String role);
	
}
