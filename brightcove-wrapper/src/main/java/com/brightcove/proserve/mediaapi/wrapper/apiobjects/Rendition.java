package com.brightcove.proserve.mediaapi.wrapper.apiobjects;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.ControllerTypeEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.VideoCodecEnum;

/**
 * <p>Represents a Rendition object to/from the Media API.</p>
 * 
 * <p>From the Brightcove documentation on 2010/08/15 (<a href="http://support.brightcove.com/en/docs/media-api-objects-reference">http://support.brightcove.com/en/docs/media-api-objects-reference</a>):<br/>
 * <code>The Rendition object represents one of the dynamic delivery renditions of a video. A Video should have not more than 10 Renditions. For more information, see <a href="http://support.brightcove.com/en/docs/using-dynamic-delivery">Using dynamic delivery</a> and <a href="http://support.brightcove.com/en/docs/creating-videos-dynamic-delivery">Creating videos for dynamic delivery</a>.</code>
 * <table>
 * 	<thead>
 * 		<tr>
 * 			<th style="width: 20%;" scope="col"><strong>property name</strong></th>
 * 			<th style="width: 10%;" scope="col"><strong>type</strong></th>
 * 			<th style="width: 10%;" scope="col"><strong>read only?</strong></th>
 * 			<th style="width: 60%;" scope="col"><strong>description</strong></th>
 * 		</tr>
 * 	</thead>
 * 	<tbody>
 * 		<tr>
 * 			<td>url</td>
 * 			<td>String</td>
 * 			<td>yes</td>
 * 			<td>The URL of the rendition file.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>controllerType</td>
 * 			<td>enum</td>
 * 			<td>no</td>
 * 			<td>[Optional � required for <a href="http://support.brightcove.com/en/docs/delivering-live-video">live streaming</a> only] Depending on your CDN, one of the following values: LIMELIGHT_LIVE or AKAMAI_LIVE.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>encodingRate</td>
 * 			<td>int</td>
 * 			<td>yes</td>
 * 			<td>The rendition's encoding rate, in bits per second.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>frameHeight</td>
 * 			<td>int</td>
 * 			<td>yes</td>
 * 			<td>The rendition's display height, in pixels.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>frameWidth</td>
 * 			<td>int</td>
 * 			<td>yes</td>
 * 			<td>The rendition's display width, in pixels.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>size</td>
 * 			<td>long</td>
 * 			<td>yes</td>
 * 			<td>Required. The file size of the rendition, in bytes.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>remoteUrl</td>
 * 			<td>string</td>
 * 			<td>no</td>
 * 			<td>Required, for remote assets. The complete path to the file hosted on the remote server. If the file is served using progressive download, then you must include the file name and extension for the file. You can also use a URL that re-directs to a URL that includes the file name and extension. If the file is served using Flash streaming, use the remoteStreamName attribute to provide the stream name.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>remoteStreamName</td>
 * 			<td>string</td>
 * 			<td>no</td>
 * 			<td>[Optional - required for streaming remote assets only] A stream name for Flash streaming appended to the value of the remoteUrl property.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>videoDuration</td>
 * 			<td>long</td>
 * 			<td>no</td>
 * 			<td>Required. The length of the remote video asset in milliseconds.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>videoCodec</td>
 * 			<td>enum</td>
 * 			<td>no</td>
 * 			<td>Required. Valid values are SORENSON, ON2, and H264.</td>
 * 		</tr>
 * 	</tbody>
 * </table>
 * </p>
 * 
 * @author Sander Gates <three.4.clavins.kitchen @at@ gmail.com>
 *
 */
public class Rendition {
	private String             url;
	private ControllerTypeEnum controllerType;
	private Integer            encodingRate;
	private String             displayName;
	private Integer            frameHeight;
	private Integer            frameWidth;
	private Long               size;
	private String             remoteUrl;
	private String             remoteStreamName;
	private Long               videoDuration;
	private Long               id;
	private Boolean            audioOnly;
	private String             referenceId;
	private Long               uploadTimestampMillis;
	private String             videoContainer;
	
	private VideoCodecEnum videoCodec;
	
