package com.brightcove.proserve.mediaapi.wrapper.apiobjects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.PlaylistTypeEnum;

/**
 * <p>Represents a Playlist object to/from the Media API.</p>
 * 
 * <p>From the Brightcove documentation on 2009/08/18 (<a href="http://support.brightcove.com/en/docs/media-api-objects-reference#Playlist">http://support.brightcove.com/en/docs/media-api-objects-reference#Playlist</a>):<br/>
 * <code>The Playlist object is a collection of <a href="http://support.brightcove.com/en/docs/media-api-objects-reference#Video">Videos</a>. A Playlist has the following properties::</code>
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
 * 			<td>id</td>
 * 			<td>long</td>
 * 			<td>yes</td>
 * 			<td>A number that uniquely identifies this Playlist. This id is automatically assigned when the Playlist is created.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>referenceId</td>
 * 			<td>String</td>
 * 			<td>no</td>
 * 			<td>A user-specified id, limited to 150 characters, that uniquely identifies this Playlist. Note that the find_playlists_by_reference_ids method cannot handle referenceIds that contain commas, so you may want to avoid using commas in referenceId values.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>accountId</td>
 * 			<td>long</td>
 * 			<td>yes</td>
 * 			<td>A number that uniquely identifies the account to which this Playlist belongs, assigned by Brightcove.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>name</td>
 * 			<td>String</td>
 * 			<td>no</td>
 * 			<td>The title of this Playlist, limited to 50 characters. The name is a required property when you create a playlist.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>shortDescription</td>
 * 			<td>String</td>
 * 			<td>no</td>
 * 			<td>A short description describing this Playlist, limited to 250 characters.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>videoIds</td>
 * 			<td>List</td>
 * 			<td>no</td>
 * 			<td>A list of the ids of the <a href="http://support.brightcove.com/en/docs/media-api-objects-reference#Video">Videos</a> that are encapsulated in this Playlist.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>videos</td>
 * 			<td>List</td>
 * 			<td>no</td>
 * 			<td>A list of the <a href="http://support.brightcove.com/en/docs/media-api-objects-reference#Video">Video</a> objects that are encapsulated in this Playlist.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>playlistType</td>
 * 			<td>Enum</td>
 * 			<td>no</td>
 * 			<td>Options are OLDEST_TO_NEWEST, NEWEST_TO_OLDEST, ALPHABETICAL, PLAYSTOTAL, and PLAYS_TRAILING_WEEK (each of which is a smart playlist, ordered as indicated) or EXPLICIT (a manual playlist). The playlistType is a required property when you create a playlist.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>filterTags</td>
 * 			<td>List</td>
 * 			<td>no</td>
 * 			<td>A list of the tags that define this smart playlist. For example:<br/><br/>"filterTags":["Sitka","ticks"]</td>
 * 		</tr>
 * 		<tr>
 * 			<td>thumbnailURL</td>
 * 			<td>String</td>
 * 			<td>yes</td>
 * 			<td>The URL of the thumbnail associated with this Playlist.</td>
 * 		</tr>
 * 	</tbody>
 * </table>
 * </p>
 * 
 * @author Sander Gates <three.4.clavins.kitchen @at@ gmail.com>
 *
 */
public class Playlist {
	private Long    id;
	private String  referenceId;
	private Long    accountId;
	private String  name;
	private String  shortDescription;
	private String  thumbnailUrl;
	
	private List<Long>   videoIds;
	private List<Video>  videos;
	private List<String> filterTags;
	
	private PlaylistTypeEnum playlistType;
	
	/**
	 * <p>Default Constructor.</p>
	 * 
	 * <p>All fields set to null to start - required fields must be set before calling Write Media API.</p>
	 * 
	 */
	public Playlist(){
		initAll();
	}
	
	/**
	 * <p>Constructor using JSON string.</p>
	 * 
	 * <p>Given a JSON string from the Media API, attempts to construct a new Playlist object and fill out all of the fields defined.  All other fields will be null.</p>
	 * 
	 */
	public Playlist(String json) throws JSONException {
		initAll();
		
		if(json == null){
			throw new JSONException("[ERR] Playlist can not be parsed from null JSON string.");
		}
		
		JSONObject jsonObj = new JSONObject(json);
		
		finishConstruction(jsonObj);
	}
	
	/**
	 * <p>Constructor using JSON object.</p>
	 * 
	 * <p>Given a JSON object from the Media API, attempts to construct a new Playlist object and fill out all of the fields defined.  All other fields will be null.</p>
	 * 
	 */
	public Playlist(JSONObject jsonObj) throws JSONException {
		finishConstruction(jsonObj);
	}
	
