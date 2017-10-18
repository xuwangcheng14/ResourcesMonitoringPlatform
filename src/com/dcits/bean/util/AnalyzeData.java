package com.dcits.bean.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzeData {
	
	private String id;
	private String host;
	private String serverType;
	private String beginTime;
	private String endTime;
	
	private List<AnalyzeItemData> items = new ArrayList<AnalyzeItemData>();
	
	//{id:"", host:"", serverType:""}
	private Map<String, Object> ruleItemStr = new HashMap<String, Object>();
	
	public void analyzeData(Map<String, List<String>> itemInfos, Map<String, Map<String, Object>> datas,
			Map<String, List<String>> dates) {
		
		if (ruleItemStr == null) {
			return;
		}
		
		id = ruleItemStr.get("id").toString();
		host = ruleItemStr.get("host").toString();
		serverType = ruleItemStr.get("serverType").toString();
		List<String> datesThis = dates.get(id);
		beginTime = datesThis.get(0);
		endTime = datesThis.get(datesThis.size() - 1);
		
		datas = (Map<String, Map<String, Object>>) datas.get(serverType).get(id);
		
		for (String s:itemInfos.get(serverType)) {
			String[] ss = s.split("\\.");
			AnalyzeItemData item = new AnalyzeItemData();
			item.setItemName(s);
			item.analyzeData((List<String>)datas.get(ss[0]).get(ss[1]));
			this.items.add(item);
		}
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Map<String, Object> getRuleItemStr() {
		return ruleItemStr;
	}
	
	public void setRuleItemStr(Map<String, Object> ruleItemStr) {
		this.ruleItemStr = ruleItemStr;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public List<AnalyzeItemData> getItems() {
		return items;
	}
	public void setItems(List<AnalyzeItemData> items) {
		this.items = items;
	}
	
}
