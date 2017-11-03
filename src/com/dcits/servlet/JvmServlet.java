package com.dcits.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import xuwangcheng.love.w.servlet.AbstractHttpServlet;
import xuwangcheng.love.w.servlet.annotation.ExecuteRequest;
import xuwangcheng.love.w.servlet.annotation.RequestBody;

import com.dcits.bean.JvmInfo;
import com.dcits.bean.LinuxInfo;
import com.dcits.bean.WeblogicInfo;
import com.dcits.bean.util.UserSpace;
import com.dcits.consts.LinuxConstant;
import com.dcits.util.DcitsUtil;
import com.dcits.util.ServletUtil;

public class JvmServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JvmServlet() {
		// TODO Auto-generated constructor stub
		super.setServlet(this);
	}
	
	@ExecuteRequest
	public void delAll(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		for (JvmInfo info:space.getJvmInfos()) {
			info.stop();
			info.disconect();
		}		
		space.getJvmInfos().clear();
		System.gc();
		
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
	}	
	
	@ExecuteRequest
	public void getList(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			ajaxData.put("data", new ArrayList<Object>());
			return;
		}
		
		List<JvmInfo> infos = space.getJvmInfos();
		for (final JvmInfo info:infos) {
			ServletUtil.execThread(new Runnable() {				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					info.setInfo();					
				}
			});
			//info.setInfo();	
		}
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
		ajaxData.put("data", infos);
	}
	
	@ExecuteRequest
	public void del(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("id") Integer id, @RequestBody("userKey")String userKey) {		
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		
		JvmInfo info = space.getJvmInfo(id);
		
		if (info == null) {
			ajaxData.put("msg", "该记录可能已被删除，请尝试刷新表格查看!");
			return;
		}
		
		info.stop();
		info.disconect();
		
		space.getJvmInfos().remove(info);			
		
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
		
	}
	
	@ExecuteRequest
	public void stack(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("id") Integer id, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		JvmInfo info = space.getJvmInfo(id);
		
		if (info == null) {
			ajaxData.put("msg", "该记录可能已被删除，请尝试刷新表格查看!");
			return;
		}
		
		String log = info.stackShow();
		if (log == null) {
			ajaxData.put("msg", "获取失败,请检查连接有效性然后重新尝试!");
			return;
		}
		ajaxData.put("log", log);
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
	}
	
	@ExecuteRequest
	public void reconnect(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("id") Integer id, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		JvmInfo info = space.getJvmInfo(id);
		
		if (info == null) {
			ajaxData.put("msg", "该记录可能已被删除，请尝试刷新表格查看!");
			return;
		}

		//info.disconect();
		
		try {
			info.setPid();
			info.conectJvm();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("msg", "重新连接时发生错误:" + e.getMessage() + "\n(" + info.getErrorInfo() + ")");
			return;
		}
		
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
	}
	
	@ExecuteRequest
	public void check (Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("id") Integer id, @RequestBody("javaHome")String javaHome, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		LinuxInfo info = space.getLinuxInfo(id);
		
		if (info == null) {
			ajaxData.put("msg", "该主机可能已被删除，请尝试刷新表格查看!");
			return;
		}
		
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
		
		if (StringUtils.isNotEmpty(javaHome)) {
			info.setJavaHome(javaHome);
		}
		
		if (StringUtils.isEmpty(info.getJavaHome())) {
			info.parseJavaHome();
		}		
		
		ajaxData.put("javaHome",info.getJavaHome());
		String processNames = info.jpsShow();
		
		if (StringUtils.isEmpty(processNames)) {
			ajaxData.put("processNames", "没有获取该到主机上的java进程列表,请手动输入需要监控的java进程pid或者检查JAVA_HOME的BIN路径是否正确!");
			return;
		}		
		ajaxData.put("processNames", processNames);		
	}
	
	@ExecuteRequest
	public void addWeblogicJvm(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("id") Integer weblogicId, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		WeblogicInfo info = space.getWeblogicInfo(weblogicId);
		if (info == null) {
			ajaxData.put("msg", "该应用可能已被删除，请尝试刷新表格查看!");
			return;
		}
		
		/*//检查是否有对应的parameters参数
		if (StringUtils.isEmpty(info.getParameters())) {
			ajaxData.put("msg", "你还没设置该weblogic信息的附加参数!");
			return;
		}
		Map maps = null;
		try {
			maps = new ObjectMapper().readValue(info.getParameters(), Map.class);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("msg", "解析附加参数出错:" + e.getMessage());
			return;		
		}
		
		//创建新的jvm对象
		JvmInfo jvmInfo = new JvmInfo();
		
		try {
			jvmInfo.setUsername(maps.get("linuxLoginUsername").toString());
			jvmInfo.setPassword(maps.get("linuxLoginPassword").toString());
			jvmInfo.setJavaHome(maps.get("javaHome").toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("msg", "附加参数设置错误!请检查!");
			return;	
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
			ajaxData.put("msg", e.getMessage());
			return;	
		}				
		
		try {
			jvmInfo.conectJvm();
			jvmInfo.setInfo();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			jvmInfo.disconect();
			ajaxData.put("msg", jvmInfo.getErrorInfo());
			return;
		}
		
		jvmInfo.setStopFlag(false);
		int r = ++DcitsUtil.id;
		jvmInfo.setId(r);
		info.setJvmId(r);
		jvmInfo.setMark(StringUtils.isEmpty(info.getMark()) ? "weblogic(" + info.getHost() + ":" + info.getPort() + ")" : info.getMark() );	
		space.getJvmInfos().add(jvmInfo);*/
		Object[] os = DcitsUtil.addWeblogicJvm(info);
		
		if (!(boolean)os[0]) {
			ajaxData.put("msg", (String)os[1]);
			return;
		}
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
		space.getJvmInfos().add((JvmInfo)os[2]);		
	}
	
	@ExecuteRequest
	public void add (Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("id") Integer id
			, @RequestBody("pid") String pid, @RequestBody("processName")String processName
			, @RequestBody("javaHome")String javaHome, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		LinuxInfo info = space.getLinuxInfo(id);
		
		if (info == null) {
			ajaxData.put("msg", "该主机可能已被删除，请尝试刷新表格查看!");
			return;
		}
		
		if (StringUtils.isEmpty(pid)) {
			ajaxData.put("msg", "pid不能为空!");
			return;
		}
		
		if (StringUtils.isEmpty(info.getJavaHome())) {
			info.parseJavaHome();
		}
		
		if (StringUtils.isNotEmpty(javaHome)) {
			info.setJavaHome(javaHome);
		}
		
		JvmInfo jvmInfo = new JvmInfo(info);
		jvmInfo.setPid(pid.trim());
		try {
			jvmInfo.conectJvm();
			jvmInfo.setInfo();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			jvmInfo.disconect();
			ajaxData.put("msg", jvmInfo.getErrorInfo());
			return;
		}
		
		jvmInfo.setStopFlag(false);
		jvmInfo.setId(++DcitsUtil.id);
		jvmInfo.setProcessName(processName);	
		space.getJvmInfos().add(jvmInfo);	
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
	}
	
	
	
}
