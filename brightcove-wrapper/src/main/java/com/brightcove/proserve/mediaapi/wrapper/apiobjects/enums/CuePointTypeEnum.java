package com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums;

public enum CuePointTypeEnum {
	AD(0, "AD"),
	CODE(1, "CODE"),
	CHAPTER(2, "CHAPTER");
	
	private final String  name;
	private final Integer code;
	CuePointTypeEnum(Integer code, String name){
		this.code = code;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public Integer getCode() {
		return code;
	}
}
