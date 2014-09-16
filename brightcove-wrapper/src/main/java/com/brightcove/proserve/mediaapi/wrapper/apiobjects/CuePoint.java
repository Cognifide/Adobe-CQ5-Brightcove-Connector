package com.brightcove.proserve.mediaapi.wrapper.apiobjects;

import org.json.JSONException;
import org.json.JSONObject;

import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.CuePointTypeEnum;

/**
 * <p>
 * Represents a Cue Point object to/from the Media API.
 * </p>
 * 
 * <p>
 * From the Brightcove documentation on 2009/08/18 (<a
 * href="http://support.brightcove.com/en/docs/media-api-objects-reference"
 * >http://support.brightcove.com/en/docs/media-api-objects-reference</a>):<br/>
 * <code>The CuePoint object is a marker set at a precise time point in the duration of a video. You can use cue points to trigger mid-roll ads or to separate chapters or scenes in a long-form video. For more information, see <a href="http://support.brightcove.com/en/docs/adding-cue-points-videos">Adding Cue Points to Videos</a> and <a href="http://support.brightcove.com/en/docs/setting-cue-points-media-api">Setting CuePoints with the Media API</a>.</code>
 * <table>
 * <thead>
 * <tr>
 * <th style="width: 20%;" scope="col"><strong>property name</strong></th>
 * <th style="width: 10%;" scope="col"><strong>type</strong></th>
 * <th style="width: 10%;" scope="col"><strong>read only?</strong></th>
 * <th style="width: 60%;" scope="col"><strong>description</strong></th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>name</td>
 * <td>String</td>
 * <td>yes</td>
 * <td>Required. A name for the cue point, so that you can refer to it.</td>
 * </tr>
 * <tr>
 * <td>videoId</td>
 * <td>String</td>
 * <td>yes</td>
 * <td>A comma-separated list of the ids of one or more videos that this cue point applies to.</td>
 * </tr>
 * <tr>
 * <td>time</td>
 * <td>Long</td>
 * <td>yes</td>
 * <td>Required. The time of the cue point, measured in milliseconds from the beginning of the video.</td>
 * </tr>
 * <tr>
 * <td>forceStop</td>
 * <td>Boolean</td>
 * <td>no</td>
 * <td>If true, the video stops playback at the cue point. This setting is valid only for AD type cue points.</td>
 * </tr>
 * <tr>
 * <td>type</td>
 * <td>Enum</td>
 * <td>yes</td>
 * <td>Required. An integer code corresponding to the type of cue point. One of 0 (AD), 1 (CODE), or 2
 * (CHAPTER). An AD cue point is used to trigger mid-roll ad requests. A CHAPTER cue point indicates a chapter
 * or scene break in the video. A CODE cue point causes an event that you can listen for and respond to.</td>
 * </tr>
 * <tr>
 * <td>metadata</td>
 * <td>String</td>
 * <td>no</td>
 * <td>A string that can be passed along with a CODE cue point.</td>
 * </tr>
 * </tbody>
 * </table>
 * </p>
 * 
 * @author Sander Gates <three.4.clavins.kitchen @at@ gmail.com>
 * 
 */
public class CuePoint {
	private Long id;

	private String name;

	private String videoId;

	private Long time;

	private Boolean forceStop;

	private CuePointTypeEnum type;

	private String metadata;

	/**
	 * <p>
	 * Default Constructor.
	 * </p>
	 * 
	 * <p>
	 * All fields set to null to start - required fields must be set before calling Write Media API.
	 * </p>
	 * 
	 */
	public CuePoint() {
		InitAll();
	}

	/**
	 * <p>
	 * Constructor using JSON string.
	 * </p>
	 * 
	 * <p>
	 * Given a JSON string from the Media API, attempts to construct a new Cue Point object and fill out all
	 * of the fields defined. All other fields will be null.
	 * </p>
	 * 
	 */
	public CuePoint(String json) throws JSONException {
		InitAll();

		if (json == null) {
			throw new JSONException("[ERR] Cue Point can not be parsed from null JSON string.");
		}

		JSONObject jsonObj = new JSONObject(json);

		String[] rootKeys = JSONObject.getNames(jsonObj);

		for (String rootKey : rootKeys) {
			Object rootValue = jsonObj.get(rootKey);

			if ((rootValue == null) || ("null".equals(rootValue.toString()))) {
				// Don't bother setting the attribute, it should already be null
			} else if ("forceStop".equals(rootKey)) {
				forceStop = jsonObj.getBoolean(rootKey);
			} else if ("id".equals(rootKey)) {
				id = jsonObj.getLong(rootKey);
			} else if ("metadata".equals(rootKey)) {
				metadata = rootValue.toString();
			} else if ("name".equals(rootKey)) {
				name = rootValue.toString();
			} else if ("time".equals(rootKey)) {
				time = jsonObj.getLong(rootKey);
			} else if ("type".equals(rootKey)) {
				// Not currently handled - see typeEnum
			} else if ("typeEnum".equals(rootKey)) {
				for (CuePointTypeEnum typeEnum : CuePointTypeEnum.values()) {
					if (typeEnum.getName().equals(rootValue.toString())) {
						type = typeEnum;
					}
				}
			} else if ("videoId".equals(rootKey)) {
				videoId = rootValue.toString();
			} else {
				throw new JSONException("[ERR] Unknown root key '" + rootKey + "'='" + rootValue + "'.");
			}
		}
	}

