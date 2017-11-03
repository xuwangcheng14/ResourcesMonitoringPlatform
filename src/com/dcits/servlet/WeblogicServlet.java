package com.dcits.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import xuwangcheng.love.w.servlet.AbstractHttpServlet;
import xuwangcheng.love.w.servlet.annotation.ExecuteRequest;
import xuwangcheng.love.w.servlet.annotation.InjectDao;
import xuwangcheng.love.w.servlet.annotation.RequestBody;
import xuwangcheng.love.w.util.Constants;

import com.dcits.bean.JvmInfo;
import com.dcits.bean.WeblogicInfo;
import com.dcits.bean.util.UserSpace;
import com.dcits.consts.LinuxConstant;
import com.dcits.dao.ServerDao;
import com.dcits.util.DcitsUtil;
import com.dcits.util.ServletUtil;
import com.dcits.util.linux.GetLinuxInfoUtil;

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

	
	/**
	 * 重启weblogic
	 * @param ajaxData
	 * @param request
	 * @param weblogicId
	 * @param userKey
	 */
	@ExecuteRequest
	public void reboot(Map<String, Object> ajaxData, HttpServletRequest request
			, @RequestBody("id") Integer weblogicId, @RequestBody("userKey")String userKey) {
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
		
		//检查是否有对应的parameters参数
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
		
		//开始重启
		//通过kill -9 停止
		JvmInfo jvmInfo = space.getJvmInfo(info.getJvmId());
		if (jvmInfo == null) {
			Object[] os = DcitsUtil.addWeblogicJvm(info);
			if (!(boolean)os[0]) {
				ajaxData.put("msg", (String)os[1]);
				return;
			}
			jvmInfo = (JvmInfo)os[2];
		}
		String killMsg = "";
		//先从列表中删除该weblogic
		space.getWeblogicInfos().remove(info);
		try {
			killMsg = GetLinuxInfoUtil.execCommand(jvmInfo.getConn(), "kill -9 " + jvmInfo.getPid(), 1, null, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ajaxData.put("msg", "执行kill -9命令出现错误:" + e.getMessage());
			return;
		}
		
		if (!StringUtils.isEmpty(killMsg)) {
			ajaxData.put("msg", "无法通过kill命令停止该weblogic进程:" + killMsg);
			return;
		}
		
		//停止成功，开始重启
		String scriptPath = (String) maps.get("startScriptPath");
		
		if (StringUtils.isEmpty(scriptPath)) {
			ajaxData.put("msg", "没有配置该weblogic附加参数的startScriptPath(weblogic启动脚本绝对路径)");
			return;
		}
		
		String msg = "";
		try {
			GetLinuxInfoUtil.execCommand(jvmInfo.getConn(), "sh " + scriptPath, 99, null, 2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("msg", "启动该weblogic失败");
			return;
		} 
		
		jvmInfo.setPid("");
		int count = 0;
		while (StringUtils.isEmpty(jvmInfo.getPid())) {						
			try {			
				Thread.sleep(5000);
				jvmInfo.setPid();					
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				continue;
			}	
			count++;
			if (count >= 60) {
				ajaxData.put("msg", "启动该weblogic失败，请至主机查看相关日志");
				return;
			}
			
		} 
		//重连weblogic
		info.connect();
		//重新加入到列表
		space.getWeblogicInfos().add(info);
		
		ajaxData.put("msg", msg);
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
	}
	
	
	/***************************************************************/
	public ServerDao getServerDao() {
		return serverDao;
	}



	public void setServerDao(ServerDao serverDao) {
		this.serverDao = serverDao;
	}

}
