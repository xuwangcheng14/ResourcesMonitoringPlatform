package com.dcits.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.dcits.bean.JvmInfo;
import com.dcits.bean.WeblogicInfo;

/**
 * 工具类
 * @author xuwangcheng
 * @version 20170217
 *
 */
public class DcitsUtil {
	
	//public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";   	
	public static final String DEFAULT_DATE_PATTERN = "HH:mm:ss"; 
	public static final String FULL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static Integer id = 0;
	
	public static String dataFormat(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format); 
		return dateFormat.format(date);
	}
	
	/**
	 * 当前日期的String返回
	 * @return
	 */
	public static String getCurrentTime(String format) {
		return dataFormat(new Date(), format);
	}	
	
	
	/**
	 * jmx获取属性参数
	 * 
	 * @param objectName
	 * @param name
	 * @param connection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(ObjectName objectName, String name, MBeanServerConnection connection) {
	    Object obj = null;
	    try {
	        obj = connection.getAttribute(objectName, name);
	    } catch (Exception e) {
	        // TODO
	        e.printStackTrace();
	    }
	    return (T) obj;
	}
	
	/**
	 * 日期格式转换
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate(Date date, String format) {
	    DateFormat df = new SimpleDateFormat(format);
	    return df.format(date);
	}
	
	
	/**
	 * 字节转换成MB
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteToMB(long bytes) {
	    double mb = (double) bytes / 1024 / 1024;
	    DecimalFormat df = new DecimalFormat("#.00");
	    return df.format(mb);
	}
	
	/**
	 * 
	 * @param span
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String formatTimeSpan(long span) {  
        long minseconds = span % 1000;  
  
        span = span / 1000;  
        long seconds = span % 60;  
  
        span = span / 60;  
        long mins = span % 60;  
  
        span = span / 60;  
        long hours = span % 24;  
  
        span = span / 24;  
        long days = span;  
        return (new Formatter()).format("%1$d天 %2$02d:%3$02d:%4$02d.%5$03d",  
                days, hours, mins, seconds, minseconds).toString();  
    }  
	
	
	public static Object[] addWeblogicJvm(WeblogicInfo info) {
		boolean flag = false ;
		String msg = "";
		JvmInfo jvmInfo = null;
		
		//检查是否有对应的parameters参数
		if (StringUtils.isEmpty(info.getParameters())) {
			msg = "你还没设置该weblogic信息的附加参数!";
			return new Object[]{flag, msg, jvmInfo};
		}
		Map maps = null;
		try {
			maps = new ObjectMapper().readValue(info.getParameters(), Map.class);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = "解析附加参数出错:" + e.getMessage();
			return new Object[]{flag, msg, jvmInfo};	
		}
		
		//创建新的jvm对象
		jvmInfo = new JvmInfo();
		
		try {
			jvmInfo.setUsername(maps.get("linuxLoginUsername").toString());
			jvmInfo.setPassword(maps.get("linuxLoginPassword").toString());
			jvmInfo.setJavaHome(maps.get("javaHome").toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = "附加参数设置错误!请检查!";
			return new Object[]{flag, msg, jvmInfo};
		}
		
		jvmInfo.setHost(info.getHost());
		jvmInfo.setPort("22");		
		jvmInfo.setServerId(info.getServerId());
		jvmInfo.setWeblogicPort(info.getPort());
				
		//获取pid
		try {
			jvmInfo.setPid();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = "获取pid出错";
			return new Object[]{flag, msg, jvmInfo};
		}
		
		try {
			jvmInfo.conectJvm();
			jvmInfo.setInfo();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			jvmInfo.disconect();
			msg = jvmInfo.getErrorInfo();
			return new Object[]{flag, msg, jvmInfo};
		}
		
		jvmInfo.setStopFlag(false);
		jvmInfo.setId(++DcitsUtil.id);
		info.setJvmId(jvmInfo.getId());
		jvmInfo.setMark(StringUtils.isEmpty(info.getMark()) ? "weblogic(" + info.getHost() + ":" + info.getPort() + ")" : info.getMark() );	
		flag = true;
		return new Object[]{flag, msg, jvmInfo};
	}
}
