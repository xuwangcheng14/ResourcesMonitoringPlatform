package com.dcits.bean;

import javax.management.MBeanServerConnection;

import com.dcits.bean.tomcat.TomcatRealTimeInfo;
import com.dcits.util.DcitsUtil;
import com.dcits.util.tomcat.TomcatJmxUtil;

public class TomcatInfo extends ServerInfo {
	
	private Integer id;
	
	/**
	 * tomcat的指定的连接
	 */
	private MBeanServerConnection connection;
	
	private String realTime;
	
	/**
	 * 是否首次获取信息
	 * 一些基本信息在首次获取之后就不会再次获取
	 * 除非手动指定该值为true
	 */
	private boolean isFirstGetInfo = true;
	
	/**
	 * 连接是否正常
	 * 在连接获取信息出现异常的时候此值保存异常信息
	 */
	private String connectStatus = "true";
	
	/**
	 * 实时状态
	 */
	private TomcatRealTimeInfo info = new TomcatRealTimeInfo();
	
	
	public TomcatInfo(Integer id, String host, String port, String username,
			String password, String mark, String time, String parameters) {
		super();
		this.id = id;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.mark = mark;
		this.time = time;
		this.parameters = parameters;
	}
	
	public TomcatInfo(String host, String port, String username,
			String password, String time, String parameters) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;	
		this.time = time;
		this.parameters = parameters;
	}
	
	public TomcatInfo(ServerInfo  serverInfo) {
		super();
		this.host = serverInfo.host;
		this.port = serverInfo.port;
		this.username = serverInfo.username;
		this.password = serverInfo.password;
		this.mark = serverInfo.mark;
		this.time = DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN);
		this.parameters = serverInfo.parameters;
		this.realHost = serverInfo.realHost;
	}
	
	public TomcatInfo() {
		super();
	}
	
	/**
	 * 初始化Jmx连接
	 */
	public void connect() {
		try {
			TomcatJmxUtil.getConnection(this);
			this.connectStatus = "true";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.connectStatus = "未知";
			if (e.getMessage() != null) {
				this.connectStatus = e.getMessage();
			}			
			e.printStackTrace();
		}
	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public MBeanServerConnection getConnection() {
		return connection;
	}

	public void setConnection(MBeanServerConnection connection) {
		this.connection = connection;
	}

	public String getRealTime() {
		return realTime;
	}

	public void setRealTime(String realTime) {
		this.realTime = realTime;
	}

	public boolean isFirstGetInfo() {
		return isFirstGetInfo;
	}

	public void setFirstGetInfo(boolean isFirstGetInfo) {
		this.isFirstGetInfo = isFirstGetInfo;
	}

	public String getConnectStatus() {
		return connectStatus;
	}

	public void setConnectStatus(String connectStatus) {
		this.connectStatus = connectStatus;
	}

	public TomcatRealTimeInfo getInfo() {
		return info;
	}

	public void setInfo(TomcatRealTimeInfo info) {
		this.info = info;
	}
}
