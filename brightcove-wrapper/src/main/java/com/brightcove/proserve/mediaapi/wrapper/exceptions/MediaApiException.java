package com.brightcove.proserve.mediaapi.wrapper.exceptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>This is an exception thrown by the Media API</p>
 * 
 * @author Sander Gates <three.4.clavins.kitchen @at@ gmail.com>
 *
 */
public class MediaApiException extends BrightcoveException {
	private static final long serialVersionUID = -4136614631876754138L;
	
	private JSONObject jsonObject;
	private Integer    responseCode;
	private String     responseMessage;
	
	public static final Integer MEDIA_API_ERROR_UNPARSABLE = -1;
	
	public MediaApiException(JSONObject jsonObject) throws WrapperException {
		this.type = ExceptionType.MEDIA_API_EXCEPTION;
		
		this.responseCode    = null;
		this.responseMessage = null;
		this.jsonObject      = jsonObject;
		
		// Parse out the error object if available
		JSONObject errorObj  = null;
		try{
			String errorString = jsonObject.getString("error");
			if("null".equals(errorString)){
				// Ignore - basically "null" means there was no error
			}
			else{
				errorObj = new JSONObject(errorString);
			}
		}
		catch(JSONException jsone){
			// Couldn't find an "error" entry, request was probably successful
			errorObj = null;
		}
		
		// Parse the error code and message out of the error object
		if(errorObj != null){
			// Known formats for error JSON:
			//     {
			//         "error": "invalid token",
			//         "code":210
			//     }
			//     
			//     {
			//         "result": null,
			//         "error": { 
			//             "code": 103, 
			//             "name": "CallTimeoutError", 
			//             "message": "The request you made is taking longer than expected to return. If requesting a large amount of data please try again with a smaller page_size."
			//         } 
			//     }
			
			try{
				responseCode    = jsonObject.getInt("code");
				responseMessage = jsonObject.getString("error");
			}
			catch(JSONException jsone){
				// Maybe it's on the error object instead of the json object
				try{
					responseCode    = errorObj.getInt("code");
					responseMessage = errorObj.getString("message");
				}
				catch(JSONException jsone2){
					responseCode = MEDIA_API_ERROR_UNPARSABLE;
				}
			}
		}
	}
	
	public JSONObject getJsonObject(){
		return this.jsonObject;
	}
	public void setJsonObject(JSONObject jsonObject){
		this.jsonObject = jsonObject;
	}
	
	public Integer getResponseCode(){
		return this.responseCode;
	}
	public void setResponseCode(Integer responseCode){
		this.responseCode = responseCode;
	}
	
	public String getResponseMessage(){
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage){
		this.responseMessage = responseMessage;
	}
	
	public String toString(){
		return "[" + this.getClass().getCanonicalName() + "] JSON Object: '" + jsonObject + "'";
	}
}
