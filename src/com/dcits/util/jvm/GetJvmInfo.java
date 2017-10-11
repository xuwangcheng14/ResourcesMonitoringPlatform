package com.dcits.util.jvm;

import org.apache.commons.lang.StringUtils;

import com.dcits.bean.JvmInfo;
import com.dcits.bean.jvm.JvmRealTimeInfo;
import com.dcits.util.DcitsUtil;
import com.dcits.util.linux.GetLinuxInfoUtil;

public class GetJvmInfo {
	
	public static void getRealTimeInfo (JvmInfo info) throws Exception {
		String str ;
		try {
			str = GetLinuxInfoUtil.execCommand(info.getConn(), info.getJavaHome() + "/jstat -gcutil " + info.getPid(), 5, null, 0);
			parseInfo(info, str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw e;			
		}
					

	}	
	
	private static void parseInfo (JvmInfo info, String str) {
		if (StringUtils.isNotEmpty(str)) {
			//  S0     S1     E      O      P     YGC     YGCT    FGC    FGCT     GCT   
			//  0.00  35.77  34.13  74.47  99.73   1971   32.001    25    7.382   39.383
			str = str.trim();
			String[] infos = ((str.split("\\n")[1]).trim()).split("\\s+");
			JvmRealTimeInfo realTimeInfo = info.getJvmInfo();
			if (infos.length == 10) {
				realTimeInfo.setSurvivorSpacePercent_0(infos[0]);
				realTimeInfo.setSurvivorSpacePercent_1(infos[1]);
				realTimeInfo.setEdenSpacePercent(infos[2]);
				realTimeInfo.setOldSpacePercent(infos[3]);
				realTimeInfo.setPermSpacePercent(infos[4]);
				realTimeInfo.setYoungGCTotalCount(infos[5]);
				realTimeInfo.setYoungGCTime(infos[6]);
				realTimeInfo.setFullGCTotalCount(infos[7]);
				realTimeInfo.setFullGCTime(infos[8]);
				realTimeInfo.setGCTotalTime(infos[9]);
				realTimeInfo.setTime(DcitsUtil.getCurrentTime(DcitsUtil.DEFAULT_DATE_PATTERN));
			} 			
		} else {
			info.setConnectStatus("获取该Java进程信息出错！");
		}
	}

}