	/**
	 * <p>Default Constructor.</p>
	 * 
	 * <p>All fields set to null to start - required fields must be set before calling Write Media API.</p>
	 * 
	 */
	public Rendition(){
		initAll();
	}
	
	/**
	 * <p>Constructor using JSON string.</p>
	 * 
	 * <p>Given a JSON string from the Media API, attempts to construct a new Rendition object and fill out all of the fields defined.  All other fields will be null.</p>
	 * 
	 */
	public Rendition(String json) throws JSONException {
		initAll();
		
		if(json == null){
			throw new JSONException("[ERR] Rendition can not be parsed from null JSON string.");
		}
		
		JSONObject jsonObj = new JSONObject(json);
		
		String[] rootKeys = JSONObject.getNames(jsonObj);
		
		for(String rootKey : rootKeys){
			Object rootValue = jsonObj.get(rootKey);
			
			if((rootValue == null) || ("null".equals(rootValue.toString()))){
				// Don't bother setting the attribute, it should already be null
			}
			else if("encodingRate".equals(rootKey)){
				encodingRate = jsonObj.getInt(rootKey);
			}
			else if("referenceId".equals(rootKey)){
				referenceId = rootValue.toString();
			}
			else if("url".equals(rootKey)){
				url = rootValue.toString();
			}
			else if("controllerType".equals(rootKey)){
				if("AKAMAI_LIVE".equals(rootValue.toString())){
					controllerType = ControllerTypeEnum.AKAMAI_LIVE;
				}
				else if("LIMELIGHT_LIVE".equals(rootValue.toString())){
					controllerType = ControllerTypeEnum.LIMELIGHT_LIVE;
				}
				else if("DEFAULT".equals(rootValue.toString())){
					controllerType = ControllerTypeEnum.DEFAULT;
				}
				else{
					throw new JSONException("[ERR] Media API specified invalid value for controller type '" + rootValue + "'.  Acceptable values are 'AKAMAI_LIVE' and 'LIMELIGHT_LIVE'.");
				}
			}
			else if("size".equals(rootKey)){
				size = jsonObj.getLong(rootKey);
			}
			else if("id".equals(rootKey)){
				id = jsonObj.getLong(rootKey);
			}
			else if("uploadTimestampMillis".equals(rootKey)){
				uploadTimestampMillis = jsonObj.getLong(rootKey);
			}
			else if("frameWidth".equals(rootKey)){
				frameWidth = jsonObj.getInt(rootKey);
			}
			else if("remoteUrl".equals(rootKey)){
				remoteUrl = rootValue.toString();
			}
			else if("remoteStreamName".equals(rootKey)){
				remoteStreamName = rootValue.toString();
			}
			else if("displayName".equals(rootKey)){
				displayName = rootValue.toString();
			}
			else if("videoCodec".equals(rootKey)){
				if(rootValue.toString().equals("NONE")){
					videoCodec = VideoCodecEnum.NONE;
				}
				else if(rootValue.toString().equals("ON2")){
					videoCodec = VideoCodecEnum.ON2;
				}
				else if(rootValue.toString().equals("H264")){
					videoCodec = VideoCodecEnum.H264;
				}
				else if(rootValue.toString().equals("SORENSON")){
					videoCodec = VideoCodecEnum.SORENSON;
				}
				else{
					throw new JSONException("[ERR] Media API specified invalid value for video codec '" + rootValue + "'.  Acceptable values are 'NONE', 'ON2', 'H264' and 'SORENSON'.");
				}
			}
			else if("videoDuration".equals(rootKey)){
				videoDuration = jsonObj.getLong(rootKey);
			}
			else if("frameHeight".equals(rootKey)){
				frameHeight = jsonObj.getInt(rootKey);
			}
			else if("audioOnly".equals(rootKey)){
				audioOnly = jsonObj.getBoolean(rootKey);
			}
			else if("videoContainer".equals(rootKey)){
				videoContainer = rootValue.toString();
			}
			else{
				// Disabling exception throw for now - unknown keys will simply junk up the log instead of failing the program
				// throw new JSONException("[ERR] Unknown root key '" + rootKey + "'='" + rootValue + "'.");
				Logger.getLogger(this.getClass().getCanonicalName()).warning("[ERR] Unknown root key '" + rootKey + "'='" + rootValue + "'.");
			}
		}
	}
	
