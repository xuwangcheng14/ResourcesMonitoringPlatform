package com.dcits.servlet;

import com.dcits.dao.ServerDao;

import xuwangcheng.love.w.servlet.AbstractHttpServlet;
import xuwangcheng.love.w.servlet.annotation.InjectDao;

public class TomcatServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TomcatServlet() {
		// TODO Auto-generated constructor stub
		super.setServlet(this);
	}
	
	@InjectDao
	private ServerDao serverDao;
	
	public void setServerDao(ServerDao serverDao) {
		this.serverDao = serverDao;
	}
	
	public ServerDao getServerDao() {
		return serverDao;
	}
	

}
