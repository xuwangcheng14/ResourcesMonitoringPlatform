package com.dcits.listener;

import java.util.HashMap;

import javax.servlet.ServletContextEvent;

import xuwangcheng.love.w.servlet.listener.LovewInitListener;

import com.dcits.bean.util.UserSpace;
import com.dcits.util.ServletUtil;

public class InitWeb extends LovewInitListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		super.contextDestroyed(arg0);
		ServletUtil.shutdownPool();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		super.contextInitialized(arg0);
		/*arg0.getServletContext().setAttribute(LinuxConstant.LINUX_INFO_LIST_ATTRIBUTE, new ArrayList<LinuxInfo>());
		arg0.getServletContext().setAttribute(WeblogicConstant.WEBLOGIC_INFO_LIST_ATTRIBUTE, new ArrayList<WeblogicInfo>());
		arg0.getServletContext().setAttribute("jvmInfos", new ArrayList<JvmInfo>());*/
		arg0.getServletContext().setAttribute("userSpaces", new HashMap<String, UserSpace>());
		ServletUtil.setContext(arg0.getServletContext());
		
		/*WeblogicInfo info1 = new WeblogicInfo(1, "127.0.0.1", "7001", "weblogic", "12345678", "", "");
		WeblogicInfo info2 = new WeblogicInfo(2, "127.0.0.1", "7001", "weblogic", "12345678", "", "");
		WeblogicInfo info3 = new WeblogicInfo(3, "127.0.0.1", "7001", "weblogic", "12345678", "", "");
		WeblogicInfo info4 = new WeblogicInfo(4, "127.0.0.1", "7001", "weblogic", "12345678", "", "");
		info1.connect();
		info2.connect();
		info3.connect();
		info4.connect();
		ServletUtil.getWeblogicList().add(info1);
		ServletUtil.getWeblogicList().add(info2);
		ServletUtil.getWeblogicList().add(info3);
		ServletUtil.getWeblogicList().add(info4);*/
		
		/*LinuxInfo info5 = new LinuxInfo(5, "22", "115.159.34.224", "root", "xUWANGCHENG14", "", "");
		try {
			info5.conect();
			info5.start(arg0.getServletContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//ServletUtil.getLinuxList().add(info5);
		
	}
	
	

}