	/**
	 * <p>Sets all variables to null.</p>
	 */
	public void initAll(){
		url                   = null;
		controllerType        = null;
		encodingRate          = null;
		displayName           = null;
		frameHeight           = null;
		frameWidth            = null;
		size                  = null;
		remoteUrl             = null;
		remoteStreamName      = null;
		videoDuration         = null;
		id                    = null;
		audioOnly             = null;
		referenceId           = null;
		uploadTimestampMillis = null;
		videoContainer        = null;
		
		videoCodec = null;
	}
	
	/**
	 * <p>Gets the URL for this Rendition.</p>
	 * 
	 * <p><code>The URL of the rendition file.</code></p>
	 * 
	 * @return Rendition URL as a String
	 */
	public String  getUrl(){
		return url;
	}
	
	/**
	 * <p>Sets the URL for this Rendition.</p>
	 * 
	 * <p><code>The URL of the rendition file.</code></p>
	 * 
	 * @param url String representing the Rendition URL
	 */
	public void setUrl(String url){
		this.url = url;
	}
	
	/**
	 * <p>Gets the Controller Type for this Rendition.</p>
	 * 
	 * <p><code>The Controller Type of the rendition file.</code></p>
	 * 
	 * @return Controller Type enumeration value
	 */
	public ControllerTypeEnum getControllerType(){
		return controllerType;
	}
	
	/**
	 * <p>Sets the Controller Type for this Rendition.</p>
	 * 
	 * <p><code>[Optional � required for live streaming only] Depending on your CDN, one of the following values: LIMELIGHT_LIVE or AKAMAI_LIVE.</code></p>
	 * 
	 * @param controllerType Enumeration value for the controller type
	 */
	public void setControllerType(ControllerTypeEnum controllerType){
		this.controllerType = controllerType;
	}
	
	/**
	 * <p>Gets the encoding rate for this Rendition.</p>
	 * 
	 * <p><code>The rendition's encoding rate, in bits per second.</code></p>
	 * 
	 * @return Encoding rate for this Rendition, in bits/second
	 */
	public Integer getEncodingRate(){
		return encodingRate;
	}
	
	/**
	 * <p>Sets the encoding rate for this Rendition.</p>
	 * 
	 * <p><code>The rendition's encoding rate, in bits per second.</code></p>
	 * 
	 * @param encodingRate Encoding rate for this Rendition, in bits/second
	 */
	public void setEncodingRate(Integer encodingRate){
		this.encodingRate = encodingRate;
	}
	
	/**
	 * <p>Gets the display name for this Rendition.</p>
	 * 
	 * <p><code>The display name of the rendition file.</code></p>
	 * 
	 * @return Rendition display name as a String
	 */
	public String  getDisplayName(){
		return displayName;
	}
	
	/**
	 * <p>Sets the display name for this Rendition.</p>
	 * 
	 * <p><code>The display name of the rendition file.</code></p>
	 * 
	 * @param displayName
	 */
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	/**
	 * <p>Gets the frame height for this Rendition in pixels.</p>
	 * 
	 * <p><code>The rendition's display height, in pixels.</code></p>
	 * 
	 * @return Frame height for this Rendition in pixels
	 */
	public Integer getFrameHeight(){
		return frameHeight;
	}
	
	/**
	 * <p>Sets the frame height for this Rendition in pixels.</p>
	 * 
	 * <p><code>The rendition's display height, in pixels.</code></p>
	 * 
	 * @param frameHeight Frame height for this Rendition in pixels
	 */
	public void setFrameHeight(Integer frameHeight){
		this.frameHeight = frameHeight;
	}
	
	/**
	 * <p>Gets the frame width for this Rendition in pixels.</p>
	 * 
	 * <p><code>The rendition's display width, in pixels.</code></p>
	 * 
	 * @return Frame width for this Rendition in pixels
	 */
	public Integer getFrameWidth(){
		return frameWidth;
	}
	
