package com.brightcove.proserve.mediaapi.wrapper.exceptions;

public class WrapperException extends BrightcoveException {
	private static final long serialVersionUID = -4695861147571405702L;
	
	private WrapperExceptionCode code;
	private String               message;
	
	public WrapperException(WrapperExceptionCode code, String message) {
		super();
		this.code    = code;
		this.message = message;
		this.type    = ExceptionType.WRAPPER_EXCEPTION;
	}
	
	public WrapperExceptionCode getCode(){
		return code;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String toString(){
		return "[" + this.getClass().getCanonicalName() + "] (" + code.getCode() + ": " + code.getDescription() + ") Message: '" + message + "'";
	}
}
