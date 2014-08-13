package com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums;

import java.util.HashSet;
import java.util.Set;

public enum VideoStateFilterEnum {
	PLAYABLE, UNSCHEDULED, INACTIVE, DELETED;
	
	public static Set<VideoStateFilterEnum> CreateEmptySet(){
		return ((Set<VideoStateFilterEnum>)(new HashSet<VideoStateFilterEnum>()));
	}
	
	public static Set<VideoStateFilterEnum> CreateFullSet(){
		Set<VideoStateFilterEnum> ret = new HashSet<VideoStateFilterEnum>();
		ret.add(PLAYABLE);
		ret.add(UNSCHEDULED);
		ret.add(INACTIVE);
		ret.add(DELETED);
		return ret;
	}
}