	/**
	 * <p>Sets the frame width for this Rendition in pixels.</p>
	 * 
	 * <p><code>The rendition's display width, in pixels.</code></p>
	 * 
	 * @param frameWidth Frame width for this Rendition in pixels
	 */
	public void setFrameWidth(Integer frameWidth){
		this.frameWidth = frameWidth;
	}
	
	/**
	 * <p>Gets the file size of this Rendition in bytes.</p>
	 * 
	 * <p><code>Required. The file size of the rendition, in bytes.</code></p>
	 * 
	 * @return File size in bytes for this Rendition
	 */
	public Long    getSize(){
		return size;
	}
	
	/**
	 * <p>Sets the file size of this Rendition in bytes.</p>
	 * 
	 * <p><code>Required. The file size of the rendition, in bytes.</code></p>
	 * 
	 * @param size File size in bytes for this Rendition
	 */
	public void setSize(Long size){
		this.size = size;
	}
	
	/**
	 * <p>Gets the remote URL attribute for this asset.</p>
	 * 
	 * <p><code>Required, for remote assets. The complete path to the file hosted on the remote server. If the file is served using progressive download, then you must include the file name and extension for the file. You can also use a URL that re-directs to a URL that includes the file name and extension. If the file is served using Flash streaming, use the remoteStreamName attribute to provide the stream name.</code></p>
	 * 
	 * @return Remote URL attribute for this asset as a String
	 */
	public String  getRemoteUrl(){
		return remoteUrl;
	}
	
	/**
	 * <p>Sets the remote URL attribute for this asset.</p>
	 * 
	 * <p><code>Required, for remote assets. The complete path to the file hosted on the remote server. If the file is served using progressive download, then you must include the file name and extension for the file. You can also use a URL that re-directs to a URL that includes the file name and extension. If the file is served using Flash streaming, use the remoteStreamName attribute to provide the stream name.</code></p>
	 * 
	 * @param remoteUrl Remote URL attribute for this asset as a String
	 */
	public void setRemoteUrl(String remoteUrl){
		this.remoteUrl = remoteUrl;
	}
	
	/**
	 * <p>Gets the remote stream name attribute for this Rendition.</p>
	 * 
	 * <p><code>[Optional - required for streaming remote assets only] A stream name for Flash streaming appended to the value of the remoteUrl property.</code></p>
	 * 
	 * @return Remote stream name
	 */
	public String  getRemoteStreamName(){
		return remoteStreamName;
	}
	
	/**
	 * <p>Sets the remote stream name attribute for this Rendition.</p>
	 * 
	 * <p><code>[Optional - required for streaming remote assets only] A stream name for Flash streaming appended to the value of the remoteUrl property.</code></p>
	 * 
	 * @param remoteStreamName Remote stream name
	 */
	public void setRemoteStreamName(String remoteStreamName){
		this.remoteStreamName = remoteStreamName;
	}
	
	/**
	 * <p>Gets the length of the video asset.</p>
	 * 
	 * <p><code>Required. The length of the remote video asset in milliseconds.</code></p>
	 * 
	 * @return The length of the video asset in milliseconds
	 */
	public Long    getVideoDuration(){
		return videoDuration;
	}
	
	/**
	 * <p>Sets the length of the video asset.</p>
	 * 
	 * <p><code>Required. The length of the remote video asset in milliseconds.</code></p>
	 * 
	 * @param videoDuration the length of the video asset in milliseconds
	 */
	public void setVideoDuration(Long videoDuration){
		this.videoDuration = videoDuration;
	}
	
	/**
	 * <p>Gets the video codec for this Rendition.</p>
	 * 
	 * <p><code>Required. Valid values are SORENSON, ON2, and H264.</code></p>
	 * 
	 * @return Video codec for this Rendition
	 */
	public VideoCodecEnum getVideoCodec(){
		return videoCodec;
	}
	
