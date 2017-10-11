package com.dcits.util.linux.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParseInfoFactory {
	
	/**
	 * 根据主机类型获取不同的解析方法
	 * @param uname
	 * @return
	 */
	public static ParseInfo getInstance(String uname) {
		ParseInfo p = null;
		try {
			p = (ParseInfo) Class.forName(getProperties(uname)).newInstance();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return p;
	}
	
	
	private static String getProperties(String name) throws IOException {
		Properties p = new Properties();
		InputStream inputStream = ParseInfoFactory.class.getClassLoader()
			       .getResourceAsStream("parseInfo.properties");		  
		p.load(inputStream);
		return p.getProperty(name);
	}
}
