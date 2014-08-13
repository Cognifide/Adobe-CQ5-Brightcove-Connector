package com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums;

import java.util.EnumSet;

public enum VideoFieldEnum {
	ID("ID", "id"),
	NAME("NAME", "name"),
	SHORTDESCRIPTION("SHORTDESCRIPTION", "shortDescription"),
	LONGDESCRIPTION("LONGDESCRIPTION", "longDescription"),
	CREATIONDATE("CREATIONDATE", "creationDate"),
	PUBLISHEDDATE("PUBLISHEDDATE", "publishedDate"),
	LASTMODIFIEDDATE("LASTMODIFIEDDATE", "lastModifiedDate"),
	STARTDATE("STARTDATE", "startDate"),
	ENDDATE("ENDDATE", "endDate"),
	LINKURL("LINKURL", "linkUrl"),
	LINKTEXT("LINKTEXT", "linkText"),
	TAGS("TAGS", "tags"),
	VIDEOSTILLURL("VIDEOSTILLURL", "videoStillUrl"),
	THUMBNAILURL("THUMBNAILURL", "thumbnailUrl"),
	REFERENCEID("REFERENCEID", "referenceId"),
	LENGTH("LENGTH", "length"),
	ECONOMICS("ECONOMICS", "economics"),
	ITEMSTATE("ITEMSTATE", "itemState"),
	PLAYSTOTAL("PLAYSTOTAL", "playsTotal"),
	PLAYSTRAILINGWEEK("PLAYSTRAILINGWEEK", "playsTrailingWeek"),
	VERSION("VERSION", "version"),
	CUEPOINTS("CUEPOINTS", "cuePoints"),
	SUBMISSIONINFO("SUBMISSIONINFO", "submissionInfo"),
	CUSTOMFIELDS("CUSTOMFIELDS", "customfields"),
	RELEASEDATE("RELEASEDATE", "releaseDate"),
	FLVURL("FLVURL", "flvUrl"),
	RENDITIONS("RENDITIONS", "renditions"),
	GEOFILTERED("GEOFILTERED", "geoFiltered"),
	GEORESTRICTED("GEORESTRICTED", "geoRestricted"),
	GEOFILTEREXCLUDE("GEOFILTEREXCLUDE", "geoFilterExclude"),
	EXCLUDELISTEDCOUNTRIES("EXCLUDELISTEDCOUNTRIES", "excludeListedCountries"),
	GEOFILTEREDCOUNTRIES("GEOFILTEREDCOUNTRIES", "geoFilteredCountries"),
	ALLOWEDCOUNTRIES("ALLOWEDCOUNTRIES", "allowedCountries"),
	ACCOUNTID("ACCOUNTID", "accountId"),
	FLVFULLLENGTH("FLVFULLLENGTH", "flvFullLength"),
	VIDEOFULLLENGTH("VIDEOFULLLENGTH", "videoFullLength");
	
	private final String definition;
	private final String jsonName;
	VideoFieldEnum(String definition, String jsonName){
		this.definition = definition;
		this.jsonName   = jsonName;
	}
	
	public String getDefinition() {
		return definition;
	}
	public String getJsonName() {
		return jsonName;
	}
	
	public static EnumSet<VideoFieldEnum> CreateEmptyEnumSet(){
		return EnumSet.noneOf(VideoFieldEnum.class);
	}
	
	public static EnumSet<VideoFieldEnum> CreateFullEnumSet(){
		return EnumSet.allOf(VideoFieldEnum.class);
	}
}
