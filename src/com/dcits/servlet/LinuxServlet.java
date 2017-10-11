package com.dcits.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import xuwangcheng.love.w.servlet.AbstractHttpServlet;
import xuwangcheng.love.w.servlet.annotation.ExecuteRequest;
import xuwangcheng.love.w.servlet.annotation.InjectDao;
import xuwangcheng.love.w.servlet.annotation.RequestBody;
import xuwangcheng.love.w.util.Constants;

import com.dcits.bean.LinuxInfo;
import com.dcits.bean.util.UserSpace;
import com.dcits.consts.LinuxConstant;
import com.dcits.dao.ServerDao;
import com.dcits.util.DcitsUtil;
import com.dcits.util.ServletUtil;
import com.dcits.util.linux.GetLinuxInfoUtil;

public class LinuxServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LinuxServlet() {
		// TODO Auto-generated constructor stub
		super.setServlet(this);
	}
	@InjectDao
	private ServerDao serverDao;
	
	@ExecuteRequest
	public void getList(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			ajaxData.put("data", new ArrayList<Object>());
			return;
		}
		
		List<LinuxInfo> infos = space.getLinuxInfos();
		for (final LinuxInfo info:infos) {
			//info.setInfo();
			ServletUtil.execThread(new Runnable() {				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					info.setInfo();					
				}
			});
		}
		
		ajaxData.put("data", infos);	
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
	}
		
	/*此方法需要更改：执行命令时生成一个唯一标识返回给前台，强制停止时也需要验证此标识，防止不同用户同时执行命令影响*/
	@ExecuteRequest
	public void stopExec(Map<String, Object> ajaxData, HttpServletRequest request) {
		ServletUtil.getContext().setAttribute(LinuxConstant.STOP_EXEC_COMMAND_FLAG_ATTRIBUTE, "true");
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
	}
	
	@ExecuteRequest
	public void execCommand(Map<String, Object> ajaxData, HttpServletRequest request
			, @RequestBody("id") Integer id, @RequestBody("command")String command
			, @RequestBody("userKey")String userKey) {				
		
		if (StringUtils.isEmpty(command)) {
			ajaxData.put("msg", "执行命令不能为空!");
			return;
		}
		
		if (GetLinuxInfoUtil.checkCommand(command)) {
			ajaxData.put("msg", "禁止执行该命令!");
			return;
		}
		
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		LinuxInfo info = space.getLinuxInfo(id);
		
		if (info == null) {
			ajaxData.put("msg", "没有该主机信息或者该主机已被删除, 请尝试重新打开此页面!");
			return;
		}
			
		try {
			String returnInfo = GetLinuxInfoUtil.execCommand(info.getConn(), command, 9999999, ServletUtil.getContext(), 2);
			ajaxData.put("returnInfo", returnInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ajaxData.put("msg", "执行命令过程中发生意外错误:" + e.getMessage());
			return;
		}
		
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
	}
	
	
	
	@ExecuteRequest
	public void delAll(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		List<LinuxInfo> list = space.getLinuxInfos();
		
		for (LinuxInfo info:list) {
			info.stop();
			info.disconect();
		}
		
		list.clear();
			
		ajaxData.put("returnCode", LinuxConstant.CORRECT_RETURN_CODE);
	}
	
	@ExecuteRequest
	public void del(Map<String, Object> ajaxData, HttpServletRequest request
			, @RequestBody("id")Integer id, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		LinuxInfo info = space.getLinuxInfo(id);
		
		if (info == null) {
			ajaxData.put("msg", "该主机或者应用可能已被删除，请尝试刷新表格查看!");
			return;
		}
		
		info.stop();
		info.disconect();
		
		space.getLinuxInfos().remove(info);
		
		ajaxData.put("returnCode",  LinuxConstant.CORRECT_RETURN_CODE);
		
	}
	
	@ExecuteRequest
	public void reconnect(Map<String, Object> ajaxData, HttpServletRequest request
			, @RequestBody("id")Integer id, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		LinuxInfo info = space.getLinuxInfo(id);
		
		if (info == null) {
			ajaxData.put("msg", "该主机或者应用可能已被删除，请尝试刷新表格查看!");
			return;
		}
		
		info.stop();
		info.disconect();
		
		ajaxData.put("returnCode",  LinuxConstant.CORRECT_RETURN_CODE);
		try {			
			info.conect();
			//info.start(getServletContext());			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LOGGER.error("无法连接到主机" + info.getHost() + "：" + info.getErrorInfo(), e);
			ajaxData.put("returnCode",  LinuxConstant.ERROR_RETURN_CODE);
			ajaxData.put("msg", "重新创建连接失败：" + info.getErrorInfo());
		}			
	}
	
	/**
	 * 使用历史记录
	 * @param ajaxData
	 * @param request
	 * @param id
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
		LinuxInfo info = null;
		try {
			info = new LinuxInfo(serverDao.findById(id));
			info.setId(++DcitsUtil.id);
			info.conect();
    		//info.start(ServletUtil.getContext()); 
    		
    		space.getLinuxInfos().add(info);
    		serverDao.updateTime(id);			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("returnCode", Constants.ERROR_RETURN_CODE);
			ajaxData.put("msg", "在启用监控时发生了错误：" + info.getErrorInfo());
		}
	}
	
	/******************************************************************************/
	public void setServerDao(ServerDao serverDao) {
		this.serverDao = serverDao;
	}
	
	public ServerDao getServerDao() {
		return serverDao;
	}

}
