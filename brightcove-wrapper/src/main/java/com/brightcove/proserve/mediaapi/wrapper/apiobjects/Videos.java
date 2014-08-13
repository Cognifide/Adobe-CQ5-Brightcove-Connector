package com.brightcove.proserve.mediaapi.wrapper.apiobjects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>Not a real Media API object - a wrapper object to represent a list of Video objects.</p>
 * 
 * @author Sander Gates <three.4.clavins.kitchen @at@ gmail.com>
 *
 */
public class Videos extends ArrayList<Video> {
	private static final long serialVersionUID = -1603397104852631362L;
	
	private Integer totalCount = 0;
	
	public Videos(JSONObject jsonObj) throws JSONException {
		JSONArray jsonItems = jsonObj.getJSONArray("items");
		for(int itemIdx=0;itemIdx<jsonItems.length();itemIdx++){
			JSONObject jsonItem = (JSONObject)jsonItems.get(itemIdx);
			Video video = new Video(jsonItem);
			add(video);
		}
		
		try{
			totalCount = jsonObj.getInt("total_count");
		}
		catch(JSONException jsone){
			// Don't fail altogether
			totalCount = -1;
		}
	}
	
	public Integer getTotalCount(){
		return this.totalCount;
	}
}
