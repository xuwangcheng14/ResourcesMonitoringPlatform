package com.dcits.util.linux.parse;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

import com.dcits.bean.LinuxInfo;
import com.dcits.bean.linux.RealTimeInfo;

/**
 * 不同类型的主机需要不同信息解析规则
 * @author xuwangcheng
 * @version 20170317
 *
 */
public abstract class ParseInfo {
	/**
	 * 解析vmstat返回的信息<br>
	 * 不同类型的主机可能都可以执行vmstat,但是返回的格式可能不相同<br>
	 * 不再使用长连接获取信息
	 * @param info
	 * @param realTimeInfo
	 * @return null
	 */
	
	public void parseInfo(String info, LinuxInfo linuxInfo){
		
		if ("".equals(info)) {
			return;
		}
		RealTimeInfo realTimeInfo = linuxInfo.getInfo();
		
		String[] infos = info.trim().split("(\\s)+");
		
		DecimalFormat formater = new DecimalFormat();  
		formater.setMaximumFractionDigits(1);  
		formater.setRoundingMode(RoundingMode.UP); 
		Double freeMem = 0.0;
		String idle = "";		
		
		if (linuxInfo.getUanme().equals("SunOS") || linuxInfo.getUanme().equals("HU-UX")) {
			freeMem = Double.parseDouble(infos[4]);
			idle = infos[infos.length - 1];
			realTimeInfo.setIoWait("");
		} else {
			freeMem = ((Double.valueOf(infos[3]) 
					+ Double.valueOf(infos[4]) 
					+ Double.valueOf(infos[5])));
			idle = infos[infos.length - 3];
			realTimeInfo.setIoWait(infos[infos.length - 2]);
		}		
		realTimeInfo.setFreeCpu(idle);
		realTimeInfo.setFreeMem(formater.format((freeMem / Double.parseDouble(linuxInfo.getMemInfo())) * 100));
				
		/*DecimalFormat formater = new DecimalFormat();  
		formater.setMaximumFractionDigits(1);  
		formater.setRoundingMode(RoundingMode.UP); 
		
		//top返回
		if (infos.length == 3) {
			String[] cpus = infos[0].split(",");
			String[] mems = infos[1].split(",");
			String[] swaps = infos[2].split("[,\\.]");
			
			realInfo.setFreeCpu(cpus[3].substring(0, cpus[3].indexOf("%")).trim());
			realInfo.setIoWait(cpus[4].substring(0, cpus[4].indexOf("%")).trim());
			Double freeMemKb = Double.parseDouble(mems[2].substring(0, mems[2].indexOf("k")).trim()) 
					+ Double.parseDouble(mems[3].substring(0, mems[3].indexOf("k")).trim())
					+ Double.parseDouble(swaps[3].substring(0, swaps[3].indexOf("k")).trim());
			
			realInfo.setFreeMem(formater.format(freeMemKb / Double.parseDouble(linuxInfo.getMemInfo()) * 100));
			
		}
		
		//vmstat返回
		if (infos.length == 1) {
			infos = infos[0].split("(\\s)+");
			
			realInfo.setFreeCpu(infos[infos.length - 1]);
			realInfo.setFreeMem(formater.format((Double.parseDouble(infos[4]) / Double.parseDouble(linuxInfo.getMemInfo())) * 100));
			realInfo.setIoWait("");
		}*/
		
			
	};
	
	/**
	 * 解析返回的tcp端口信息<br>
	 * @param info
	 * @return
	 */
	public void parseTcpInfo(String info, RealTimeInfo realTimeInfo) {
		Map<String, String> map = realTimeInfo.getTcpInfo();		
		
		if (info != null && !info.isEmpty()) {
			String[] strs = info.split(",");
			String[] ss = null;
			for (String s:strs) {
				ss = s.trim().split("\\s+");
				map.put(ss[1], ss[0]);
			}
						
		}
	};
	
	/**
	 * 解析返回的网络带宽信息<br>
	 * @param info
	 * @param realTimeInfo
	 */
	public void parseNetworkInfo(String info, RealTimeInfo realTimeInfo) {
		
	};
	
	/**
	 * 解析返回的磁盘空间信息<br>
	 * @param info
	 * @param realTimeInfo
	 */
	public void parseDiskInfo(String info, RealTimeInfo realTimeInfo) {
		Map<String, String> map = realTimeInfo.getDiskInfo();
		if (info != null && !info.isEmpty()) {
			String[] strs = info.split(",");
			String[] ss = null;
			
			ss = strs[0].trim().split("\\s+");
			String percent = ss[4].substring(0, ss[4].length() - 1);
			
			if (!ss[4].contains("%")) {
				percent = ss[3].substring(0, ss[3].length() - 1);
			} 
			
			map.put("rootDisk", percent);
			
			if (strs.length > 1) {
				ss = strs[1].trim().split("\\s+");
				
				String percent2 = ss[4].substring(0, ss[4].length() - 1);
				
				if (!ss[4].contains("%")) {
					percent2 = ss[3].substring(0, ss[3].length() - 1);
				}
				
				map.put("userDisk", percent2);
			}

		}
	};
	
	
}
