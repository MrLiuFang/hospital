package com.lion.device.service;

public class ExcelColumn {

	private String name;
	
	private String key;
	
	
	
	public ExcelColumn(String name, String key) {
		super();
		this.name = name;
		this.key = key;
	}

	public static ExcelColumn build(String name,String key) {
		return new ExcelColumn(name,key);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
}
