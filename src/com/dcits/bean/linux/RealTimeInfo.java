package com.dcits.bean.linux;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * 主机的实时信息
 * @author xuwangcheng
 * @version 20170224
 *
 */
public class RealTimeInfo {
	/**
	 * 空闲cpu
	 */
	private String freeCpu;
	/**
	 * 空闲内存
	 */
	private String freeMem;
	
	/**
	 * io等待
	 */
	private String ioWait;
	
	/**
	 * tcp端口占用情况
	 */
	private Map<String, String> tcpInfo = new HashMap<String, String>();
	
	/**
	 * io情况
	 */
	private Map<String, String> ioInfo = new HashMap<String, String>();
	
	/**
	 * 网络带宽情况
	 */
	private Map<String, String> networkInfo = new HashMap<String, String>();
	
	/**
	 * 磁盘文件系统情况
	 */
	private Map<String, String> diskInfo = new HashMap<String, String>();
	
	/**
	 * 当前主机上运行的weblogic实例
	 */
	private String weblogicServerCount = "0"; 
	
	private String time;
	
	

	public RealTimeInfo(String freeCpu, String freeMem, String time) {
		super();
		this.freeCpu = freeCpu;
		this.freeMem = freeMem;
		this.time = time;				
	}

	public RealTimeInfo() {
		super();
		// TODO Auto-generated constructor stub
		tcpInfo.put("ESTABLISHED", "0");
		tcpInfo.put("LISTEN", "0");
		tcpInfo.put("CLOSE_WAIT", "0");
		tcpInfo.put("TIME_WAIT", "0");
		
		networkInfo.put("rx", "0");
		networkInfo.put("tx", "0");
		
		diskInfo.put("rootDisk", "0");
		diskInfo.put("userDisk", "0");
	}

	
	public void setIoWait(String ioWait) {
		this.ioWait = ioWait;
	}
	
	public String getIoWait() {
		return ioWait;
	}
	
	public Map<String, String> getDiskInfo() {
		return diskInfo;
	}

	public void setDiskInfo(Map<String, String> diskInfo) {
		this.diskInfo = diskInfo;
	}

	public String getWeblogicServerCount() {
		return weblogicServerCount;
	}

	public void setWeblogicServerCount(String weblogicServerCount) {
		this.weblogicServerCount = weblogicServerCount;
	}

	public String getFreeCpu() {
		return freeCpu;
	}

	public void setFreeCpu(String freeCpu) {
		this.freeCpu = freeCpu;
	}

	public String getFreeMem() {
		return freeMem;
	}

	public void setFreeMem(String freeMem) {
		this.freeMem = freeMem;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public Map<String, String> getTcpInfo() {
		return tcpInfo;
	}

	public void setTcpInfo(Map<String, String> tcpInfo) {
		this.tcpInfo = tcpInfo;
	}

	public Map<String, String> getIoInfo() {
		return ioInfo;
	}

	public void setIoInfo(Map<String, String> ioInfo) {
		this.ioInfo = ioInfo;
	}

	public Map<String, String> getNetworkInfo() {
		return networkInfo;
	}

	public void setNetworkInfo(Map<String, String> networkInfo) {
		this.networkInfo = networkInfo;
	}	
}
