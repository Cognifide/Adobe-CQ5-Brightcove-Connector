package com.brightcove.proserve.mediaapi.wrapper.apiobjects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>Represents a CustomField object to/from the Media API.</p>
 * 
 * <p>Note that custom fields aren't officially dcoumented on the Brightcove documentation as of 2009/08/18 (<a href="http://support.brightcove.com/en/docs/media-api-objects-reference">http://support.brightcove.com/en/docs/media-api-objects-reference</a>).</p>
 * 
 * @author Sander Gates <three.4.clavins.kitchen @at@ gmail.com>
 *
 */
public class CustomField {
	private String name;
	private String value;
	
	/**
	 * <p>Default Constructor.</p>
	 * 
	 * <p>All fields set to null to start - required fields must be set before calling Write Media API.</p>
	 * 
	 */
	public CustomField(){
		InitAll();
	}
	
	/**
	 * <p>Constructor using JSON string.</p>
	 * 
	 * <p>Given a JSON string from the Media API, attempts to construct a new Custom Field object and fill out all of the fields defined.  All other fields will be null.</p>
	 * 
	 */
	public CustomField(String json) throws JSONException {
		InitAll();
		
		if(json == null){
			throw new JSONException("[ERR] Custom Field can not be parsed from null JSON string.");
		}
		
		JSONObject jsonObj = new JSONObject(json);
		
		String[] rootKeys = JSONObject.getNames(jsonObj);
		
		for(String rootKey : rootKeys){
			// Object rootValue = jsonObj.get(rootKey);
			
			this.name  = rootKey;
			this.value = jsonObj.getString(rootKey);
		}
	}
	
	/**
	 * <p>Constructor using key-value pair.</p>
	 * 
	 * <p>Given a custom name and value, attempts to construct a new Custom Field object and fill out all of the fields defined.  All other fields will be null.</p>
	 * 
	 */
	public CustomField(String name, String value) {
		InitAll();
		
		this.name  = name;
		this.value = value;
	}
	
	/**
	 * <p>Initializes all variables to null</p>
	 */
	public void InitAll(){
		name  = null;
		value = null;
	}
	
	/**
	 * <p>Gets the name of the Custom Field.</p>
	 * 
	 * @return Name of the Custom Field
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * <p>Sets the name of the Custom Field.</p>
	 * 
	 * @param name Name of the Custom Field
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * <p>Gets the value of the Custom Field.</p>
	 * 
	 * @return value of the Custom Field
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * <p>Sets the value of the Custom Field.</p>
	 * 
	 * @param value Value of the Custom Field
	 */
	public void setId(String value){
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String ret = "[com.brightcove.proserve.mediaapi.wrapper.apiobjects.CustomField (\n" +
			"\tname:'"  + name  + "'\n" +
			"\tvalue:'" + value + "'\n" +
			")]";
		
		return ret;
	}
}
