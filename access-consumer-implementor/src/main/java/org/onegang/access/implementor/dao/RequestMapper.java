package org.onegang.access.implementor.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.onegang.access.entity.Status;

@Mapper
public interface RequestMapper {
	
	@Update("UPDATE request SET status=#{status} WHERE id=#{id}")
	void updateStatus(int id, Status status);
	
}
