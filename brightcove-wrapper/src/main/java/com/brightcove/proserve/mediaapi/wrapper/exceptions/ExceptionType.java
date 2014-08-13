package com.brightcove.proserve.mediaapi.wrapper.exceptions;

/**
 * <p>Types of BrightcoveException that can be thrown by calls to the Wrapper</p>
 * 
 * <p><ul>
 * <li>WRAPPER_EXCEPTION - Exception in the Wrapper logic</li>
 * <li>MEDIA_API_EXCEPTION - Exception thrown by the Media API</li>
 * <li>JAVA_EXCEPTION - java.lang.Exception thrown somewhere unexpected</li>
 * <li>UNKNOWN_TYPE - This should trigger a code review of the Wrapper source...</li>
 * </ul></p>
 * 
 * @author Sander Gates <three.4.clavins.kitchen @at@ gmail.com>
 * 
 */
public enum ExceptionType {
	WRAPPER_EXCEPTION, MEDIA_API_EXCEPTION, JAVA_EXCEPTION, UNKNOWN_TYPE
}
