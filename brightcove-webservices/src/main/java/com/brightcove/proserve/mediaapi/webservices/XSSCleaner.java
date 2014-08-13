package com.brightcove.proserve.mediaapi.webservices;

public class XSSCleaner {

	public static String cleanXSS(final String value) {
		
		if(value == null){
			return "";
		}
		
		String result = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		
		result = result.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		result = result.replaceAll("\"", "&#34;");
		result = result.replaceAll("'", "&#39;");
		result = result.replaceAll("eval\\((.*)\\)", "");
		result = result.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		result = result.replaceAll("script", "");
		
		return result;
	}
}