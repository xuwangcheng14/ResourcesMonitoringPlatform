package com.dcits.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import xuwangcheng.love.w.servlet.AbstractHttpServlet;
import xuwangcheng.love.w.servlet.annotation.ExecuteRequest;
import xuwangcheng.love.w.servlet.annotation.InjectDao;
import xuwangcheng.love.w.servlet.annotation.RequestBody;
import xuwangcheng.love.w.util.Constants;

import com.dcits.bean.WeblogicInfo;
import com.dcits.bean.util.UserSpace;
import com.dcits.consts.LinuxConstant;
import com.dcits.dao.ServerDao;
import com.dcits.util.DcitsUtil;
import com.dcits.util.ServletUtil;

public class WeblogicServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public WeblogicServlet() {
		// TODO Auto-generated constructor stub
		super.setServlet(this);
	}
	
	@InjectDao
	private ServerDao serverDao;
	
	
	
	/**
	 * 获取当前weblogic实时信息
	 * @param ajaxData
	 * @param request
	 * @throws Exception
	 */
	@ExecuteRequest
	public void getList(Map<String, Object> ajaxData, HttpServletRequest request
			, @RequestBody("userKey")String userKey) {
		
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			ajaxData.put("data", new ArrayList<Object>());
			return;
		}
		
		List<WeblogicInfo> list = space.getWeblogicInfos();
			
		for (final WeblogicInfo w:list) {
			//w.setInfo();
    		ServletUtil.execThread(new Runnable() {				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					w.setInfo();					
				}
			});
    	}		
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
		ajaxData.put("data", list);	
	}
	
	/**
	 * 重新获取连接
	 * @param ajaxData
	 * @param request
	 * @param id
	 * @throws Exception
	 */
	@ExecuteRequest
	public void reconnect(Map<String, Object> ajaxData, HttpServletRequest request
			, @RequestBody("id") Integer id, @RequestBody("userKey")String userKey) {
		
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		WeblogicInfo info = space.getWeblogicInfo(id);
		if (info == null) {
			ajaxData.put("msg", "该主机或者应用可能已被删除，请尝试刷新表格查看!");
			return;
		}
		
		info.connect();
		
		if (!"true".equals(info.getConnectStatus())) {
			LOGGER.error("weblogic" + info.getHost() + ":" + info.getPort() + "在创建连接时出错:" + info.getConnectStatus());
			ajaxData.put("msg", "重新创建连接失败:" + info.getConnectStatus());
			return;
		}
		
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
		
	}
	
	/**
	 * 删除weblogic
	 * @param ajaxData
	 * @param request
	 * @param id
	 * @throws Exception
	 */
	@ExecuteRequest
	public void del(Map<String, Object> ajaxData, HttpServletRequest request
			, @RequestBody("id") Integer id, @RequestBody("userKey")String userKey) {	
		
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		WeblogicInfo info = space.getWeblogicInfo(id);
		
		if (info == null) {
			ajaxData.put("msg", "该主机或者应用可能已被删除，请尝试刷新表格查看!");
			return;
		}
				
		space.getWeblogicInfos().remove(info);			
		
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
		
	}
	
	/**
	 * 删除全部
	 * @param ajaxData
	 * @param request
	 * @throws Exception
	 */
	@ExecuteRequest
	public void delAll(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("userKey")String userKey) {
		
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		space.getWeblogicInfos().clear();
		System.gc();
		
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);		
	}
	
	
	/**
	 * 使用历史记录
	 * @param ajaxData
	 * @param request
	 * @param id
	 * @throws Exception
	 */
	@ExecuteRequest
	public void useHistory(Map<String, Object> ajaxData, HttpServletRequest request
			, @RequestBody("serverId") Integer id, @RequestBody("userKey")String userKey) {	
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
		try {
			WeblogicInfo info = new WeblogicInfo(serverDao.findById(id));
    		info.setId(++DcitsUtil.id);
    		info.connect();
    		
    		if (!"true".equals(info.getConnectStatus())) {
    			ajaxData.put("returnCode", Constants.ERROR_RETURN_CODE);
    			ajaxData.put("msg", "尝试连接服务器时发生了错误,详情：" + info.getConnectStatus());
    		} else {
    			space.getWeblogicInfos().add(info);
        		serverDao.updateTime(id); 
    		}    	    		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("returnCode", Constants.ERROR_RETURN_CODE);
			ajaxData.put("msg", "在启用监控时发生了错误：" + e.getMessage());
		} 		
	}



	
	
	/***************************************************************/
	public ServerDao getServerDao() {
		return serverDao;
	}



	public void setServerDao(ServerDao serverDao) {
		this.serverDao = serverDao;
	}

}
