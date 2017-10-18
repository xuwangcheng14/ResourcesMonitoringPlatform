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
			if (infos.length == 10 || infos.length == 11) { //不同版本的jdk打印的内容不一样， JDK1.8  11列  JDK1.7 10列 M替代P
				realTimeInfo.setSurvivorSpacePercent_0(infos[0]);
				realTimeInfo.setSurvivorSpacePercent_1(infos[1]);
				realTimeInfo.setEdenSpacePercent(infos[2]);
				realTimeInfo.setOldSpacePercent(infos[3]);
				
				realTimeInfo.setPermSpacePercent(infos[4]); //JDK1.8为Metaspace  JDK1.7为PermGen
				
				int m = infos.length == 10 ? 4 : 5;
				
				realTimeInfo.setYoungGCTotalCount(infos[++m]);
				realTimeInfo.setYoungGCTime(infos[++m]);
				realTimeInfo.setFullGCTotalCount(infos[++m]);
				realTimeInfo.setFullGCTime(infos[++m]);
				realTimeInfo.setGCTotalTime(infos[++m]);
				realTimeInfo.setTime(DcitsUtil.getCurrentTime(DcitsUtil.DEFAULT_DATE_PATTERN));
			} 
		} else {
			info.setConnectStatus("获取该Java进程信息出错！");
		}
	}

}
