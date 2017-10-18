package com.dcits.bean.util;

public class LeaveMessage {
	
	private Integer id;
	private String username;
	private String content;	
	private String createTime;
	
	public LeaveMessage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LeaveMessage(String username, String content, String createTime) {
		super();
		this.username = username;
		this.content = content;
		this.createTime = createTime;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
		
}
