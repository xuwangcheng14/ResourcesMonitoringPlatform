package com.dcits.bean.util;

/**
 * 导出数据的信息
 * @author xuwangcheng
 * @version 2018.1.23
 *
 */
public class ExportFileInfo {
	
	private static Integer id = 1;	
	
	private Integer exportId;
	/**
	 * 文件路径
	 */
	private String filePath;
	/**
	 * 文件名
	 */
	private String fileName;
	/**
	 * 导出的时间
	 */
	private String exportTime;
	/**
	 * 服务器数量
	 */
	private Integer serverCount;
	/**
	 * 记录数
	 */
	private String recordCount;
	/**
	 * 文件大小
	 */
	private String fileSize;
	
	public ExportFileInfo() {
		super();
		this.exportId = ++id;
	}

	public Integer getExportId() {
		return exportId;
	}

	public void setExportId(Integer exportId) {
		this.exportId = exportId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExportTime() {
		return exportTime;
	}

	public void setExportTime(String exportTime) {
		this.exportTime = exportTime;
	}


	public Integer getServerCount() {
		return serverCount;
	}

	public void setServerCount(Integer serverCount) {
		this.serverCount = serverCount;
	}
	
	

	public String getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(String recordCount) {
		this.recordCount = recordCount;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "ExportFileInfo [exportId=" + exportId + ", filePath="
				+ filePath + ", fileName=" + fileName + ", exportTime="
				+ exportTime + ", serverCount="
				+ serverCount + ", recordCount=" + recordCount + ", fileSize="
				+ fileSize + "]";
	}
}
