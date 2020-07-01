package org.onegang.access.email.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;

@Mapper
public interface RequestMapper {
	
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
