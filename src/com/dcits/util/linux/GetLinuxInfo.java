package com.dcits.util.linux;

import java.io.IOException;
import java.util.Map;

import com.dcits.bean.LinuxInfo;
import com.dcits.bean.linux.RealTimeInfo;
import com.dcits.consts.CommandConstant;
import com.dcits.util.linux.parse.ParseInfo;
import com.dcits.util.linux.parse.ParseInfoFactory;

/**
 * 执行相关命令返回linux资源情况
 * 使用到的命令：sar netstat awk vmstat df iostat
 * 获取返回的信息
 * @author xuwangcheng
 * @version 20170224
 *
 */
public class GetLinuxInfo implements Runnable {
	
	/**
	 * 执行命令的时间间隔 默认3s
	 */
	private static final String INTERVAL_TIME = "2";

	private LinuxInfo info;
	
	private String str = "";
	
	@Override
	public void run() {
		// TODO Auto-generated method stub	
		String intervalTime = INTERVAL_TIME;		
		RealTimeInfo realTimeInfo = info.getInfo();	
		
		if (this.info.getOptions().get("intervalTime") != null) {
			intervalTime = this.info.getOptions().get("intervalTime");
		}
		
		
		//实例化不同类型主机的信息解析类实例
		ParseInfo parseUtil = ParseInfoFactory.getInstance(info.getUanme());
		SSHBufferedReader vmstatBrStat = null;
		try {
			Map<String, String> commandMap = info.getCommandMap();
			
			vmstatBrStat = GetLinuxInfoUtil.execLoopCommand(info.getConn(), commandMap.get(CommandConstant.VMSTAT) + " " + intervalTime);

			//vmstat前三行不读
			vmstatBrStat.readLine();	
			vmstatBrStat.readLine();	
			vmstatBrStat.readLine();
			
			//io前一行不读
			//ioBrStat.readLine();
			
			while (!this.info.isStopFlag()) {
				
				//vmstat读取cpu和内存
				str = vmstatBrStat.readLine();
				
				while (str == null || str.contains("memory") || str.contains("free") || str.isEmpty()) {
					str = vmstatBrStat.readLine();
				}								

				parseUtil.parseInfo(str, info);				
				

				//处理tcp端口
				parseUtil.parseTcpInfo(GetLinuxInfoUtil.execCommand(info.getConn()
						, commandMap.get(CommandConstant.GET_TCP_PORT_COUNT), 1, null, 0), realTimeInfo);
				
				//处理网络带宽
				parseUtil.parseNetworkInfo(GetLinuxInfoUtil.execCommand(info.getConn()
						, commandMap.get(CommandConstant.GET_NETWORK_INFO), 1, null, 0), realTimeInfo);
				
				//处理磁盘空间使用信息 匹配 /和/username挂载的磁盘
				//while ((str = diskBrStat.readLine()).isEmpty()) {}	
				parseUtil.parseDiskInfo(GetLinuxInfoUtil.execCommand(info.getConn()
						, commandMap.get(CommandConstant.GET_DISK_INFO), 1, null, 0), realTimeInfo);
				
				//处理磁盘io	
				this.info.setConnectStatus("true");
			}				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.info.setConnectStatus("获取信息发生错误:" + e.getMessage());
			this.info.disconect();
			try {
				this.info.conect();
				//this.info.start(context);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			this.info.setStopFlag(false);						
			if (vmstatBrStat != null) {
				try {
					vmstatBrStat.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	public GetLinuxInfo(LinuxInfo info) {
		super();
		this.info = info;
	}

}
