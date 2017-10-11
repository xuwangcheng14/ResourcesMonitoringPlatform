package com.dcits.util.weblogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import weblogic.health.HealthState;

import com.dcits.bean.WeblogicInfo;
import com.dcits.bean.weblogic.WeblogicRealTimeInfo;
import com.dcits.util.DcitsUtil;


/**
 * jmx获取weblogic信息的工具类<br>
 * 参考 JmxWeblogicDemo.java
 * @author xuwangcheng
 * @version 20170310
 *
 */
public class WeblogicJmxUtil {
	
	private static final String RUNTIMESERVICEMBEAN = "com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean";
	
	private static final String PROTOCOL = "t3";
	
	private static final String JINDIROOT = "/jndi/";
	
	private static final String MSERVER = "weblogic.management.mbeanservers.runtime";	
	
	
	/**
	 * 初始化jmx连接
	 * @param weblogic
	 * @throws MalformedObjectNameException
	 * @throws IOException
	 */
	public static void getConnection(WeblogicInfo weblogic) throws MalformedObjectNameException, IOException, Exception {
		
		Integer portInteger = Integer.valueOf(weblogic.getPort());
		int port = portInteger.intValue();
		JMXServiceURL serviceURL = new JMXServiceURL(PROTOCOL, weblogic.getHost(), port, JINDIROOT + MSERVER);
		
		Hashtable<String, String> h = new Hashtable<String, String>();
		h.put(Context.SECURITY_PRINCIPAL, weblogic.getUsername());
	    h.put(Context.SECURITY_CREDENTIALS, weblogic.getPassword());
	    h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
	            "weblogic.management.remote");
	    	    
	    weblogic.setConnection(JMXConnectorFactory.connect(serviceURL, h).getMBeanServerConnection());
	    
	    ObjectName runtimeService = new ObjectName(RUNTIMESERVICEMBEAN);
	    
	    //运行状态
	    ObjectName serverRuntime =  DcitsUtil.getAttribute(runtimeService, "ServerRuntime", weblogic.getConnection());	    
	    weblogic.setServerRuntime(serverRuntime);
	    //JVM状态
	    ObjectName jvmRuntime =  DcitsUtil.getAttribute(serverRuntime, "JVMRuntime", weblogic.getConnection());
	    weblogic.setJvmRuntime(jvmRuntime);
	    
	    //线程状态
	    ObjectName threadPoolRuntime =  DcitsUtil.getAttribute(serverRuntime, "ThreadPoolRuntime", weblogic.getConnection());
	    weblogic.setThreadPoolRuntime(threadPoolRuntime);
	    
	    //jdbc状态
	    ObjectName jdbcServiceRuntime = DcitsUtil.getAttribute(serverRuntime, "JDBCServiceRuntime", weblogic.getConnection());
	    //ObjectName[] jdbcDataSourceRuntimeMBeans = DcitsUtil.getAttribute(jdbcServiceRuntime, "JDBCDataSourceRuntimeMBeans", weblogic.getConnection());
	    /*if (jdbcDataSourceRuntimeMBeans.length > 0) {
	    	weblogic.setJdbcRuntime(jdbcDataSourceRuntimeMBeans[0]);
	    }*/
	    
