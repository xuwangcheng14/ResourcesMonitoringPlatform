package com.dcits.util.linux;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dcits.bean.LinuxInfo;
import com.dcits.bean.linux.RealTimeInfo;
import com.dcits.consts.CommandConstant;
import com.dcits.util.DcitsUtil;
import com.dcits.util.linux.parse.ParseInfo;
import com.dcits.util.linux.parse.ParseInfoFactory;

public class GetLinuxInfo {
	
	public static void getRealTimeInfo (LinuxInfo info) {
		//实例化不同类型主机的信息解析类实例
		ParseInfo parseUtil = ParseInfoFactory.getInstance(info.getUanme());
		
		Map<String, String> commandMap = info.getCommandMap();
		
		RealTimeInfo realTimeInfo = info.getInfo();	
		
		String host = info.getHost();
		if (StringUtils.isNotBlank(info.getRealHost())) {
			host = info.getRealHost();
		}
		try {
			//cpu  内存信息
			parseUtil.parseInfo(GetLinuxInfoUtil.execCommand(info.getConn()
					, commandMap.get(CommandConstant.VMSTAT), 5, null, 0, ""), info);				
			

			//处理tcp端口
			parseUtil.parseTcpInfo(GetLinuxInfoUtil.execCommand(info.getConn()
					, commandMap.get(CommandConstant.GET_TCP_PORT_COUNT), 1, null, 0, ""), realTimeInfo);
			
			//处理网络带宽
			parseUtil.parseNetworkInfo(GetLinuxInfoUtil.execCommand(info.getConn()
					, commandMap.get(CommandConstant.GET_NETWORK_INFO).replace("HOSTNAME", host), 1, null, 0, ""), realTimeInfo);
			
			//处理磁盘空间使用信息 匹配 /和/username挂载的磁盘
			//while ((str = diskBrStat.readLine()).isEmpty()) {}	
			parseUtil.parseDiskInfo(GetLinuxInfoUtil.execCommand(info.getConn()
					, commandMap.get(CommandConstant.GET_DISK_INFO), 1, null, 0, ""), realTimeInfo);
			
			realTimeInfo.setTime(DcitsUtil.getCurrentTime(DcitsUtil.DEFAULT_DATE_PATTERN));	
			info.setConnectStatus("true");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			info.setConnectStatus("获取信息发生错误:" + e.getMessage());
			info.disconect();			
		}
		
	}
}
