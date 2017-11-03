package com.dcits.bean;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.dcits.bean.weblogic.WeblogicRealTimeInfo;
import com.dcits.util.DcitsUtil;
import com.dcits.util.weblogic.WeblogicJmxUtil;

/**
 * weblogic的基本信息
 * 包括实时资源信息
 * @author xuwangcheng
 * @version 20170310
 *
 */
public class WeblogicInfo extends ServerInfo{
	
	/**
	 * weblogic的指定的连接
	 */
	private MBeanServerConnection connection;
	
	/**
	 * 运行时状态
	 */
	private ObjectName serverRuntime;
	
	/**
	 * jvm状态
	 */
	private ObjectName jvmRuntime;
	
	/**
	 * jdbc状态
	 */
	private ObjectName jdbcRuntime;
	
	/**
	 * 线程状态
	 */
	private ObjectName threadPoolRuntime;
	
	
	private Integer id;

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
	 * 对应weblogic的实时信息
	 */
	private WeblogicRealTimeInfo info = new WeblogicRealTimeInfo();
	
	private String realTime;
	
	private Integer jvmId;
	
	public WeblogicInfo(Integer id, String host, String port, String username,
			String password, String mark, String time, String parameters, String tags) {
		super();
		this.id = id;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.mark = mark;
		this.time = time;
		this.parameters = parameters;
		this.tags = tags;
	}
	

	public WeblogicInfo(String host, String port, String username,
			String password, String time, String parameters, String tags) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;	
		this.time = time;
		this.parameters = parameters;
		this.tags = tags;
	}

	public WeblogicInfo(ServerInfo serverInfo) {
		super();
		this.serverId = serverInfo.serverId;
		this.host = serverInfo.host;
		this.port = serverInfo.port;
		this.username = serverInfo.username;
		this.password = serverInfo.password;
		this.mark = serverInfo.mark;
		this.time = DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN);
		this.parameters = serverInfo.parameters;
		this.tags = serverInfo.tags;
	}
	
	public WeblogicInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 初始化Jmx连接
	 */
	public void connect() {
		try {
			WeblogicJmxUtil.getConnection(this);
			this.connectStatus = "true";
			this.setInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.connectStatus = "未知";
			if (e.getMessage() != null) {
				this.connectStatus = e.getMessage();
			}			
			e.printStackTrace();
		}
	}

	
	public Integer getJvmId() {
		return jvmId;
	}
	
	public void setJvmId(Integer jvmId) {
		this.jvmId = jvmId;
	}
	
	public void setJdbcRuntime(ObjectName jdbcRuntime) {
		this.jdbcRuntime = jdbcRuntime;
	}
	
	public ObjectName getJdbcRuntime() {
		return jdbcRuntime;
	}
	
	public String getRealTime() {
		return realTime;
	}


	public void setRealTime(String realTime) {
		this.realTime = realTime;
	}


	public String getConnectStatus() {
		return connectStatus;
	}


	public void setConnectStatus(String connectStatus) {
		this.connectStatus = connectStatus;
	}


	public ObjectName getServerRuntime() {
		return serverRuntime;
	}


	public void setServerRuntime(ObjectName serverRuntime) {
		this.serverRuntime = serverRuntime;
	}

	public ObjectName getThreadPoolRuntime() {
		return threadPoolRuntime;
	}


	public void setThreadPoolRuntime(ObjectName threadPoolRuntime) {
		this.threadPoolRuntime = threadPoolRuntime;
	}


	public ObjectName getJvmRuntime() {
		return jvmRuntime;
	}


	public void setJvmRuntime(ObjectName jvmRuntime) {
		this.jvmRuntime = jvmRuntime;
	}


	public MBeanServerConnection getConnection() {
		return connection;
	}


	public void setConnection(MBeanServerConnection connection) {
		this.connection = connection;
	}


	public WeblogicRealTimeInfo getInfo() {
		return info;
	}

	/**
	 * 获取实时状态
	 * @param info
	 * @throws IOException 
	 * @throws ReflectionException 
	 * @throws MBeanException 
	 * @throws InstanceNotFoundException 
	 * @throws AttributeNotFoundException 
	 */
	public void setInfo() {
		try {
			WeblogicJmxUtil.getWeblogicInfo(this);
			this.connectStatus = "true";
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			this.connectStatus = e.getMessage();
			e.printStackTrace();
		}
	}
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public boolean isFirstGetInfo() {
		return isFirstGetInfo;
	}

	public void setFirstGetInfo(boolean isFirstGetInfo) {
		this.isFirstGetInfo = isFirstGetInfo;
	}
	
	
	
}
