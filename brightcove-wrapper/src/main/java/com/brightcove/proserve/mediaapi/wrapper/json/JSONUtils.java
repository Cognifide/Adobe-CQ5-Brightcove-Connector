package com.brightcove.proserve.mediaapi.wrapper.json;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.WrapperException;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.WrapperExceptionCode;

public class JSONUtils {
	public static String parseHttpEntity(HttpEntity entity) throws BrightcoveException {
		if(entity == null){
			return null;
		}
		
		String      output   = "";
	    InputStream instream = null;
	    try{
	    	instream = entity.getContent();
	    }
	    catch(IOException ioe){
			throw new WrapperException(WrapperExceptionCode.MAPI_IO_EXCEPTION, "Exception: '" + ioe + "'");
	    }
	    catch(IllegalStateException ise){
			throw new WrapperException(WrapperExceptionCode.MAPI_ILLEGAL_STATE_RESPONSE, "Exception: '" + ise + "'");
	    }
	    
	    int length;
	    byte[] tmp = new byte[2048];
	    try{
	    	while ((length = instream.read(tmp)) != -1) {
	    		if(length < 2048){
	    			byte[] tmp2 = new byte[length];
	    			for(int tmpIdx=0;tmpIdx<length;tmpIdx++){
	    				tmp2[tmpIdx] = tmp[tmpIdx];
	    			}
	    			output += (new String(tmp2));
	    		}
	    		else{
	    			output += (new String(tmp));
	    		}
	    	}
		}
	    catch(IOException ioe){
			throw new WrapperException(WrapperExceptionCode.MAPI_IO_EXCEPTION, "Exception: '" + ioe + "'");
	    }
	    
	    return output;
	}
}
