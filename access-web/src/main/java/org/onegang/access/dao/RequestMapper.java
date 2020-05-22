package org.onegang.access.dao;

import java.util.Collection;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;

@Mapper
public interface RequestMapper {
	
	@Select("SELECT r.id as id, r.status as status, r.requestor as requestor, r.purpose as purpose, "
			+ "r.comments as comments, r.effectiveDate as effectiveDate, r.expiryDate as expiryDate, "
			+ "r.submitDate as submitDate, r.lastModifiedDate as lastModifiedDate, "
			+ "s.user as s_name, s.status as s_status, "
			+ "a.user as a_name, a.status as a_status "
			+ "FROM request r "
			+ "LEFT OUTER JOIN request_supporter s on s.requestId=r.id "
			+ "LEFT OUTER JOIN request_approver a on a.requestId=r.id "
			+ "WHERE r.requestor=#{submitter}")
	@ResultMap("RequestMap")
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
	
	@Insert("INSERT INTO request_supporter(requestId, user, status) VALUES("
			+ "#{requestId}, #{user}, #{status})")
	void insertRequestSupporter(int requestId, String user, Status status);
	
	@Insert("INSERT INTO request_approver(requestId, user, status) VALUES("
			+ "#{requestId}, #{user}, #{status})")
	void insertRequestApprover(int requestId, String user, Status status);
	
}
