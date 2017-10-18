package com.dcits.bean.util;

import java.util.List;

public class AnalyzeItemData {
	
	private String itemName;
	private String maxValue;
	private String minValue;
	private String avgValue;
		
	public void analyzeData(List<String> datas) {
		if (datas == null) {
			return;
		}
		
		double max = Double.parseDouble(datas.get(0));
		double min = Double.parseDouble(datas.get(0));
		double avg = 0;
		for (int i = 0;i < datas.size();i++) {
			if (max < Double.parseDouble(datas.get(i))) {
				max = Double.parseDouble(datas.get(i));
			}
			if (min > Double.parseDouble(datas.get(i))) {
				min = Double.parseDouble(datas.get(i));
			}
			
			avg += Double.parseDouble(datas.get(i));
		}
		
		maxValue = String.valueOf(max);
		minValue = String.valueOf(min);
		avgValue = String.format("%.2f", avg / datas.size());	
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getAvgValue() {
		return avgValue;
	}

	public void setAvgValue(String avgValue) {
		this.avgValue = avgValue;
	}
	
	
}