	/**
	 * <p>Private method to finish construction for other constructors</p>
	 * 
	 * @param jsonObj
	 * @throws JSONException
	 */
	private void finishConstruction(JSONObject jsonObj) throws JSONException {
		String[] rootKeys = JSONObject.getNames(jsonObj);
		
		for(String rootKey : rootKeys){
			Object rootValue = jsonObj.get(rootKey);
			
			if((rootValue == null) || ("null".equals(rootValue.toString()))){
				// Don't bother setting the attribute, it should already be null
			}
			else if("name".equals(rootKey)){
				name = (String)rootValue;
			}
			else if("id".equals(rootKey)){
				id = (Long)rootValue;
			}
			else if("accountId".equals(rootKey)){
				Long rootLong = jsonObj.getLong(rootKey);
				accountId = rootLong;
			}
			else if("referenceId".equals(rootKey)){
				referenceId = rootValue.toString();
			}
			else if("shortDescription".equals(rootKey)){
				shortDescription = rootValue.toString();
			}
			else if("thumbnailURL".equals(rootKey)){
				thumbnailUrl = rootValue.toString();
			}
			else if("videoIds".equals(rootKey)){
				videoIds = new ArrayList<Long>();
				
				JSONArray idsArray = jsonObj.getJSONArray(rootKey);
				for(int idIdx=0;idIdx<idsArray.length();idIdx++){
					Long id = new Long((idsArray.get(idIdx)).toString());
					videoIds.add(id);
				}
			}
			else if("videos".equals(rootKey)){
				videos = new ArrayList<Video>();
				
				JSONArray videoArray = jsonObj.getJSONArray(rootKey);
				for(int videoIdx=0;videoIdx<videoArray.length();videoIdx++){
					Video video = new Video(videoArray.getString(videoIdx).toString());
					videos.add(video);
				}
			}
			else if("playlistType".equals(rootKey)){
				if(rootValue.toString().equals("OLDEST_TO_NEWEST")){
					playlistType = PlaylistTypeEnum.OLDEST_TO_NEWEST;
				}
				else if(rootValue.toString().equals("NEWEST_TO_OLDEST")){
					playlistType = PlaylistTypeEnum.NEWEST_TO_OLDEST;
				}
				else if(rootValue.toString().equals("ALPHABETICAL")){
					playlistType = PlaylistTypeEnum.ALPHABETICAL;
				}
				else if(rootValue.toString().equals("PLAYSTOTAL")){
					playlistType = PlaylistTypeEnum.PLAYSTOTAL;
				}
				else if(rootValue.toString().equals("PLAYS_TRAILING_WEEK")){
					playlistType = PlaylistTypeEnum.PLAYS_TRAILING_WEEK;
				}
				else if(rootValue.toString().equals("EXPLICIT")){
					playlistType = PlaylistTypeEnum.EXPLICIT;
				}
				else{
					throw new JSONException("[ERR] Media API specified invalid value for playlist type '" + rootValue + "'.  Acceptable values are 'OLDEST_TO_NEWEST', 'NEWEST_TO_OLDEST', 'ALPHABETICAL', 'PLAYSTOTAL', 'PLAYS_TRAILING_WEEK', 'EXPLICIT'.");
				}
			}
			else if("filterTags".equals(rootKey)){
				filterTags = new ArrayList<String>();
				
				JSONArray tagArray = jsonObj.getJSONArray(rootKey);
				for(int tagIdx=0;tagIdx<tagArray.length();tagIdx++){
					String tag = tagArray.get(tagIdx).toString();
					filterTags.add(tag);
				}
			}
			else{
				throw new JSONException("[ERR] Unknown root key '" + rootKey + "'='" + rootValue + "'.");
			}
		}
		
		String jsonName = (String)jsonObj.get("name");
		name = jsonName;
	}
	
	/**
	 * <p>Fully initializes the playlist object by setting all fields to null</p>
	 */
	public void initAll() {
		id                 = null;
		referenceId        = null;
		accountId          = null;
		name               = null;
		shortDescription   = null;
		thumbnailUrl       = null;
		videoIds           = null;
		videos             = null;
		filterTags         = null;
		playlistType       = null;
	}
	
	/**
	 * <p>Gets the id for this Playlist.</p>
	 * 
	 * <p><code>A number that uniquely identifies this Playlist. This id is automatically assigned when the Playlist is created.</code></p>
	 * 
	 * @return The id for this Playlist
	 */
	public Long getId(){
		return id;
	}
	
