package org.onegang.access.dao;

import java.util.Collection;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.entity.UserRole;

@Mapper
public interface RequestMapper {
	
	@Select("SELECT r.id as id, r.status as status, r.requestor as requestor, r.purpose as purpose, "
			+ "r.comments as comments, r.effectiveDate as effectiveDate, r.expiryDate as expiryDate, "
			+ "r.submitDate as submitDate, r.lastModifiedDate as lastModifiedDate, r.manual as manual, "
			+ "s.user as s_name, s.status as s_status, "
			+ "a.user as a_name, a.status as a_status "
			+ "FROM request r "
			+ "LEFT OUTER JOIN request_supporter s on s.requestId=r.id "
			+ "LEFT OUTER JOIN request_approver a on a.requestId=r.id "
			+ "WHERE ((s.user=#{submitter} AND s.status=#{status}) OR (a.user=#{submitter} AND a.status=#{status})) "
			+ "AND ((r.status='APPROVING') OR (r.status='APPROVED') OR (r.status='IMPLEMENTING'))"
			+ "ORDER BY r.lastModifiedDate desc")
	@ResultMap("RequestMap")
	Collection<Request> selectApprovalRequestsOpened(String submitter, Status status);
	
	@Select("SELECT r.id as id, r.status as status, r.requestor as requestor, r.purpose as purpose, "
			+ "r.comments as comments, r.effectiveDate as effectiveDate, r.expiryDate as expiryDate, "
			+ "r.submitDate as submitDate, r.lastModifiedDate as lastModifiedDate, r.manual as manual, "
			+ "s.user as s_name, s.status as s_status, "
			+ "a.user as a_name, a.status as a_status "
			+ "FROM request r "
			+ "LEFT OUTER JOIN request_supporter s on s.requestId=r.id "
			+ "LEFT OUTER JOIN request_approver a on a.requestId=r.id "
			+ "WHERE ((s.user=#{submitter} AND s.status=#{status}) OR (a.user=#{submitter} AND a.status=#{status})) "
			+ "AND ((r.status='REJECTED') OR (r.status='CANCELLED') OR (r.status='DONE'))"
			+ "ORDER BY r.lastModifiedDate desc")
	@ResultMap("RequestMap")
	Collection<Request> selectApprovalRequestsClosed(String submitter, Status status);
	
	@Select("SELECT r.id as id, r.status as status, r.requestor as requestor, r.purpose as purpose, "
			+ "r.comments as comments, r.effectiveDate as effectiveDate, r.expiryDate as expiryDate, "
			+ "r.submitDate as submitDate, r.lastModifiedDate as lastModifiedDate, r.manual as manual, "
			+ "s.user as s_name, s.status as s_status, "
			+ "a.user as a_name, a.status as a_status "
			+ "FROM request r "
			+ "LEFT OUTER JOIN request_supporter s on s.requestId=r.id "
			+ "LEFT OUTER JOIN request_approver a on a.requestId=r.id "
			+ "WHERE r.requestor=#{submitter} AND r.status=#{status} "
			+ "ORDER BY r.lastModifiedDate desc")
	@ResultMap("RequestMap")
	Collection<Request> selectRequests(String submitter, Status status);
	
	@Select("SELECT r.id as id, r.status as status, r.requestor as requestor, r.purpose as purpose, "
			+ "r.comments as comments, r.effectiveDate as effectiveDate, r.expiryDate as expiryDate, "
			+ "r.submitDate as submitDate, r.lastModifiedDate as lastModifiedDate, r.manual as manual, "
			+ "s.user as s_name, s.status as s_status, "
			+ "a.user as a_name, a.status as a_status "
			+ "FROM request r "
			+ "LEFT OUTER JOIN request_supporter s on s.requestId=r.id "
			+ "LEFT OUTER JOIN request_approver a on a.requestId=r.id "
			+ "LEFT OUTER JOIN request_change rc on rc.requestId=r.id "
			+ "WHERE rc.user=#{user} "
			+ "ORDER BY r.lastModifiedDate desc")
	@ResultMap("RequestMap")
	Collection<Request> selectUserRequests(String user);
	
	@Select("SELECT r.id as id, r.status as status, r.requestor as requestor, r.purpose as purpose, "
			+ "r.comments as comments, r.effectiveDate as effectiveDate, r.expiryDate as expiryDate, "
			+ "r.submitDate as submitDate, r.lastModifiedDate as lastModifiedDate, r.manual as manual, "
			+ "s.user as s_name, s.status as s_status, "
			+ "a.user as a_name, a.status as a_status, "
			+ "attach.filename as attach_filename "
			+ "FROM request r "
			+ "LEFT OUTER JOIN request_supporter s on s.requestId=r.id "
			+ "LEFT OUTER JOIN request_approver a on a.requestId=r.id "
			+ "LEFT OUTER JOIN request_attachment attach on attach.requestId=r.id "
			+ "WHERE r.id=#{id}")
	@ResultMap("RequestMap")
	Request selectRequest(int id);
	
	@Select("SELECT * FROM request_change WHERE requestId=#{id}")
	Collection<UserRole> selectRequestChanges(int id);
	
	@Select("SELECT * FROM request_user WHERE requestId=#{id}")
	Collection<UserRole> selectRequestEffectiveUserRoles(int id);
	
	@Insert("INSERT INTO request(requestor, status, effectiveDate, expiryDate, submitDate, "
			+ "lastModifiedDate, purpose, comments, manual) "
			+ "VALUES(#{requestor}, #{status}, #{effectiveDate}, #{expiryDate}, CURRENT_TIMESTAMP(), "
			+ "CURRENT_TIMESTAMP(), #{purpose}, #{comments}, #{manual})")
	@Options(useGeneratedKeys=true, keyProperty="id")
	void insertRequest(Request request);
	
	@Insert("INSERT INTO request_user(requestId, user, role) "
			+ "VALUES(#{requestId}, #{user}, #{role})")
	void insertRequestUser(int requestId, String user, String role);
	
	@Insert("INSERT INTO request_change(requestId, type, user, role) "
			+ "VALUES(#{requestId}, #{type}, #{user}, #{role})")
	void insertRequestChange(int requestId, String type, String user, String role);
	
	@Insert("INSERT INTO request_supporter(requestId, user, status) "
			+ "VALUES(#{requestId}, #{user}, #{status})")
	void insertRequestSupporter(int requestId, String user, Status status);
	
	@Insert("INSERT INTO request_approver(requestId, user, status) "
			+ "VALUES(#{requestId}, #{user}, #{status})")
	void insertRequestApprover(int requestId, String user, Status status);
	
	@Insert("INSERT INTO request_attachment(requestId, filename) "
			+ "VALUES(#{requestId}, #{filename})")
	void insertRequestAttachment(int requestId, String filename);
	
	@Update("UPDATE request_supporter SET status=#{status} "
			+ "WHERE requestId=#{requestId} AND user=#{user}")
	void updateRequestSupporter(int requestId, String user, Status status);
	
	@Update("UPDATE request_approver SET status=#{status} "
			+ "WHERE requestId=#{requestId} AND user=#{user}")
	void updateRequestApprover(int requestId, String user, Status status);

	@Update("UPDATE request SET status=#{status} "
			+ "WHERE id=#{id}")
	void updateStatus(int id, Status status);
	
}
