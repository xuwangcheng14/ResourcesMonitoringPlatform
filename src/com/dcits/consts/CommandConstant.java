package com.dcits.consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取信息的相关命令<br>
 * 注意：为方便解析，所有的命令应该按行返回，信息以行为单位，以行进行解析
 * @author xuwangcheng
 * @version 20170317
 *
 */
public class CommandConstant {
	
	/**
	 * linux类型主机可用命令
	 */
	public static final Map<String, String> LINUX_COMMAND_MAP = new HashMap<String, String>();
	
	/**
	 * HP-UX类型主机可用命令
	 */
	public static final Map<String, String> HP_COMMAND_MAP = new HashMap<String, String>();
	
	/**
	 * SunOS类型主机可用命令
	 */
	public static final Map<String, String> SUN_COMMAND_MAP = new HashMap<String, String>();
	
	/**
	 * 禁止用户通过前台执行的命令
	 */
	public static final List<String> FORBID_EXEC_COMMANDS = new ArrayList<String>();

	
	/**
	 * 获取机器的类型<br>
	 * 目前可判断的为：Linux/HP-UX/SunOS<br>
	 * 返回：机器类型
	 */
	public static final String GET_UNAME = "uname";
	
	/**
	 * 通用命令获取实时 cpu和内存信息<br>
	 * HP-UX,SunOS和Linux的返回需要不同解析
	 */
	public static final String VMSTAT = "vmstat";
	
	/**
	 * 获取cpu的核数<br>
	 * 返回：cpu个数
	 */
	public static final String GET_CPU_INFO = "getCpuInfo";
	
	/**
	 * 获取主机内存<br>
	 * 返回：内存量 kb单位
	 */
	public static final String GET_MEMORY_INFO = "getMemoryInfo";
	
	/**
	 * 获取所有网卡名称<br>
	 * 返回：eth0,eth1,
	 */
	public static final String GET_NETWORK_CARD = "getNetworkCard";
	
	/**
	 * 获取当前各种连接状态的tcp端口数量<br>
	 * 返回：	7 ESTABLISHED,7 LISTEN
	 * 
	 */
	public static final String GET_TCP_PORT_COUNT = "getTcpPortCount";
	
	/**
	 * 获取当前网络带宽情况<br>
	 * 只获取出网和入网的速率  单位KB <br>
	 * 返回：0.11 0.00,0.11 0.00,0.11 0.00,
	 */
	public static final String GET_NETWORK_INFO = "getNetworkInfo";
	
	/**
	 * 获取当前磁盘的使用率，百分比<br>
	 * 通过grep只查找根目录和用户名目录挂载的<br>
	 * 返回：/dev/vda1        20G  4.4G   15G  24% / ,/dev/vda1        20G  4.4G   15G  24% /root<br>
	 * 注意：解析的时候需要考虑换行的情况<br><br>
	 * 
	 * 
	 */
	public static final String GET_DISK_INFO = "getDiskInfo";
	
	/**
	 * 获取当前主机上运行的weblogic实例个数<br>
	 * 返回：实例个数
	 * 
	 */
	public static final String GET_WEBLOGIC_COUNT = "getWeblogicCount";
	
