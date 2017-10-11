package com.dcits.util.tomcat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.StringUtils;

import com.dcits.bean.TomcatInfo;
import com.dcits.bean.tomcat.TomcatRealTimeInfo;
import com.dcits.util.DcitsUtil;

public class TomcatJmxUtil {

	/**
	 * 获取jmx连接
	 * @param info
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void getConnection (TomcatInfo info) throws Exception {
		String jmxUrl = "service:jmx:rmi:///jndi/rmi://" + info.getHost() + ":8999/jmxrmi";
		JMXServiceURL serviceURL = new JMXServiceURL(jmxUrl); 
		
		JMXConnector connector = null;
		
		if (StringUtils.isEmpty(info.getUsername()) && StringUtils.isEmpty(info.getPassword())) {
			connector = JMXConnectorFactory.connect(serviceURL);
		} else {			
			Map map = new HashMap();  
            String[] credentials = new String[] {info.getUsername(), info.getPassword()};  
            map.put("jmx.remote.credentials", credentials); 
            connector = JMXConnectorFactory.connect(serviceURL, map);
		}	
		
		MBeanServerConnection mbsc = connector.getMBeanServerConnection(); 
		
		info.setConnection(mbsc);		
	}
	
	public static void getRealTimeInfo (TomcatInfo tomcat) throws Exception {
		
		if (tomcat.getConnection() == null) {
			tomcat.setConnectStatus("没有可用的连接!");
			return;
		}
		
		TomcatRealTimeInfo info = tomcat.getInfo();
		MBeanServerConnection conn = tomcat.getConnection();
		
		//是否首次获取或者是否重新获取新的固定信息？
		if (tomcat.isFirstGetInfo()) {
			//启动时间
			ObjectName runtimeObjName = new ObjectName("java.lang:type=Runtime");
			Date startTime = new Date((Long)DcitsUtil.getAttribute(runtimeObjName, "StartTime", conn));
			
			info.setStartTime(DcitsUtil.formatDate(startTime, DcitsUtil.FULL_DATE_PATTERN));
			
			Long timespan = (Long)DcitsUtil.getAttribute(runtimeObjName, "Uptime", conn);
			
			info.setUpTime(DcitsUtil.formatTimeSpan(timespan));
		}
		
		//获取动态信息
		
		//堆使用情况
		
		
	}
}
