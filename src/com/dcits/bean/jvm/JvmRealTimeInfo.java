package com.dcits.bean.jvm;

public class JvmRealTimeInfo {

	/**
	 * 标记时间
	 */
	private String time;
	
	/**
	 * Heap上的 Survivor space 0 区已使用空间的百分比
	 */
	private String survivorSpacePercent_0;
	
	/**
	 * Heap上的 Survivor space 1 区已使用空间的百分比
	 */
	private String survivorSpacePercent_1;
	
	/**
	 * Heap上的 Eden space 区已使用空间的百分比
	 */
	private String edenSpacePercent;
	
	/**
	 * Heap上的 Old space 区已使用空间的百分比
	 */
	private String oldSpacePercent;
	
	/**
	 * Perm space 区已使用空间的百分比
	 */
	private String permSpacePercent;
	
	/**
	 * 从应用程序启动到采样时发生 Young GC 的次数
	 */
	private String youngGCTotalCount;
	
	/**
	 * 从应用程序启动到采样时 Young GC 所用的时间(单位秒)
	 */
	private String youngGCTime;
	
	/**
	 * 从应用程序启动到采样时发生 Full GC 的次数
	 */
	private String fullGCTotalCount;
	
	/**
	 * 从应用程序启动到采样时 Full GC 所用的时间(单位秒)
	 */
	private String fullGCTime;
	
	/**
	 * 从应用程序启动到采样时用于垃圾回收的总时间(单位秒)，它的值等于YGC+FGC
	 */
	private String GCTotalTime;

	
	
	public String getYoungGCTime() {
		return youngGCTime;
	}

	public void setYoungGCTime(String youngGCTime) {
		this.youngGCTime = youngGCTime;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSurvivorSpacePercent_0() {
		return survivorSpacePercent_0;
	}

	public void setSurvivorSpacePercent_0(String survivorSpacePercent_0) {
		this.survivorSpacePercent_0 = survivorSpacePercent_0;
	}

	public String getSurvivorSpacePercent_1() {
		return survivorSpacePercent_1;
	}

	public void setSurvivorSpacePercent_1(String survivorSpacePercent_1) {
		this.survivorSpacePercent_1 = survivorSpacePercent_1;
	}

	public String getEdenSpacePercent() {
		return edenSpacePercent;
	}

	public void setEdenSpacePercent(String edenSpacePercent) {
		this.edenSpacePercent = edenSpacePercent;
	}

	public String getOldSpacePercent() {
		return oldSpacePercent;
	}

	public void setOldSpacePercent(String oldSpacePercent) {
		this.oldSpacePercent = oldSpacePercent;
	}

	public String getPermSpacePercent() {
		return permSpacePercent;
	}

	public void setPermSpacePercent(String permSpacePercent) {
		this.permSpacePercent = permSpacePercent;
	}

	public String getYoungGCTotalCount() {
		return youngGCTotalCount;
	}

	public void setYoungGCTotalCount(String youngGCTotalCount) {
		this.youngGCTotalCount = youngGCTotalCount;
	}

	public String getFullGCTotalCount() {
		return fullGCTotalCount;
	}

	public void setFullGCTotalCount(String fullGCTotalCount) {
		this.fullGCTotalCount = fullGCTotalCount;
	}

	public String getFullGCTime() {
		return fullGCTime;
	}

	public void setFullGCTime(String fullGCTime) {
		this.fullGCTime = fullGCTime;
	}

	public String getGCTotalTime() {
		return GCTotalTime;
	}

	public void setGCTotalTime(String gCTotalTime) {
		GCTotalTime = gCTotalTime;
	}
	
}
