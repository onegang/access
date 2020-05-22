package org.onegang.access.dao;

import java.util.Collection;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.onegang.access.entity.Request;

@Mapper
public interface RequestMapper {

	@Select("SELECT * FROM request WHERE requestor=#{submitter}")
	Collection<Request> selectRequests(String submitter);
	
	@Insert("INSERT INTO request(requestor, effectiveDate, expiryDate, submitDate, purpose, comments) VALUES(#{requestor}, #{effectiveDate}, #{expiryDate}, CURRENT_TIMESTAMP(), #{purpose}, #{comments})")
	void insertRequest(Request request);
	
}