	/**
	 * <p>Gets the reference id for this Playlist.</p>
	 * 
	 * <p><code>A user-specified id, limited to 150 characters, that uniquely identifies this Playlist. Note that the find_playlists_by_reference_ids method cannot handle referenceIds that contain commas, so you may want to avoid using commas in referenceId values.</code></p>
	 * 
	 * @return Reference id for this Playlist
	 */
	public String getReferenceId(){
		return referenceId;
	}
	
	/**
	 * <p>Gets the account id for this Playlist.</p>
	 * 
	 * <p><code>A number that uniquely identifies the account to which this Playlist belongs, assigned by Brightcove.</code></p>
	 * 
	 * @return Account id for this Playlist
	 */
	public Long getAccountId(){
		return accountId;
	}
	
	/**
	 * <p>Gets the name (title) for this Playlist.</p>
	 * 
	 * <p><code>The title of this Playlist, limited to 50 characters. The name is a required property when you create a playlist.</code></p>
	 * 
	 * @return Name of the Playlist
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * <p>Gets the short description for this Playlist.</p>
	 * 
	 * <p><code>A short description describing this Playlist, limited to 250 characters.</code></p>
	 * 
	 * @return Short description for this Playlist
	 */
	public String getShortDescription(){
		return shortDescription;
	}
	
	/**
	 * <p>Gets the Video Ids for this Playlist.</p>
	 * 
	 * <p><code>A list of the ids of the <a href="http://support.brightcove.com/en/docs/media-api-objects-reference#Video">Videos</a> that are encapsulated in this Playlist.</code></p>
	 * 
	 * @return Video Ids for this Playlist
	 */
	public List<Long> getVideoIds(){
		return videoIds;
	}
	
	/**
	 * <p>Gets the Videos for this Playlist.</p>
	 * 
	 * <p><code>A list of the <a href="http://support.brightcove.com/en/docs/media-api-objects-reference#Video">Video</a> objects that are encapsulated in this Playlist.</code></p>
	 * 
	 * @return Videos for this Playlist
	 */
	public List<Video> getVideos(){
		return videos;
	}
	
	/**
	 * <p>Gets the type for this Playlist.</p>
	 * 
	 * <p><code>Options are OLDEST_TO_NEWEST, NEWEST_TO_OLDEST, ALPHABETICAL, PLAYSTOTAL, and PLAYS_TRAILING_WEEK (each of which is a smart playlist, ordered as indicated) or EXPLICIT (a manual playlist). The playlistType is a required property when you create a playlist.</code></p>
	 * 
	 * @return Type for this Playlist
	 */
	public PlaylistTypeEnum getPlaylistType(){
		return playlistType;
	}
	
	/**
	 * <p>Gets the filter tags for this Playlist.</p>
	 * 
	 * <p><code>A list of the tags that define this smart playlist. For example:<br/><br/>"filterTags":["Sitka","ticks"]</code></p>
	 * 
	 * @return Filter tags for this Playlist
	 */
	public List<String> getFilterTags(){
		return filterTags;
	}
	
	/**
	 * <p>Gets the thumbnail URL for this Playlist.</p>
	 * 
	 * <p><code>The URL of the thumbnail associated with this Playlist.</code></p>
	 * 
	 * @return Thumbnail URL for this Playlist
	 */
	public String getThumbnailUrl(){
		return thumbnailUrl;
	}
	
	/**
	 * <p>Sets the id for this Playlist.</p>
	 * 
	 * <p><code>A number that uniquely identifies this Playlist. This id is automatically assigned when the Playlist is created.</code></p>
	 * 
	 * @param id The id for this Playlist
	 */
	public void setId(Long id){
		this.id = id;
	}
	
	/**
	 * <p>Sets the reference id for this Playlist.</p>
	 * 
	 * <p><code>A user-specified id, limited to 150 characters, that uniquely identifies this Playlist. Note that the find_playlists_by_reference_ids method cannot handle referenceIds that contain commas, so you may want to avoid using commas in referenceId values.</code></p>
	 * 
	 * @param referenceId Reference id for this Playlist
	 */
	public void setReferenceId(String referenceId){
		this.referenceId = referenceId;
	}
	
	/**
	 * <p>Sets the account id for this Playlist.</p>
	 * 
	 * <p><code>A number that uniquely identifies the account to which this Playlist belongs, assigned by Brightcove.</code></p>
	 * 
	 * @param accountId Account id for this Playlist
	 */
	public void setAccountId(Long accountId){
		this.accountId = accountId;
	}
	
