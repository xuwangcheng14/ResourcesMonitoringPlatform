package com.dcits.bean;

import org.apache.commons.lang.StringUtils;

import com.dcits.bean.jvm.JvmRealTimeInfo;
import com.dcits.util.jvm.GetJvmInfo;
import com.dcits.util.linux.GetLinuxInfoUtil;

public class JvmInfo extends LinuxInfo {
	
	private JvmRealTimeInfo info = new JvmRealTimeInfo();
	
	private String pid;
	
	private String processName;

	private String weblogicPort;

	public JvmInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JvmInfo(LinuxInfo info) {
		super(info);
		this.javaHome = info.javaHome;
	}
	
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getProcessName() {
		return processName;
	}
	
	public JvmRealTimeInfo getJvmInfo() {
		return info;
	}

	public void setJvmInfo(JvmRealTimeInfo info) {
		this.info = info;
	}	

	public void setPid(String pid) {
		this.pid = pid;
	}
	
	public void setPid() throws Exception {
		
		if (StringUtils.isEmpty(this.weblogicPort)) {
			throw new Exception("请先删除然后手动创建连接!");
		}
		
		String execCommand = "netstat -anp|grep java|grep LISTEN|grep " + this.weblogicPort + "|head -1";
		try {
			this.conect();
			pid = GetLinuxInfoUtil.execCommand(this.conn, execCommand, 1, null, 0, "");			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new Exception("获取该weblogic对应的pid信息时发生了错误:" + e.getMessage() + "\n(" + this.errorInfo + ")");
		}
		
		
		if (StringUtils.isEmpty(pid)) {
			throw new Exception("没有获取到该weblogic对应的pid进程信息!");
		}
		String[] strs = pid.trim().split("(\\s)+");
		pid = (strs[strs.length - 1]);
		pid = pid.substring(0, pid.indexOf("/"));		
	}
	
	public String getPid() {
		return pid;
	}
	
	public void setWeblogicPort(String weblogicPort) {
		this.weblogicPort = weblogicPort;
	}
	
	public String getWeblogicPort() {
		return weblogicPort;
	}	
	
	public void setInfo() {
		try {
			GetJvmInfo.getRealTimeInfo(this);
			this.connectStatus = "true";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.connectStatus = "获取信息时发生了错误：" + e.getMessage();
		}
	}
	
	public void conectJvm () throws Exception {
		try {
			if (this.conn == null) {
				GetLinuxInfoUtil.getConnection(this);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.errorInfo = "在尝试连接主机时发生了错误:" + e.getMessage();
			throw e;
		}
		
		if (this.conn == null) {
			this.errorInfo = "用户名密码凭证不正确!";
			throw new Exception();			
		}
		
		//检测命令是否可用或者pid的java进程是否存在
		
		String returnStr = GetLinuxInfoUtil.execCommand(this.conn, this.javaHome + "/jstat -gcutil " + this.pid, 1, null, 0, "");
		
		if (StringUtils.isEmpty(returnStr)) {
			this.errorInfo = "执行jstat命令发生错误：请检查JAVA_HOME、主机环境以及PID是否正确.[execCommand=" + this.javaHome + "/jstat -gcutil " + this.pid + "]";
			throw new Exception("执行jstat失败!");	
		}		
		/*if (returnStr.endsWith("command not found\n")) {
			this.errorInfo = "jstat命令不可用,请检查主机环境!";
			throw new Exception();	
		}
		
		if (returnStr.endsWith(this.pid + " not found\n")) {
			this.errorInfo = "不存在pid为" + this.pid + "的java进程,请重新更换pid值!";
			throw new Exception();	
		}
		
		if (returnStr.endsWith("No such file or directory\n")) {
			this.errorInfo = "JAVA_HOME获取有误，请手动填写正确的路径然后重试!";
			throw new Exception();	
		}*/
	}
	
	public String stackShow () {
		String str = null;
		if (this.conn != null) {
			try {
				str = GetLinuxInfoUtil.execCommand(this.conn, this.javaHome + "/jstack " + this.pid, 9999999, null, 2, "");
				
				return str;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}						
		}
		
		return str;
	}
	
}