	/**
	 * <p>
	 * Initializes all variables to null
	 * </p>
	 */
	public void InitAll() {
		id = null;
		name = null;
		videoId = null;
		time = null;
		forceStop = null;
		type = null;
		metadata = null;
	}

	/**
	 * <p>
	 * Gets the name of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>Required. A name for the cue point, so that you can refer to it.</code>
	 * </p>
	 * 
	 * @return Name of the Cue Point
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>
	 * Sets the name of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>Required. A name for the cue point, so that you can refer to it.</code>
	 * </p>
	 * 
	 * @param name Name of the Cue Point
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p>
	 * Gets the id of the Cue Point.
	 * </p>
	 * 
	 * @return id of the Cue Point
	 */
	public Long getId() {
		return id;
	}

	/**
	 * <p>
	 * Sets the id of the Cue Point.
	 * </p>
	 * 
	 * @param id Id of the Cue Point
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * <p>
	 * Gets the unique id of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>A comma-separated list of the ids of one or more videos that this cue point applies to.</code>
	 * </p>
	 * 
	 * @return Comma-separated String of the ids of the Cue Point
	 */
	public String getVideoId() {
		return videoId;
	}

	/**
	 * <p>
	 * Sets the unique id of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>A comma-separated list of the ids of one or more videos that this cue point applies to.</code>
	 * </p>
	 * 
	 * @param videoId Comma-separated String of the ids of the Cue Point
	 */
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	/**
	 * <p>
	 * Gets the time of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>Required. The time of the cue point, measured in milliseconds from the beginning of the video.</code>
	 * </p>
	 * 
	 * @return Time (in milliseconds from the beginning of the video) of the Cue Point
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * <p>
	 * Sets the time of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>Required. The time of the cue point, measured in milliseconds from the beginning of the video.</code>
	 * </p>
	 * 
	 * @param time Time (in milliseconds from the beginning of the video) of the Cue Point
	 */
	public void setTime(Long time) {
		this.time = time;
	}

	/**
	 * <p>
	 * Gets the forceStop attribute of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>If true, the video stops playback at the cue point. This setting is valid only for AD type cue points.</code>
	 * </p>
	 * 
	 * @return forceStop attribute of the Cue Point
	 */
	public Boolean getForceStop() {
		return forceStop;
	}

	/**
	 * <p>
	 * Sets the forceStop attribute of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>If true, the video stops playback at the cue point. This setting is valid only for AD type cue points.</code>
	 * </p>
	 * 
	 * @param forceStop forceStop attribute of the Cue Point
	 */
	public void setForceStop(Boolean forceStop) {
		this.forceStop = forceStop;
	}

	/**
	 * <p>
	 * Gets the type of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>Required. An integer code corresponding to the type of cue point. One of 0 (AD), 1 (CODE), or 2 (CHAPTER). An AD cue point is used to trigger mid-roll ad requests. A CHAPTER cue point indicates a chapter or scene break in the video. A CODE cue point causes an event that you can listen for and respond to.</code>
	 * </p>
	 * 
	 * @return Type of the Cue Point
	 */
	public CuePointTypeEnum getType() {
		return type;
	}

	/**
	 * <p>
	 * Sets the type of the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>Required. An integer code corresponding to the type of cue point. One of 0 (AD), 1 (CODE), or 2 (CHAPTER). An AD cue point is used to trigger mid-roll ad requests. A CHAPTER cue point indicates a chapter or scene break in the video. A CODE cue point causes an event that you can listen for and respond to.</code>
	 * </p>
	 * 
	 * @param type Type of the Cue Point
	 */
	public void setType(CuePointTypeEnum type) {
		this.type = type;
	}

	/**
	 * <p>
	 * Gets the meta data on the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>A string that can be passed along with a CODE cue point.</code>
	 * </p>
	 * 
	 * @return Meta data on the Cue Point
	 */
	public String getMetadata() {
		return metadata;
	}

	/**
	 * <p>
	 * Sets the meta data on the Cue Point.
	 * </p>
	 * 
	 * <p>
	 * <code>A string that can be passed along with a CODE cue point.</code>
	 * </p>
	 * 
	 * @param metadata Meta data on the Cue Point
	 */
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	/**
	 * <p>
	 * Converts the cue point into a JSON object suitable for use with the Media API
	 * </p>
	 * 
	 * @return JSON object representing the cue point
	 */
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();

		if (id != null) {
			json.put("id", id);
		}
		if (name != null) {
			json.put("name", name);
		}
		if (videoId != null) {
			json.put("videoId", videoId);
		}
		if (time != null) {
			json.put("time", time);
		}
		if (forceStop != null) {
			json.put("forceStop", forceStop);
		}
		if (type != null) {
			json.put("type", type);
		}
		if (metadata != null) {
			json.put("metadata", metadata);
		}

		return json;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String ret = "[com.brightcove.proserve.mediaapi.wrapper.apiobjects.CuePoint (\n" + "\tid:'" + id
				+ "'\n" + "\tname:'" + name + "'\n" + "\tvideoId:'" + videoId + "'\n" + "\ttime:'" + time
				+ "'\n" + "\tforceStop:'" + forceStop + "'\n" + "\ttype:'" + type + "'\n" + "\tmetadata:'"
				+ metadata + "'\n" + ")]";

		return ret;
	}
}
