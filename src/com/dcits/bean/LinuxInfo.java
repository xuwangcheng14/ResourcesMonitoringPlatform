package com.dcits.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import ch.ethz.ssh2.Connection;

import com.dcits.bean.linux.RealTimeInfo;
import com.dcits.util.DcitsUtil;
import com.dcits.util.ServletUtil;
import com.dcits.util.linux.GetLinuxInfo;
import com.dcits.util.linux.GetLinuxInfoUtil;

/**
 * linux主机信息
 * @author xuwangcheng
 * @version 20170224
 *
 */
public class LinuxInfo extends ServerInfo {
	
	protected Integer id;
	
	/**
	 * 传入自定义的参数
	 * 由于目前只使用了vmstat命令
	 * 所以能传入的参数只有  intervalTime 不传的话默认为3 即每两秒执行一次vmstat
	 */
	protected Map<String, String> options = new HashMap<String, String>();

	/**
	 * 与指定主机建立的连接
	 */
	protected Connection conn;
	
	/**
	 * 该主机每次获取远程返回的结果时
	 * 都会检查该值
	 * 如果为true 将会关闭session和connection
	 */
	protected boolean stopFlag = false;
	
	/**
	 * 主机类型
	 * 例如 Linux HP-UX SUNOS
	 */
	private String uanme = "";
	/**
	 * Cpu信息 cpu核数
	 */
	private String cpuInfo = "0";
	
	/**
	 * 内存信息
	 */
	private String memInfo = "0";
	
	/**
	 * 网卡信息
	 */
	private String[] newWorkInfo;
	
	/**
	 * 可使用的命令
	 */
	private Map<String, String> commandMap;
	
	protected String realTime;
	
	protected String javaHome;
	
	private RealTimeInfo info = new RealTimeInfo();
	
	/**
	 * 启动远程查询
	 * 线程独立
	 * @param context
	 * @throws Exception
	 */
	public void start() {
		final LinuxInfo l = this;
		ServletUtil.execThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while (!stopFlag) {
						Thread.sleep(5000);
						GetLinuxInfo.getRealTimeInfo(l);
					}				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 初始化连接时的报错信息
	 */
	protected String errorInfo = "";
	
	/**
	 * 连接状态
	 */
	
	protected String connectStatus = "true";
	
	public void stop(){
		stopFlag = true;
	}
	
	public void conect() throws Exception  {
		try {
			GetLinuxInfoUtil.getConnection(this);
			this.setInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.errorInfo = e.getMessage();
			throw e;
		}
		
		if (this.conn == null) {
			this.errorInfo = "用户名密码凭证不正确!";
			throw new Exception();			
		}		
	}
	
	public void disconect() {
		GetLinuxInfoUtil.closeConnection(conn);
	}
	
	/**
	 * 获取实时信息
	 */
	public void setInfo() {		
		GetLinuxInfo.getRealTimeInfo(this);
	}
	
	public String jpsShow () {
		String processNames = null;
		if (this.conn != null) {
			try {
				processNames = GetLinuxInfoUtil.execCommand(this.conn, this.javaHome + "/jps|grep -vi jps", 100, null, 0, "");				
				
				if (!processNames.contains("command not found")) {
					return processNames;					
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		return processNames;
	}
	
	@SuppressWarnings("rawtypes")
	public String parseJavaHome () {
		String home = "";
		//优先通过parameters来获取
		if (StringUtils.isNotEmpty(this.parameters)) {
			try {
				Map maps = new ObjectMapper().readValue(this.parameters, Map.class);
				home = maps.get("javaHome").toString();
				
				if (StringUtils.isNotEmpty(home)) {
					this.javaHome = home;
					return home;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
						
		if ("root".equalsIgnoreCase(this.username)) {
			try {
				home = GetLinuxInfoUtil.execCommand(this.conn, "find / -name jps|sed -n '1p'", 1, null, 0, "");
				if (StringUtils.isNotEmpty(home)) {
					home = home.substring(0, home.lastIndexOf("/"));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}		
		this.javaHome = home;
		return home;
	}
	
	public LinuxInfo(String host, String port, String username, String password, String time, String parameters, String tags) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.time = time;
		this.parameters = parameters;
		this.tags = tags;
	}

	public LinuxInfo(Integer id, String port, String host, String username, String password
			, String mark, String time, String parameters, String tags) {
		super();
		this.id = id;
		this.port = port;
		this.host = host;
		this.username = username;
		this.password = password;
		this.mark = mark;
		this.time = time;
		this.parameters = parameters;
		this.tags = tags;
	}
	
	
	public LinuxInfo(ServerInfo serverInfo) {
		super();
		this.serverId = serverInfo.serverId;
		this.host = serverInfo.host;
		this.port = serverInfo.port;
		this.username = serverInfo.username;
		this.password = serverInfo.password;
		this.mark = serverInfo.mark;
		this.parameters = serverInfo.parameters;
		this.time = DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN);
		this.tags = serverInfo.tags;
		this.realHost = serverInfo.realHost;
	}
	
	public LinuxInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setConnectStatus(String connectStatus) {
		this.connectStatus = connectStatus;
	}
	
	public String getConnectStatus() {
		return connectStatus;
	}
	
	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}
	
	public String getJavaHome() {
		return javaHome;
	}
	
	public void setInfo(RealTimeInfo info) {
		this.info = info;
	}
	
	public RealTimeInfo getInfo() {
		return info;
	}
	
	public String getRealTime() {
		return realTime;
	}
	
	public void setRealTime(String realTime) {
		this.realTime = realTime;
	}
	
	public Map<String, String> getCommandMap() {
		return commandMap;
	}

	public void setCommandMap(Map<String, String> commandMap) {
		this.commandMap = commandMap;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public String[] getNewWorkInfo() {
		return newWorkInfo;
	}


	public void setNewWorkInfo(String[] newWorkInfo) {
		this.newWorkInfo = newWorkInfo;
	}	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Connection getConn() {
		return conn;
	}
	
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}

	public boolean isStopFlag() {
		return stopFlag;
	}

	public void setStopFlag(boolean stopFlag) {
		this.stopFlag = stopFlag;
	}


	public String getUanme() {
		return uanme;
	}


	public void setUanme(String uanme) {
		this.uanme = uanme;
	}


	public String getCpuInfo() {
		return cpuInfo;
	}


	public void setCpuInfo(String cpuInfo) {
		this.cpuInfo = cpuInfo;
	}


	public String getMemInfo() {
		return memInfo;
	}


	public void setMemInfo(String memInfo) {
		this.memInfo = memInfo;
	}

	
}