	/**
	 * <p>Sets the video codec for this Rendition.</p>
	 * 
	 * <p><code>Required. Valid values are SORENSON, ON2, and H264.</code></p>
	 * 
	 * @param videoCodec Video codec for this Rendition
	 */
	public void setVideoCodec(VideoCodecEnum videoCodec){
		this.videoCodec = videoCodec;
	}
	
	/**
	 * <p>Gets whether or not this rendition is audio only.</p>
	 * 
	 * @return Boolean (true if the rendition is audio only, else false)
	 */
	public Boolean getAudioOnly(){
		return audioOnly;
	}
	
	/**
	 * <p>Sets whether or not this rendition is audio only.</p>
	 * 
	 * @param audioOnly True if the rendition is audio only, else false
	 */
	public void setAudioOnly(Boolean audioOnly){
		this.audioOnly = audioOnly;
	}
	
	public Long getId(){
		return id;
	}
	public void setId(Long id){
		this.id = id;
	}
	
	public String getReferenceId(){
		return referenceId;
	}
	public void setReferenceId(String referenceId){
		this.referenceId = referenceId;
	}
	
	public Long getUploadTimestampMillis(){
		return uploadTimestampMillis;
	}
	public void setUploadTimestampMillis(Long uploadTimestampMillis){
		this.uploadTimestampMillis = uploadTimestampMillis;
	}
	
	public String getVideoContainer(){
		return videoContainer;
	}
	public void setVideoContainer(String videoContainer){
		this.videoContainer = videoContainer;
	}
	
	/**
	 * <p>Converts the rendition into a JSON object suitable for use with the Media API</p>
	 * 
	 * @return JSON object representing the rendition
	 */
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		
		if(url != null){
			json.put("url", url);
		}
		if(controllerType != null){
			json.put("controllerType", "" + controllerType);
		}
		if(encodingRate != null){
			json.put("encodingRate",   encodingRate);
		}
		if(displayName != null){
			json.put("displayName", displayName);
		}
		if(frameHeight != null){
			json.put("frameHeight", frameHeight);
		}
		if(frameWidth != null){
			json.put("frameWidth", frameWidth);
		}
		if(size != null){
			json.put("size", size);
		}
		if(remoteUrl != null){
			json.put("remoteUrl", remoteUrl);
		}
		if(remoteStreamName != null){
			json.put("remoteStreamName", remoteStreamName);
		}
		if(videoDuration != null){
			json.put("videoDuration", videoDuration);
		}
		if(id != null){
			json.put("id", id);
		}
		if(videoCodec != null){
			json.put("videoCodec", videoCodec);
		}
		if(audioOnly != null){
			json.put("audioOnly", audioOnly);
		}
		if(referenceId != null){
			json.put("referenceId", referenceId);
		}
		if(uploadTimestampMillis != null){
			json.put("uploadTimestampMillis", uploadTimestampMillis);
		}
		if(videoContainer != null){
			json.put("videoContainer", videoContainer);
		}
		
		return json;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String ret = "[com.brightcove.proserve.mediaapi.wrapper.apiobjects.Rendition (\n" +
			"\turl:'"                   + url                   + "'\n" +
			"\tcontrollerType:'"        + controllerType        + "'\n" +
			"\tencodingRate:'"          + encodingRate          + "'\n" +
			"\tdisplayName:'"           + displayName           + "'\n" +
			"\tframeHeight:'"           + frameHeight           + "'\n" +
			"\tframeWidth:'"            + frameWidth            + "'\n" +
			"\tsize:'"                  + size                  + "'\n" +
			"\tremoteUrl:'"             + remoteUrl             + "'\n" +
			"\tremoteStreamName:'"      + remoteStreamName      + "'\n" +
			"\tvideoDuration:'"         + videoDuration         + "'\n" +
			"\tid:'"                    + id                    + "'\n" +
			"\taudioOnly:'"             + audioOnly             + "'\n" +
			"\treferenceId:'"           + referenceId           + "'\n" +
			"\tuploadTimestampMillis:'" + uploadTimestampMillis + "'\n" + 
			"\tvideoCodec:'"            + videoCodec            + "'\n" +
			"\tvideoContainer:'"        + videoContainer        + "'\n" +
			")]";
		
		return ret;
	}
}
