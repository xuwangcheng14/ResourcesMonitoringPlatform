package com.dcits.bean.tomcat;

import java.util.HashMap;
import java.util.Map;

public class TomcatRealTimeInfo {
	
	/**
	 * 最大堆内存
	 */
	public static final String HEAP_MEMORY_MAX = "tomcat_maxHeapMemory";
	/**
	 * 当前使用堆内存
	 */
	public static final String HEAP_MEMORY_CURRENT = "tomcat_currentHeapMemory";
	/**
	 * 当前使用堆内存百分比
	 */	
	public static final String HEAP_MEMORY_CURRENT_PERCENT = "tomcat_currentPercentHeapMemory";
	
	/**
	 * 持久堆大小
	 */
	public static final String PERMGEN_MEMORY_USED = "tomcat_permgenMemoryUsed";
	/**
	 * 持久堆使用百分比
	 */
	public static final String PERMGEN_MEMORY_USED_PERCENT = "tomcat_permgenMemoryUsedPercent";
	
	/**
	 * 最大可用会话数。-1代表无限制
	 */
	public static final String MAX_ACTIVE_SESSION_TOTAL_COUNT = "tomcat_maxActiveSession";
	
	/**
	 * 当前会话数
	 */
	public static final String ACTIVE_SESSION_TOTAL_COUNT = "tomcat_activeSession";
	
	/**
	 * 活跃会话数 
	 */
	public static final String COUNTER_SESSION_TOTAL_COUNT = "tomcat_counterSession";
	
	/**
	 * 最大线程数
	 */
	public static final String MAX_THREAD_COUNT = "tomcat_maxThread";
	
	/**
	 * 当前线程数 
	 */
	public static final String CURRENT_THREAD_COUNT = "tomcat_currentThread";
	
	/**
	 * 当前繁忙线程数 
	 */
	public static final String CURRENT_THREAD_BUSY_COUNT = "tomcat_currentBusyThread";
		
	/**
	 * 标记时间
	 */
	private String time;
	
	/**
	 * 动态获取
	 * jvm信息
	 */	
	private Map<String, String> jvmInfo = new HashMap<String, String>();
	
	/**
	 * 动态获取
	 * session会话信息
	 */	
	private Map<String, String> sessionInfo = new HashMap<String, String>();
	
	/**
	 * 动态获取
	 * threadpool线程信息 
	 */	
	private Map<String, String> threadPoolInfo = new HashMap<String, String>();
	
	/**
	 * 启动时间
	 */
	private String startTime;
	
	/**
	 * 工作时长
	 */
	private String upTime;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Map<String, String> getJvmInfo() {
		return jvmInfo;
	}

	public void setJvmInfo(Map<String, String> jvmInfo) {
		this.jvmInfo = jvmInfo;
	}

	public Map<String, String> getSessionInfo() {
		return sessionInfo;
	}

	public void setSessionInfo(Map<String, String> sessionInfo) {
		this.sessionInfo = sessionInfo;
	}

	public Map<String, String> getThreadPoolInfo() {
		return threadPoolInfo;
	}

	public void setThreadPoolInfo(Map<String, String> threadPoolInfo) {
		this.threadPoolInfo = threadPoolInfo;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getUpTime() {
		return upTime;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}
	
	
	
}
