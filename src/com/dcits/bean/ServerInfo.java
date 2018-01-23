package com.dcits.bean;




/**
 * 服务器信息基类
 * @author xuwangcheng
 * @version 20170317
 *
 */
public class ServerInfo {
	
	/**
	 * ip地址:端口
	 * 以及登录用户名密码
	 */
	protected Integer serverId;
	
	protected String host;
	
	/**
	 * 真实的地址，当通过端口转发连接时需要填写
	 */
	protected String realHost;
	
	protected String port;
	protected String username;
	protected String password;
	
	/**
	 * 类型<br>
	 * <br>type = 0 LinuxInfo
	 * <br>type = 1 weblogicInfo
	 * <br>type = 2 Tomcat
	 * <br>type = 3 Jvm
	 * 
	 */
	protected String type;
	
	
	/**
	 * 备注
	 * 如果是通过端口转发的,则再次备注真实监控的主机地址
	 */
	protected String mark;
	
	/**
	 * 最近使用时间
	 */
	protected String time;
	
	/**
	 * 创建时间
	 */
	protected String createTime;
	
	/**
	 * 参数<br>
	 * 不是公用的自定义参数
	 */
	protected String parameters;
	
	/**
	 * 标签
	 */
	protected String tags;

	public ServerInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public String getRealHost() {
		return realHost;
	}
	
	public void setRealHost(String realHost) {
		this.realHost = realHost;
	}
	
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public String getTags() {
		return tags;
	}
	
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	public String getParameters() {
		return parameters;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
