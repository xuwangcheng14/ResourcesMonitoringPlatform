package com.dcits.consts;

/**
 * 
 * @author Administrator
 * @version 20170224
 */
public class LinuxConstant {
	
	/**
	 * context attribbuteName
	 */
	
	/**
	 * 全局信息-服务器主机信息列表的键值
	 * <br> linuxInfos
	 */
	public static final String LINUX_INFO_LIST_ATTRIBUTE = "linuxInfos";
	
	/**
	 * 全局信息-最近的服务器资源信息列表键值 
	 * <br>realTimeInfos
	 */
	public static final String LASTLY_REAL_TIME_INFO_LIST_ATTRIBUTE = "realTimeInfos";
	
	/**
	 * 针对正在执行的命令,通过此属性判断是否中断执行<br>
	 * stopExec
	 */
	public static final String STOP_EXEC_COMMAND_FLAG_ATTRIBUTE = "stopExec";
	
	/**
	 * url  /linux
	 */
	
	/**
	 * 获取实时资源情况的url 
	 * <br>/linux/getInfo
	 */
	public static final String GET_REAL_TIME_INFO_URL = "getInfo";
	
	/**
	 * 获取主机列表的url 
	 * <br>/linux/getInfo
	 */
	public static final String GET_SERVER_INFO_LIST = "getServerList";
	
	/**
	 * 保存新的服务器主机信息url
	 *  <br>/linux/saveServer
	 */
	public static final String ADD_LINUX_SERVER_URL = "saveServer";
	
	/**
	 * 删除服务器主机信息
	 *  <br>/linux/delServer
	 */
	public static final String DEL_LINUX_SERVER_URL = "delServer";
	
	/**
	 * 重新连接主机
	 *  <br>/linux/reconnect
	 */
	public static final String RECONNECT_LINUX_SERVER_URL = "reconnect";
	
	/**
	 * 删除所有主机
	 * <br>/linux/clearAll
	 */
	public static final String CLEAR_ALL_LINUX_SERVER_URL = "clearAll";
	
	/**
	 * 执行主机命令
	 * <br>/linux/execCommand
	 */
	public static final String EXEC_COMMAND_URL = "execCommand";
	
	/**
	 * 中断正在执行的命令
	 * <br>/linux/stopExecCommand
	 */
	public static final String STOP_EXEC_COMMAND_URL = "stopExecCommand";
	
	
	/**
	 * 通用：获取历史服务器信息<br>
	 * /linux/history?type=
	 */
	public static final String GET_HISTORY_SERVER_INFO_URL = "history";
	
	/**
	 * 删除指定的历史信息<br>
	 *  /linux/delHistory?id=
	 */
	public static final String DEL_HISTORY_SERVER_INFO_URL = "delHistory";
	
	/**
	 * 使用历史服务器信息<br>
	 * /linux/useHistory?id=
	 */
	public static final String USE_HISTORY_SERVER_INFO = "useHistory";
	
	
	/**
	 * false
	 */
	public static final int ERROR_RETURN_CODE = 1;
	
	/**
	 * true
	 */
	public static final int CORRECT_RETURN_CODE = 0;
	
	/**
	 * 保存主机信息模式为单条保存
	 * <br>single
	 */
	public static final String SAVE_MODE_SINGLE = "single";
	
	/**
	 * 保存主机信息模式为批量保存
	 * <br>batch
	 */
	public static final String SAVE_MODE_BATCH = "batch";
	
	
}