	static {
		/*************Linux******************/
		LINUX_COMMAND_MAP.put(GET_UNAME, "uname");
		LINUX_COMMAND_MAP.put(VMSTAT, "vmstat 1 2|sed -n '4p'");//top -b -n 1|sed -n '3,5p'
		LINUX_COMMAND_MAP.put(GET_CPU_INFO, "cat /proc/cpuinfo | grep processor | wc -l");
		LINUX_COMMAND_MAP.put(GET_MEMORY_INFO, "cat /proc/meminfo |grep MemTotal| awk '{print $2}'");
		LINUX_COMMAND_MAP.put(GET_NETWORK_CARD, "ifconfig -a|grep \"eth|lo\" |awk '{print $1}'|tr '\n' ',';echo");
		LINUX_COMMAND_MAP.put(GET_TCP_PORT_COUNT, "netstat -an|awk '/tcp/ {print $6}'|sort|uniq -c |tr '\n' ',';echo");
//		LINUX_COMMAND_MAP.put(GET_NETWORK_INFO, "sar -n DEV 1 1|grep eth*|grep -v Average|awk '{print $6,$7}'|tr '\n' ',';echo");
		LINUX_COMMAND_MAP.put(GET_NETWORK_INFO, "sar -n DEV 1 1|grep `/sbin/ifconfig -a|grep -B1 HOSTNAME|sed -n '1p'|awk '{print $1}'`|grep -v Average|awk '{print $5,$6}'");
		LINUX_COMMAND_MAP.put(GET_DISK_INFO, "df -h |egrep \"/$|($HOME)$\"|tr '\n' ',';echo");
		LINUX_COMMAND_MAP.put(GET_WEBLOGIC_COUNT, "ps -ef|grep java|grep weblogic|grep -v grep|wc -l");
		
		/*****************HP-UX**********************/
		HP_COMMAND_MAP.put(GET_UNAME, "uname");
		HP_COMMAND_MAP.put(VMSTAT, "vmstat 1 2|sed -n '4p'");
		HP_COMMAND_MAP.put(GET_CPU_INFO, ""); //ioscan -fnCprocessor
		HP_COMMAND_MAP.put(GET_MEMORY_INFO, ""); //dmesg
		HP_COMMAND_MAP.put(GET_NETWORK_CARD, ""); //lanscan
		HP_COMMAND_MAP.put(GET_TCP_PORT_COUNT, ""); //netstat -in / lsof -i | grep LISTEN  /
		HP_COMMAND_MAP.put(GET_NETWORK_INFO, ""); //netstat -s / lanadmin  / netstat -I lan0 -i 5
		HP_COMMAND_MAP.put(GET_DISK_INFO, ""); //sar -d 
		HP_COMMAND_MAP.put(GET_WEBLOGIC_COUNT, "ps -ef|grep java|grep weblogic|grep -v grep|wc -l");
		
		/*****************SunOS**********************/
		SUN_COMMAND_MAP.put(GET_UNAME, "uname");
		SUN_COMMAND_MAP.put(VMSTAT, "vmstat 1 2|sed -n '4p'");
		SUN_COMMAND_MAP.put(GET_CPU_INFO, "mpstat|grep -v CPU|wc -l"); //psrinfo -v  mpstat|grep -v CPU|wc -l
		SUN_COMMAND_MAP.put(GET_MEMORY_INFO, "echo \"`/usr/sbin/prtconf | grep 'Memory' | awk '{print $3}' ` * 1024\" |bc"); //prtconf | grep 'Memory'
		SUN_COMMAND_MAP.put(GET_NETWORK_CARD, "netstat -in | grep -v Name | grep -v lo | grep -v '^$' | awk '{print $1}' | tr '\n' ',';echo"); //grep network /etc/path_to_inst
		SUN_COMMAND_MAP.put(GET_TCP_PORT_COUNT, "netstat -an -P tcp -f inet | awk '{print $7}' |sort|uniq -c |egrep 'ESTABLISHED|LISTEN|CLOSE_WAIT|TIME_WAIT' | tr '\n' ',';echo");//netstat -an
		SUN_COMMAND_MAP.put(GET_NETWORK_INFO, "");//netstat -in 
		SUN_COMMAND_MAP.put(GET_DISK_INFO, "df -h |egrep \"/$|($HOME)$\" | tr '\n' ',';echo"); //df –F ufs –o   /df -h
		SUN_COMMAND_MAP.put(GET_WEBLOGIC_COUNT, "ps -ef|grep java|grep weblogic|grep -v grep|wc -l");
		
		/*********************ForbidCommands*******************************/
		FORBID_EXEC_COMMANDS.add("logout");
		FORBID_EXEC_COMMANDS.add("rm");
		FORBID_EXEC_COMMANDS.add("exit");
		FORBID_EXEC_COMMANDS.add("kill");
		FORBID_EXEC_COMMANDS.add("ssh");
		FORBID_EXEC_COMMANDS.add("ftp");
		FORBID_EXEC_COMMANDS.add("quit");
	}
}
