package com.dcits.bean.weblogic;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * weblogic相关状态信息
 * @author xuwangcheng
 * @version 20170310
 *
 */
public class WeblogicRealTimeInfo {
	
	/**
	 * 堆内存空闲百分比
	 */
	public static final String JVM_FREE_PERCENT = "freePercent";
	/**
	 * 堆内存当前空闲大小
	 */
	public static final String JVM_FREE_SIZE = "freeSize";
	/**
	 * 堆内存当前使用大小
	 */
	public static final String JVM_CURRENT_USE_SIZE = "currentSize";
	
	/**
	 * 暂挂用户请求数
	 */
	public static final String THREAD_REQUEST_PENDING_COUNT = "pendingCount";
	/**
	 * 当前空闲的线程数
	 */
	public static final String THREAD_IDLE_COUNT = "idleCount";
	/**
	 * 活动线程总数
	 */
	public static final String THREAD_TOTAL_COUNT = "maxThreadCount";
	
	/**
	 * 独占线程数
	 */
	public static final String THREAD_HOGGING_COUNT = "hoggingThreadCount";
	
	/**
	 * 吞吐量
	 */
	public static final String THREAD_THROUGHPUT = "throughput";
	
	/**
	 * JDBC当前状态<br>
	 * Running/Suspended/Shutdown/Overloaded/Unknown<br>
	 * 正在运行/已挂起/关闭/超载/未知
	 * 
	 */
	public static final String JDBC_DATA_SOURCE_STATE = "jdbcState";
	
	/**
	 * 当前正在由应用程序使用的连接数。
	 */
	public static final String JDBC_ACTIVE_CONNECTIONS_CURRENT_COUNT = "activeConnectionsCurrentCount";
	
	/**
	 * 等待数据库连接的连接请求数。
	 */
	public static final String JDBC_WAITING_FOR_CONNECTION_CURRENT_COUNT = "waitingForConnectionCurrentCount";
	
	/**
	 * 此数据源实例中当前空闲并可供应用程序使用的数据库连接数。
	 */
	public static final String JDBC_CURRENT_NUM_AVAILABLE = "availableConnectionCount";
	
	/**
	 * 实例化数据源以来数据源此实例中的活动数据库连接的最大数量。
	 */
	public static final String JDBC_ACTIVE_CONNECTIONS_HIGH_COUNT = "activeConnectionsHighCount";
	
	/**
	 * 当前server的名称
	 */
	private String serverName;
	
	/**
	 * 堆内存最大值
	 */
	private String maxJvm;
	
	/**
	 * 启动时间
	 */
	private String startTime;
	
	/**
	 * 当前状态RUNNING等
	 */
	private String status;
	
	/**
	 * 当前健康度
	 * 0为正常 其他还不清楚
	 */
	private String health;
	
	
	/**
	 * 动态获取
	 * jvm信息
	 */
	private Map<String, String> jvmInfo = new HashMap<String, String>();
	
	/**
	 * 动态获取
	 * 队列信息<br>只取第一个
	 */
	private Map<String, String> queueInfo = new HashMap<String, String>();
	
	/**
	 * 动态获取
	 * JDBC信息<br>
	 * key是jdbc名字   value是相关信息
	 */
	private Map<String, Object> jdbcInfo = new HashMap<String, Object>();
	
	private String time;
	

	
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getMaxJvm() {
		return maxJvm;
	}

	public void setMaxJvm(String maxJvm) {
		this.maxJvm = maxJvm;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}

	public Map<String, String> getJvmInfo() {
		return jvmInfo;
	}

	public void setJvmInfo(Map<String, String> jvmInfo) {
		this.jvmInfo = jvmInfo;
	}

	public Map<String, String> getQueueInfo() {
		return queueInfo;
	}

	public void setQueueInfo(Map<String, String> queueInfo) {
		this.queueInfo = queueInfo;
	}

	
	
	public Map<String, Object> getJdbcInfo() {
		return jdbcInfo;
	}

	public void setJdbcInfo(Map<String, Object> jdbcInfo) {
		this.jdbcInfo = jdbcInfo;
	}

	@Override
	public String toString() {
		return "WeblogicRealTimeInfo [serverName=" + serverName + ", maxJvm="
				+ maxJvm 
				+ ", startTime=" + startTime + ", status=" + status
				+ ", health=" + health + ", jvmInfo=" + jvmInfo
				+ ", queueInfo=" + queueInfo + "]";
	}
	
}
