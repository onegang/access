package org.onegang.access.implementor.dao;

import java.util.Collection;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.onegang.access.entity.Request;
import org.onegang.access.entity.Status;
import org.onegang.access.entity.UserRole;

@Mapper
public interface RequestMapper {
	
	@Update("UPDATE request SET status=#{status} WHERE id=#{id}")
	void updateStatus(int id, Status status);
	
	@Select("SELECT  * FROM request WHERE status='APPROVED' AND effectiveDate<= CURRENT_TIMESTAMP()")
	Collection<Request> selectEffectiveApprovedRequest();
	
	@Select("SELECT r.id as id, r.status as status, r.requestor as requestor, r.purpose as purpose, "
			+ "r.comments as comments, r.effectiveDate as effectiveDate, r.expiryDate as expiryDate, "
			+ "r.submitDate as submitDate, r.lastModifiedDate as lastModifiedDate, r.manual as manual, "
			+ "s.user as s_name, s.status as s_status, "
			+ "a.user as a_name, a.status as a_status "
			+ "FROM request r "
			+ "LEFT OUTER JOIN request_supporter s on s.requestId=r.id "
			+ "LEFT OUTER JOIN request_approver a on a.requestId=r.id "
			+ "WHERE r.id=#{id}")
	@ResultMap("RequestMap")
	Request selectRequest(int id);
	
	@Select("SELECT * FROM request_change WHERE requestId=#{id}")
	Collection<UserRole> selectRequestChanges(int id);
	
	@Select("SELECT * FROM request_user WHERE requestId=#{id}")
	Collection<UserRole> selectRequestEffectiveUserRoles(int id);
	
}
