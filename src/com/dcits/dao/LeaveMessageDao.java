package com.dcits.dao;

import java.util.List;

import com.dcits.bean.util.LeaveMessage;
import com.dcits.mapper.LeaveMessageMapper;

public class LeaveMessageDao {
	
	
	public int saveMessage(LeaveMessage msg) throws Exception {
		LeaveMessageMapper mapper = new LeaveMessageMapper();
		String sql = "insert into LeaveMessage values(null,?,?,?)";
		
		try {
			mapper.execSQL(sql, msg.getUsername(), msg.getContent(), msg.getCreateTime());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getEffectRowCount();
	}
	
	public List<LeaveMessage> list() throws Exception {
		LeaveMessageMapper mapper = new LeaveMessageMapper();
		String sql = "select * from LeaveMessage";
		
		try {
			mapper.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getObjectList();
	}
	
	public int delAll() throws Exception {
		LeaveMessageMapper mapper = new LeaveMessageMapper();
		String sql = "delete from LeaveMessage";
		
		try {
			mapper.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getEffectRowCount();
	}
}