	    weblogic.setJdbcRuntime(jdbcServiceRuntime);
	    
	    
	}
	
	/**
	 * 获取weblogic信息
	 * 前台调用一次weblogic/getInfo?weblogicId=?就执行一次该方法
	 * 
	 * @param weblogic
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public static void getWeblogicInfo(WeblogicInfo weblogic) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException, Exception {
		if (weblogic.getConnection() == null) {
			return;
		}
		//getServerRuntime 获取运行信息
		WeblogicRealTimeInfo info = weblogic.getInfo();
		MBeanServerConnection conn = weblogic.getConnection();
		
		//运行状态情况
		ObjectName serverRuntime = weblogic.getServerRuntime();
		//jvm情况
	    ObjectName JVMRuntime = weblogic.getJvmRuntime();
		
	    //线程队列情况
	    ObjectName threadPoolRuntime = weblogic.getThreadPoolRuntime();
	    
	    //jdbc数据源
	    ObjectName jdbcRuntime = weblogic.getJdbcRuntime();
		
		//是否首次获取或者是否重新获取新的固定信息？
		if (weblogic.isFirstGetInfo()) {
			//server的name
			String serverName =  DcitsUtil.getAttribute(serverRuntime, "Name", conn);
			
			//server的激活时间
			Long activationTime =  DcitsUtil.getAttribute(serverRuntime, "ActivationTime",  conn);
		    Date date = new Date(activationTime);
		    String time =  DcitsUtil.formatDate(date, "yyyy/MM/dd HH:mm:ss");
		    
		    //运行状态
		    String state =  DcitsUtil.getAttribute(serverRuntime, "State", conn);
		    
		    //健康状态
		    HealthState healthState = (HealthState) conn.getAttribute(serverRuntime, "HealthState");
		    String health = healthState.getState() == 0 ? "Health" : "Not Health";
		    
		    //最大堆内存大小
		    Long heapSizeMax =  DcitsUtil.getAttribute(JVMRuntime, "HeapSizeMax", conn);
		    String maxJvm =  DcitsUtil.byteToMB(heapSizeMax);
		    
		    info.setServerName(serverName);
		    info.setHealth(health);
		    info.setStatus(state);
		    info.setStartTime(time);
		    info.setMaxJvm(maxJvm);
		    
		    weblogic.setFirstGetInfo(false);
		}
		
	    //空闲内存百分比
	    Integer heapFreePercent =  DcitsUtil.getAttribute(JVMRuntime, "HeapFreePercent", conn);
	    String freePercent = String.valueOf(heapFreePercent);
	    
	    //当前已使用的堆的总空间
	    Long heapSizeCurrent =  DcitsUtil.getAttribute(JVMRuntime, "HeapSizeCurrent", conn);
	    String currentSize =  DcitsUtil.byteToMB(heapSizeCurrent);
	    
	    // 当前堆 空闲 HeapFreeCurrent
	    Long heapFreeCurrent =  DcitsUtil.getAttribute(JVMRuntime, "HeapFreeCurrent", conn);
	    String freeSize =  DcitsUtil.byteToMB(heapFreeCurrent);
	    
	    // 执行线程总数 
        Integer executeThreadTotalCount =  DcitsUtil.getAttribute(threadPoolRuntime, "ExecuteThreadTotalCount", conn);
        String maxThreadCount = String.valueOf(executeThreadTotalCount);
	        	    
	    //空闲线程数
	    Integer executeThreadCurrentIdleCount =  DcitsUtil.getAttribute(
	    		threadPoolRuntime, "ExecuteThreadIdleCount", conn);
	    String idleCount = String.valueOf(executeThreadCurrentIdleCount);
	    
	    //暂挂请求数
	    Integer pendingRequestCurrentCount = DcitsUtil.getAttribute(
	    		threadPoolRuntime, "PendingUserRequestCount", conn);
	    String pendingCount = String.valueOf(pendingRequestCurrentCount);
	         
	    //独占线程数
	    Integer hoggingThreadCount = DcitsUtil.getAttribute(threadPoolRuntime, "HoggingThreadCount", conn);
	    String hoggingCount = String.valueOf(hoggingThreadCount);
	    
	    //吞吐量
	    Double throughput  = DcitsUtil.getAttribute(threadPoolRuntime, "Throughput", conn);	    
	    String throughputs = String.format("%.2f", throughput);
	    
	    ObjectName[] jdbcDataSourceRuntimeMBeans = DcitsUtil.getAttribute(jdbcRuntime, "JDBCDataSourceRuntimeMBeans", weblogic.getConnection());
	    
	    if (jdbcDataSourceRuntimeMBeans.length > 0) {
	    	
	    	for (ObjectName o:jdbcDataSourceRuntimeMBeans) {
	    		Map<String, String> jdbcInfoMap = new HashMap<String, String>();
	    		
	    		 //jdbc数据源状态
			    String jdbcState = DcitsUtil.getAttribute(o, "State", conn);
			    
			    //jdbc当前活动连接数
			    Integer activeConnectionCount = DcitsUtil.getAttribute(o, "ActiveConnectionsCurrentCount", conn);
			    
			    //jdbc当前等待连接数
			    Integer waittingConnectionCount = DcitsUtil.getAttribute(o, "WaitingForConnectionCurrentCount", conn);
			    
			    //jdbc当前可用连接数
			    Integer availableNum = DcitsUtil.getAttribute(o, "NumAvailable", conn);
			    
			    //历史最大活动连接数
			    Integer activeConnectionsHighCount = DcitsUtil.getAttribute(o, "ActiveConnectionsHighCount", conn);
			    
			    jdbcInfoMap.put(WeblogicRealTimeInfo.JDBC_DATA_SOURCE_STATE, jdbcState);
			    jdbcInfoMap.put(WeblogicRealTimeInfo.JDBC_ACTIVE_CONNECTIONS_CURRENT_COUNT, String.valueOf(activeConnectionCount));
			    jdbcInfoMap.put(WeblogicRealTimeInfo.JDBC_WAITING_FOR_CONNECTION_CURRENT_COUNT, String.valueOf(waittingConnectionCount));
			    jdbcInfoMap.put(WeblogicRealTimeInfo.JDBC_CURRENT_NUM_AVAILABLE, String.valueOf(availableNum));
			    jdbcInfoMap.put(WeblogicRealTimeInfo.JDBC_ACTIVE_CONNECTIONS_HIGH_COUNT, String.valueOf(activeConnectionsHighCount));
			    
			    info.getJdbcInfo().put((String)DcitsUtil.getAttribute(o, "Name", conn), jdbcInfoMap);	    		
	    	}    	
	    }
	    	    
	    info.getJvmInfo().put(WeblogicRealTimeInfo.JVM_CURRENT_USE_SIZE, currentSize);
	    info.getJvmInfo().put(WeblogicRealTimeInfo.JVM_FREE_PERCENT, freePercent);
	    info.getJvmInfo().put(WeblogicRealTimeInfo.JVM_FREE_SIZE, freeSize);
	    
	    info.getQueueInfo().put(WeblogicRealTimeInfo.THREAD_IDLE_COUNT, idleCount);
	    info.getQueueInfo().put(WeblogicRealTimeInfo.THREAD_REQUEST_PENDING_COUNT, pendingCount);
	    info.getQueueInfo().put(WeblogicRealTimeInfo.THREAD_TOTAL_COUNT, maxThreadCount);
	    info.getQueueInfo().put(WeblogicRealTimeInfo.THREAD_HOGGING_COUNT, hoggingCount);
	    info.getQueueInfo().put(WeblogicRealTimeInfo.THREAD_THROUGHPUT, throughputs);
	    	    	    
	    info.setTime(DcitsUtil.getCurrentTime(DcitsUtil.DEFAULT_DATE_PATTERN));
	    
	}
		
	
	/**
	 * 解析批量保存weblogicServer的信息字符串
	 * @param str
	 * @param weblogcId
	 * @return
	 */
	public static List<WeblogicInfo> parseServerListStr(String str, int weblogcId) {
		List<WeblogicInfo> servers = new ArrayList<WeblogicInfo>();				
		
		String[] infos = str.split("\\n");
		String[] info = null;		
		for (String s:infos) {
			info = s.split(",");
			String[] ipport = info[0].split(":");	
			String mark = "";
			if (info.length > 3) {
				mark = info[3].trim();
			}
			try {				
				servers.add(new WeblogicInfo(++weblogcId, ipport[0].trim(), ipport[1].trim(), info[1].trim(), info[2].trim(), mark, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN), ""));				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				continue;
			}
		}		
		return servers;
	}
	
}
