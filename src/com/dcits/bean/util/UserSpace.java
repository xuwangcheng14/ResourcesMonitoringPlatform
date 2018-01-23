package com.dcits.bean.util;

import java.util.ArrayList;
import java.util.List;

import com.dcits.bean.JvmInfo;
import com.dcits.bean.LinuxInfo;
import com.dcits.bean.ServerInfo;
import com.dcits.bean.WeblogicInfo;
import com.dcits.util.DcitsUtil;
import com.dcits.util.ServletUtil;

/**
 * 
 * 用户空间
 * @author xuwangcheng
 * @version 2017.9.24
 *
 */
public class UserSpace {
	/**
	 * 唯一标识
	 */
	private String userKey;
	private List<LinuxInfo> linuxInfos = new ArrayList<LinuxInfo>();
	private List<WeblogicInfo> weblogicInfos = new ArrayList<WeblogicInfo>();
	private List<JvmInfo> jvmInfos = new ArrayList<JvmInfo>();
	private List<ExportFileInfo> exportFileInfos = new ArrayList<ExportFileInfo>();
	
	private String createTime = DcitsUtil.getCurrentTime(DcitsUtil.FULL_DATE_PATTERN);
	
		
	public UserSpace(String userKey) {
		super();
		this.userKey = userKey;
	}
	
	public UserSpace() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return linuxInfos.size() + weblogicInfos.size() + jvmInfos.size();
	}
	
	public WeblogicInfo getWeblogicInfo(Integer id) {
		for (WeblogicInfo info:this.weblogicInfos) {
			if (info.getId().equals(id)) {
				return info;
			}
		}
		return null;
	}
	
	public LinuxInfo getLinuxInfo(Integer id) {
		for (LinuxInfo info:this.linuxInfos) {
			if (info.getId().equals(id)) {
				return info;
			}
		}
		return null;
	}
	
	public JvmInfo getJvmInfo(Integer id) {
		for (JvmInfo info:this.jvmInfos) {
			if (info.getId().equals(id)) {
				return info;
			}
		}
		return null;
	}
	
	public ExportFileInfo getExportFileInfo (Integer id) {
		for (ExportFileInfo info:this.exportFileInfos) {
			if (info.getExportId().equals(id)) {
				return info;
			}
		}
		return null;
	}
	
	public void updateParameters(ServerInfo info) {
		ServletUtil.updateParameter(info, new ArrayList<ServerInfo>(this.linuxInfos));
		ServletUtil.updateParameter(info, new ArrayList<ServerInfo>(this.weblogicInfos));
		ServletUtil.updateParameter(info, new ArrayList<ServerInfo>(this.jvmInfos));
	}
	
	public String getUserKey() {
		return userKey;
	}
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}
	public List<LinuxInfo> getLinuxInfos() {
		return linuxInfos;
	}
	public void setLinuxInfos(List<LinuxInfo> linuxInfos) {
		this.linuxInfos = linuxInfos;
	}
	public List<WeblogicInfo> getWeblogicInfos() {
		return weblogicInfos;
	}
	public void setWeblogicInfos(List<WeblogicInfo> weblogicInfos) {
		this.weblogicInfos = weblogicInfos;
	}
	public List<JvmInfo> getJvmInfos() {
		return jvmInfos;
	}
	public void setJvmInfos(List<JvmInfo> jvmInfos) {
		this.jvmInfos = jvmInfos;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public List<ExportFileInfo> getExportFileInfos() {
		return exportFileInfos;
	}

	public void setExportFileInfos(List<ExportFileInfo> exportFileInfos) {
		this.exportFileInfos = exportFileInfos;
	}	
	
	
}
