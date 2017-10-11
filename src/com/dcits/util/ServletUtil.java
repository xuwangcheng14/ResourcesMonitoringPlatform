package com.dcits.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.dcits.bean.ServerInfo;
import com.dcits.bean.util.UserSpace;

public class ServletUtil {
	
	private static ServletContext context;
		
	private static ExecutorService pool = Executors.newFixedThreadPool(180);
	
	public static void execThread(Runnable exec) {
		pool.execute(exec);
	}
	
	public static void shutdownPool() {
		pool.shutdown();
	}
	
	
	public static ServletContext getContext() {
		return context;
	}
	
	public static void setContext(ServletContext context) {
		ServletUtil.context = context;
	}
	
	/**
	 * 更新当前监控中的服务器信息
	 * @param info
	 * @param infos
	 */
	public static void updateParameter(ServerInfo info, List<ServerInfo> infos) {
		
		for (ServerInfo s:infos) {
			if (s.getServerId() == info.getServerId()) {
				s.setParameters(info.getParameters());
				s.setUsername(info.getUsername());
				s.setPassword(info.getPassword());
				s.setPort(info.getPort());
			}
		}
	}
	
	/**
	 * 获取context中的UserSpace集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, UserSpace> getUserSpaces() {
		return (Map<String, UserSpace>) context.getAttribute("userSpaces");
	}
	
	/**
	 * 根据userKey获取当前的UserSpace
	 * @param userKey
	 * @return
	 */
	public static UserSpace getUserSpace(String userKey) {
		for (String key:getUserSpaces().keySet()) {
			if (key.equals(userKey)) {
				return getUserSpaces().get(key);
			}
		}
		return null;
	}
	
	/**
	 * 增加一个新的UserSpace
	 * @param userKey
	 * @return
	 */
	public static UserSpace addUserSpace(String userKey) {
		UserSpace space = new UserSpace(userKey);
		getUserSpaces().put(userKey, space);
		return space;
	}
	
	/**
	 * 读取request中的body内容
	 * @param request
	 * @return
	 */
	public static String getBody (HttpServletRequest request) { 

		InputStream inputStream = null;
		BufferedReader reader = null;
		String strResponse = "";
				
		try {
			inputStream = request.getInputStream();
			String strMessage = "";			
			
			reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			while ((strMessage = reader.readLine()) != null) {
				strResponse += strMessage;
			}			

			strResponse = strResponse.trim();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (inputStream != null) {
					inputStream.close();
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return strResponse;
	}
	
}
