package com.dcits.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import xuwangcheng.love.w.servlet.AbstractHttpServlet;
import xuwangcheng.love.w.servlet.annotation.ExecuteRequest;
import xuwangcheng.love.w.servlet.annotation.InjectDao;
import xuwangcheng.love.w.servlet.annotation.RequestBody;
import xuwangcheng.love.w.util.Constants;

import com.dcits.bean.LinuxInfo;
import com.dcits.bean.ServerInfo;
import com.dcits.bean.WeblogicInfo;
import com.dcits.bean.util.AnalyzeData;
import com.dcits.bean.util.ExportFileInfo;
import com.dcits.bean.util.LeaveMessage;
import com.dcits.bean.util.UserSpace;
import com.dcits.dao.LeaveMessageDao;
import com.dcits.dao.ServerDao;
import com.dcits.util.DcitsUtil;
import com.dcits.util.ServletUtil;

public class ServerServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ServerServlet() {
		// TODO Auto-generated constructor stub
		super.setServlet(this);
	}
	
	@InjectDao
	private ServerDao serverDao;
	@InjectDao
	private LeaveMessageDao leaveMessageDao;
	
	public void setLeaveMessageDao(LeaveMessageDao leaveMessageDao) {
		this.leaveMessageDao = leaveMessageDao;
	}
	
	public LeaveMessageDao getLeaveMessageDao() {
		return leaveMessageDao;
	}
	
	public void setServerDao(ServerDao serverDao) {
		this.serverDao = serverDao;
	}
	
	public ServerDao getServerDao() {
		return serverDao;
	}
	
	/**
	 * 留言列表
	 * @param ajaxData
	 * @param request
	 */
	@ExecuteRequest
	public void listMsg(Map<String, Object> ajaxData, HttpServletRequest request) {
		List<LeaveMessage>  msgs = new ArrayList<LeaveMessage>();
		try {
			msgs = leaveMessageDao.list();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
		ajaxData.put("data", msgs);
	}
	
	/**
	 * 保存留言
	 * @param ajaxData
	 * @param request
	 * @param userKey
	 * @param content
	 */
	@ExecuteRequest
	public void saveMsg(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("userKey")String userKey
				, @RequestBody("content")String content) {
		if (StringUtils.isEmpty(userKey)) {
			userKey = "游客";
		}
		
		LeaveMessage msg = new LeaveMessage(userKey, content, DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN));
		
		try {
			leaveMessageDao.saveMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("msg", "保存数据库出错:" + e.getMessage());
			return;
		}
		
		ajaxData.put("message", msg);
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
	}
	
	
	@ExecuteRequest
	public void delAllMsg(Map<String, Object> ajaxData, HttpServletRequest request) {
		int ret = 0;
		try {
			ret = leaveMessageDao.delAll();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		ajaxData.put("count", ret);
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
	}
	
	/**
	 * 导出数据
	 * @param ajaxData
	 * @param request
	 * @param dates
	 * @param serverInfos
	 */
	@ExecuteRequest
	public void exportData(Map<String, Object> ajaxData, HttpServletRequest request){
		String body = "";
		
		try {
			body = URLDecoder.decode(ServletUtil.getBody(request), "UTF-8").replaceAll("=", ":").replaceAll("&", ",");//
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Pattern r = Pattern.compile("userKey:(.*)");
		Matcher m = r.matcher(body);
		String userKey = "";
		if (m.find()) {
			userKey = m.group(1);
		}				
		
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}	
		
		
		r = Pattern.compile("recordCount:(.*),");
		m = r.matcher(body);
		String recordCount = "";
		if (m.find()) {
			recordCount = m.group(1);
		}
									
		String rootPath = request.getSession().getServletContext().getRealPath("");

		String fileName = "infos_" + System.currentTimeMillis() + "_" + userKey + ".json";
		String filePath = "infos/" + fileName;
		
		File infosFile = new File(rootPath + "/" + filePath);
		
		BufferedOutputStream buff = null;
		Map<String, Object> serverList = new HashMap<String, Object>();
		serverList.put("linux", space.getLinuxInfos());
		serverList.put("weblogic", space.getWeblogicInfos());
		serverList.put("jvm", space.getJvmInfos());
		
		JSONObject jo = JSONObject.fromObject(serverList);
		String serverLists = jo.toString();
		
		
		String content = "{\"serverList\":" + serverLists + "," + body.replace("dates:", "\"dates\":")
				.replace("serverInfos:", "\"serverInfos\":").replaceAll(",recordCount(.*)", "") + "}";					
		try {
			buff = new BufferedOutputStream(new FileOutputStream(infosFile));			
			buff.write(content.getBytes("UTF-8"));
			buff.flush();						
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("msg", "服务端写文件出错!");
			return;
		} finally {
			if (buff != null) {
				try {
					buff.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		ExportFileInfo fileInfo = new ExportFileInfo();
		fileInfo.setExportTime(DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN));
		fileInfo.setFileName(fileName);
		fileInfo.setFilePath(filePath);
		fileInfo.setRecordCount(recordCount);
		fileInfo.setServerCount(space.getCount());
		String fileSize = "";
		if (infosFile.length() > 1024 * 1024) {
			fileSize = DcitsUtil.byteToMB(infosFile.length()) + "MB";
		} else {
			fileSize = DcitsUtil.byteToKB(infosFile.length()) + "KB";
		}
		
		fileInfo.setFileSize(fileSize);
		
		space.getExportFileInfos().add(fileInfo);
		ajaxData.put("fileName", fileName);
		ajaxData.put("filePath", "./" + filePath);
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
		
	}
	
	/**
	 * 获取指定路径的文件信息
	 * @param ajaxData
	 * @param request
	 * @param userKey
	 */
	@ExecuteRequest
	public void getFileInfo(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("filePath")String filePath) {
		File file = new File(request.getSession().getServletContext().getRealPath("/") + filePath);
		if (!file.exists()) {
			ajaxData.put("msg", "该文件不存在,请刷新表格重试!");
			return;
		}
		
		//读取文件内容并返回给前台
		List<String> strs = null;
		try {
			strs = IOUtils.readLines(new FileInputStream(file), "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ajaxData.put("msg", "读取文件出错,请重试");
			return;
		}
		
		
		ajaxData.put("infos", strs.get(0));
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);	
	}
	
	
	/**
	 * 获取当前userSpace空间下的历史导出文件列表
	 * @param ajaxData
	 * @param request
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ExecuteRequest
	public void getFileList(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		List<ExportFileInfo> infos = space.getExportFileInfos();
		
		//按导出日期排序
		Collections.sort(infos, new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				SimpleDateFormat format = new SimpleDateFormat(DcitsUtil.FULL_DATE_PATTERN);
				long a1 = 0L;
				long a2 = 0L;
				try {
					a1 = format.parse(((ExportFileInfo) o1).getExportTime()).getTime();
					a2 = format.parse(((ExportFileInfo) o2).getExportTime()).getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return (int) (a2 -a1);
			}
			
		});
		
		ajaxData.put("data", space.getExportFileInfos());
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);	
	}
	
	/**
	 * 设置userKey<br>
	 * 6-8位数字
	 * @param ajaxData
	 * @param request
	 * @param userKey
	 */
	@ExecuteRequest
	public void setUserKey(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("userKey")String userKey ) {
		String pattern = "^[A-Za-z0-9_]{6,20}$";
		if (!Pattern.matches(pattern, userKey)) {
			ajaxData.put("msg", "必须为6-20位且只能为字母、数字、下划线!");
			return;
		}
		
		if (ServletUtil.getUserSpace(userKey) == null) {
			ServletUtil.addUserSpace(userKey);
		}
		
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);		
	}
	
	/**
	 * 获取当前所有的UserSpace信息
	 * @param ajaxData
	 * @param request
	 */
	@ExecuteRequest
	public void getUserSpaceList(Map<String, Object> ajaxData, HttpServletRequest request) {
		List<String[]> spaces  = new ArrayList<String[]>();
		
		for (UserSpace space:ServletUtil.getUserSpaces().values()) {
			String[] s = new String[3];
			s[0] = space.getUserKey();
			s[1] = String.valueOf(space.getCount());
			s[2] = space.getCreateTime();
			
			spaces.add(s);
		}
		
		ajaxData.put("data", spaces);
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);	
	}
	
	
	/**
	 * 根据条件展示服务器的信息
	 * @param ajaxData
	 * @param request
	 * @param type
	 */
	@ExecuteRequest("list")
	public void getList(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("type")String type ) {
		List<ServerInfo> infos = new ArrayList<ServerInfo>();
		try {
			if (type != null) {
				infos = serverDao.findByType(type);
			} else {
				infos = serverDao.findAll();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
		ajaxData.put("data", infos);
	}
	
	
	@SuppressWarnings({"unchecked" })
	@ExecuteRequest
	public void analyzeData(Map<String, Object> ajaxData, HttpServletRequest request) {
		String body = "";
		
		try {
			body = URLDecoder.decode(ServletUtil.getBody(request), "UTF-8").replaceAll("=", ":").replaceAll("&", ",");//
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Map maps = null;
		try {
			maps = new ObjectMapper().readValue("{" + body.replace("analyzeServerList", "\"analyzeServerList\"")
						.replace("analyzeItems", "\"analyzeItems\"").replace("serverInfos", "\"serverInfos\"")
						.replace("dates", "\"dates\"") + "}", Map.class);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("msg", "解析json出错:" + e.getMessage());
			return;		
		}
		
		List<AnalyzeData> analyzeDatas = new ArrayList<AnalyzeData>();
		Map<String, List<String>> itemInfos = (Map<String, List<String>>) maps.get("analyzeItems");
		Map<String, List<String>> dates = (Map<String, List<String>>) maps.get("dates");
		Map<String, Map<String, Object>> datas = (Map<String, Map<String, Object>>) maps.get("serverInfos");
		
		int linuxCount = 0;
		int jvmCount = 0;
		int weblogicCount = 0;
		
		for (Map<String, Object> serverInfo:(List<Map<String, Object>>)maps.get("analyzeServerList")) {
			AnalyzeData ad = new AnalyzeData();
			ad.setRuleItemStr(serverInfo);
			ad.analyzeData(itemInfos, datas, dates);
			
			switch (ad.getServerType()) {
			case "linux":
				linuxCount++;
				break;
			case "jvm":
				jvmCount++;
				break;
			case "weblogic":
				weblogicCount++;
				break;
			default:
				break;
			}
			
			analyzeDatas.add(ad);
		}
		
		
		ajaxData.put("linuxCount",linuxCount);
		ajaxData.put("jvmCount",jvmCount);
		ajaxData.put("weblogicCount",weblogicCount);
		ajaxData.put("result", analyzeDatas);
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
	}
	
	
	/**
	 * 更新或者添加信息
	 * @param ajaxData
	 * @param request
	 * @param serverId
	 */
	@ExecuteRequest
	public void edit(Map<String, Object> ajaxData, HttpServletRequest request
			, @RequestBody("serverId")Integer serverId, @RequestBody("userKey")String userKey) {
		
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		ServerInfo info = new ServerInfo();
		
		info.setHost(request.getParameter("host"));
		info.setPort(request.getParameter("port"));
		info.setUsername(request.getParameter("username"));
		info.setPassword(request.getParameter("password"));
		info.setMark(request.getParameter("mark"));
		info.setType(request.getParameter("type"));
		info.setParameters(request.getParameter("parameters"));
		info.setTags(request.getParameter("tags"));
		info.setRealHost(request.getParameter("realHost"));
		
		if ("0".equals(info.getType()) && StringUtils.isEmpty(info.getPort())) {
			info.setPort("22");
		}
		
		Integer ret = null;
		String msg = "";
		
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
		
		try {
			if (serverId == 0) {
				//添加
				if (serverDao.checkExistServerInDB(info) == null) {
					ret = serverDao.saveServer(info);
				}				
			} else {
				//更新
				info.setServerId(serverId);
				ret = serverDao.updateServer(info);
				//同时更新正在使用中的监控主机信息-附加参数
				space.updateParameters(info);				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("returnCode", Constants.ERROR_RETURN_CODE);
			msg = "操作数据库时发生了错误,详情：" + e.getMessage();			
		}
		
		if (ret == null || ret < 1) {
			ajaxData.put("returnCode", Constants.ERROR_RETURN_CODE);
			msg = "更新或者添加到数据库失败,可能已有重复记录或者记录已被删除!";
		}
		
		ajaxData.put("msg", msg);
		
	}
	
	/**
	 * 删除
	 * @param ajaxData
	 * @param request
	 * @param serverId
	 */
	@ExecuteRequest
	public void del(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("serverId")Integer serverId) {
		
		if (serverId == 0) {
			ajaxData.put("returnCode", Constants.ERROR_RETURN_CODE);
			ajaxData.put("msg", "入参不正确!");
		}
			
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
		try {
			serverDao.delServer(serverId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ajaxData.put("returnCode", Constants.ERROR_RETURN_CODE);
			ajaxData.put("msg", "操作数据库时发生了错误：" + e.getMessage());
		}
		
	}
	
	/**
	 * 批量添加
	 * @param ajaxData
	 * @param request
	 * @param infos
	 * @param type
	 */
	@ExecuteRequest
	public void batchSave(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("infos")String infos
			, @RequestBody("type") String type, @RequestBody("testFlag")boolean flag, @RequestBody("userKey")String userKey) {
		UserSpace space = ServletUtil.getUserSpace(userKey);
		
		if (space == null) {
			ajaxData.put("msg", "userKey不正确!");
			return;
		}
		
		if (StringUtils.isEmpty(infos)) {
			ajaxData.put("msg", "缺少必要参数!");
			return;
		}
		String serverType = "0";
		if (StringUtils.isNotEmpty(type)) {
			serverType = type;
		}
		
		String[] servers = infos.split("\\n");
		int successCount = 0;
		boolean isOk = true;
		for (String server:servers) {
			isOk = true;
			String[] serverInfo = server.split("[,，]");
			
			if (serverInfo.length < 4) {
				continue;
			}
			ServerInfo info = new ServerInfo();
			info.setHost(serverInfo[0]);
			info.setPort(serverInfo[1]);
			info.setUsername(serverInfo[2]);
			info.setPassword(serverInfo[3]);
			info.setType(serverType);
			if (serverInfo.length > 4) {
				info.setMark(serverInfo[4]);
			}	
			Map<String, String> maps = new HashMap<String, String>();
			
			//linux
			if ("0".equals(info.getType()) && (serverInfo.length > 5)) {
				maps.put("javaHome", serverInfo[5]);
			}
			
			//weblogic
			if ("1".equals(info.getType())) {
				if (serverInfo.length > 5) {
					maps.put("linuxLoginUsername", serverInfo[5]);
				}
				
				if (serverInfo.length > 6) {
					maps.put("linuxLoginPassword", serverInfo[6]);
				}
				
				if (serverInfo.length > 7) {
					maps.put("javaHome", serverInfo[7]);
				}
			}
			
			JSONObject jstr = JSONObject.fromObject(maps);
			info.setParameters(jstr.toString());
			
			try {
				
				if (flag == true) {
					switch (serverType) {
					case "0":
						LinuxInfo linux = new LinuxInfo(info);	
						linux.conect();
						if (linux.getConn() != null) {
							linux.setId(++DcitsUtil.id);
							//linux.start(ServletUtil.getContext());
							space.getLinuxInfos().add(linux);
						} else {
							isOk = false;
						}
						break;
					case "1":
						WeblogicInfo weblogic = new WeblogicInfo(info);
						weblogic.connect();
						
						if ("true".equals(weblogic.getConnectStatus())) {
							weblogic.setId(++DcitsUtil.id);
							space.getWeblogicInfos().add(weblogic);
						} else {
							isOk = false;
						}
						break;
					default:
						break;
					}				
				}	
				
				if (serverDao.checkExistServerInDB(info) == null && isOk) {
					serverDao.saveServer(info);
					successCount++;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}		
			
		}
		
		ajaxData.put("returnCode", Constants.CORRECT_RETURN_CODE);
		ajaxData.put("msg", "本次共成功添加" + successCount + "条服务器信息!");
	}
}