	/**
	 * <p>Sets the name (title) for this Playlist.</p>
	 * 
	 * <p><code>The title of this Playlist, limited to 50 characters. The name is a required property when you create a playlist.</code></p>
	 * 
	 * @param name Name of the Playlist
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * <p>Sets the short description for this Playlist.</p>
	 * 
	 * <p><code>A short description describing this Playlist, limited to 250 characters.</code></p>
	 * 
	 * @param shortDescription Short description for this Playlist
	 */
	public void setShortDescription(String shortDescription){
		this.shortDescription = shortDescription;
	}
	
	/**
	 * <p>Sets the Video Ids for this Playlist.</p>
	 * 
	 * <p><code>A list of the ids of the <a href="http://support.brightcove.com/en/docs/media-api-objects-reference#Video">Videos</a> that are encapsulated in this Playlist.</code></p>
	 * 
	 * @param videoIds Video Ids for this Playlist
	 */
	public void setVideoIds(List<Long> videoIds){
		this.videoIds = videoIds;
	}
	
	/**
	 * <p>Sets the Videos for this Playlist.</p>
	 * 
	 * <p><code>A list of the <a href="http://support.brightcove.com/en/docs/media-api-objects-reference#Video">Video</a> objects that are encapsulated in this Playlist.</code></p>
	 * 
	 * @param videos Videos for this Playlist
	 */
	public void setVideos(List<Video> videos){
		this.videos = videos;
	}
	
	/**
	 * <p>Sets the type for this Playlist.</p>
	 * 
	 * <p><code>Options are OLDEST_TO_NEWEST, NEWEST_TO_OLDEST, ALPHABETICAL, PLAYSTOTAL, and PLAYS_TRAILING_WEEK (each of which is a smart playlist, ordered as indicated) or EXPLICIT (a manual playlist). The playlistType is a required property when you create a playlist.</code></p>
	 * 
	 * @param playlistType Type for this Playlist
	 */
	public void setPlaylistType(PlaylistTypeEnum playlistType){
		this.playlistType = playlistType;
	}
	
	/**
	 * <p>Sets the filter tags for this Playlist.</p>
	 * 
	 * <p><code>A list of the tags that define this smart playlist. For example:<br/><br/>"filterTags":["Sitka","ticks"]</code></p>
	 * 
	 * @param filterTags Filter tags for this Playlist
	 */
	public void setFilterTags(List<String> filterTags){
		this.filterTags = filterTags;
	}
	
	/**
	 * <p>Sets the thumbnail URL for this Playlist.</p>
	 * 
	 * <p><code>The URL of the thumbnail associated with this Playlist.</code></p>
	 * 
	 * @param thumbnailUrl Thumbnail URL for this Playlist
	 */
	public void setThumbnailUrl(String thumbnailUrl){
		this.thumbnailUrl = thumbnailUrl;
	}
	
	/**
	 * <p>Converts the video into a JSON object suitable for use with the Media API</p>
	 * 
	 * @return JSON object representing the video
	 */
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		
		if(name != null){
			json.put("name", name);
		}
		if(id != null){
			json.put("id", id);
		}
		if(referenceId != null){
			json.put("referenceId", referenceId);
		}
		if(accountId != null){
			json.put("accountId", accountId);
		}
		if(shortDescription != null){
			json.put("shortDescription", shortDescription);
		}
		if(thumbnailUrl != null){
			json.put("thumbnailURL", thumbnailUrl);
		}
		if(videoIds != null){
			JSONArray idArray = new JSONArray();
			for(Long videoId : videoIds){
				idArray.put(videoId);
			}
			json.put("videoIds", idArray);
		}
		if(videos != null){
			JSONArray videoArray = new JSONArray();
			for(Video video : videos){
				videoArray.put(video.toJson());
			}
			json.put("videos", videoArray);
		}
		if(filterTags != null){
			JSONArray tagArray = new JSONArray();
			for(String tag : filterTags){
				tagArray.put(tag);
			}
			json.put("filterTags", tagArray);
		}
		if(playlistType != null){
			json.put("playlistType", playlistType);
		}
		
		return json;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String ret = "[com.brightcove.proserve.mediaapi.wrapper.apiobjects.Playlist (\n" +
			"\tname:'"             + name             + "'\n" +
			"\tid:'"               + id               + "'\n" +
			"\treferenceId:'"      + referenceId      + "'\n" +
			"\taccountId:'"        + accountId        + "'\n" +
			"\tshortDescription:'" + shortDescription + "'\n" +
			"\tthumbnailUrl:'"     + thumbnailUrl     + "'\n" +
			"\tvideoIds:'"         + videoIds         + "'\n" +
			"\tvideos:'"           + videos           + "'\n" +
			"\tfilterTags:'"       + filterTags       + "'\n" +
			"\tplaylistType:'"     + playlistType     + "'\n" +
			")]";
		
		return ret;
	}
}
