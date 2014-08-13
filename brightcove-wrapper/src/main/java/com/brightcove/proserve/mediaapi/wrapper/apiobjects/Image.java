package com.brightcove.proserve.mediaapi.wrapper.apiobjects;

import org.json.JSONException;
import org.json.JSONObject;

import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.ImageTypeEnum;

/**
 * <p>Represents an Image object to/from the Media API.</p>
 * 
 * <p>From the Brightcove documentation on 2009/08/18 (<a href="http://support.brightcove.com/en/docs/media-api-objects-reference">http://support.brightcove.com/en/docs/media-api-objects-reference</a>):<br/>
 * <code>This object represents metadata about an image file in your account. Images are associated with videos as thumbnail images or video still images. An image can be a JPEG, GIF, or PNG-formatted image. Note that when creating a new image asset, the only property that is required is type. If you are not uploading a file, the remoteUrl property is also required. For more information, see <a href="http://support.brightcove.com/en/docs/adding-images-videos-media-api">Adding Images to Videos with the Media API.</a></code>
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
 * 			<td>A number that uniquely identifies this Image. This id is automatically assigned by Brightcove when the Image is created.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>referenceId</td>
 * 			<td>String</td>
 * 			<td>no</td>
 * 			<td>A user-specified id that uniquely identifies this Image.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>type</td>
 * 			<td>ImageTypeEnum</td>
 * 			<td>no</td>
 * 			<td>THUMBNAIL or VIDEO_STILL. The type is writable and required when you create an Image; it cannot subsequently be changed.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>remoteUrl</td>
 * 			<td>String</td>
 * 			<td>no</td>
 * 			<td>The URL of a remote image file. This property is required if you are not uploading a file for the image asset.</td>
 * 		</tr>
 * 		<tr>
 * 			<td>displayName</td>
 * 			<td>String</td>
 * 			<td>no</td>
 * 			<td>The name of the asset, which will be displayed in the Media module.</td>
 * 		</tr>
 * 	</tbody>
 * </table>
 * </p>
 * 
 * @author Sander Gates <three.4.clavins.kitchen @at@ gmail.com>
 *
 */
public class Image {
	private Long          id;
	private String        referenceId;
	private ImageTypeEnum type;
	private String        remoteUrl;
	private String        displayName;
	
	/**
	 * <p>Default Constructor.</p>
	 * 
	 * <p>All fields set to null to start - required fields must be set before calling Write Media API.</p>
	 * 
	 */
	public Image(){
		initAll();
	}
	
	/**
	 * <p>Constructor using JSON string.</p>
	 * 
	 * <p>Given a JSON string from the Media API, attempts to construct a new Image object and fill out all of the fields defined.  All other fields will be null.</p>
	 * 
	 */
	public Image(String json) throws JSONException {
		initAll();
		
		if(json == null){
			throw new JSONException("[ERR] Image can not be parsed from null JSON string.");
		}
		
		JSONObject jsonObj = new JSONObject(json);
		
		finishConstruction(jsonObj);
	}
	
	/**
	 * <p>Constructor using JSON object.</p>
	 * 
	 * <p>Given a JSON object from the Media API, attempts to construct a new Image object and fill out all of the fields defined.  All other fields will be null.</p>
	 * 
	 */
	public Image(JSONObject jsonObj) throws JSONException {
		initAll();
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
			else if("id".equals(rootKey)){
				id = (Long)rootValue;
			}
			else if("referenceId".equals(rootKey)){
				referenceId = rootValue.toString();
			}
			else if("type".equals(rootKey)){
				if("THUMBNAIL".equals(rootValue.toString())){
					type = ImageTypeEnum.THUMBNAIL;
				}
				else if("VIDEO_STILL".equals(rootValue.toString())){
					type = ImageTypeEnum.VIDEO_STILL;
				}
				else{
					throw new JSONException("[ERR] Unknown image type '" + rootValue.toString() + "'.");
				}
			}
			else if("remoteUrl".equals(rootKey)){
				remoteUrl = rootValue.toString();
			}
			else if("displayName".equals(rootKey)){
				displayName = rootValue.toString();
			}
			else{
				throw new JSONException("[ERR] Unknown root key '" + rootKey + "'='" + rootValue + "'.");
			}
		}
	}
	
	/**
	 * <p>Fully initializes the video object by setting all fields to null</p>
	 */
	public void initAll() {
		id          = null;
		referenceId = null;
		type        = null;
		remoteUrl   = null;
		displayName = null;
	}
	
	/**
	 * <p>Gets the id for this Image.</p>
	 * 
	 * <p><code>A number that uniquely identifies this Image. This id is automatically assigned by Brightcove when the Image is created.</code></p>
	 * 
	 * @return The id for this Image
	 */
	public Long getId(){
		return id;
	}
	
	/**
	 * <p>Gets the reference id for this Image.</p>
	 * 
	 * <p><code>A user-specified id that uniquely identifies this Image.</code></p>
	 * 
	 * @return Reference id for this Image
	 */
	public String getReferenceId(){
		return referenceId;
	}
	
	/**
	 * <p>Gets the type for this Image.</p>
	 * 
	 * <p><code>THUMBNAIL or VIDEO_STILL. The type is writable and required when you create an Image; it cannot subsequently be changed.</code></p>
	 * 
	 * @return Type for this Image 
	 */
	public ImageTypeEnum getType(){
		return type;
	}
	
	/**
	 * <p>Gets the remote URL for this Image.</p>
	 * 
	 * <p><code>The URL of a remote image file. This property is required if you are not uploading a file for the image asset.</code></p>
	 * 
	 * @return Remote URL for this Image
	 */
	public String getRemoteUrl(){
		return remoteUrl;
	}
	
	/**
	 * <p>Gets the display name for this Image.</p>
	 * 
	 * <p><code>The name of the asset, which will be displayed in the Media module.</code></p>
	 * 
	 * @return Display name for this Image
	 */
	public String getDisplayName(){
		return displayName;
	}
	
	
	
	
	/**
	 * <p>Sets the id for this Image.</p>
	 * 
	 * <p><code>A number that uniquely identifies this Image. This id is automatically assigned by Brightcove when the Image is created.</code></p>
	 * 
	 * @param id The id for this Image
	 */
	public void setId(Long id){
		this.id = id;
	}
	
	/**
	 * <p>Sets the reference id for this Image.</p>
	 * 
	 * <p><code>A user-specified id that uniquely identifies this Image.</code></p>
	 * 
	 * @param referenceId Reference id for this Image
	 */
	public void setReferenceId(String referenceId){
		this.referenceId = referenceId;
	}
	
	/**
	 * <p>Sets the type for this Image.</p>
	 * 
	 * <p><code>THUMBNAIL or VIDEO_STILL. The type is writable and required when you create an Image; it cannot subsequently be changed.</code></p>
	 * 
	 * @param type Type for this Image 
	 */
	public void setType(ImageTypeEnum type){
		this.type = type;
	}
	
	/**
	 * <p>Sets the remote URL for this Image.</p>
	 * 
	 * <p><code>The URL of a remote image file. This property is required if you are not uploading a file for the image asset.</code></p>
	 * 
	 * @param remoteUrl Remote URL for this Image
	 */
	public void setRemoteUrl(String remoteUrl){
		this.remoteUrl = remoteUrl;
	}
	
	/**
	 * <p>Sets the display name for this Image.</p>
	 * 
	 * <p><code>The name of the asset, which will be displayed in the Media module.</code></p>
	 * 
	 * @param displayName Display name for this Image
	 */
	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}
	
	/**
	 * <p>Converts the image into a JSON object suitable for use with the Media API</p>
	 * 
	 * @return JSON object representing the image
	 */
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		
		if(id != null){
			json.put("id",   id);
		}
		if(referenceId != null){
			json.put("referenceId", referenceId);
		}
		if(type != null){
			json.put("type", type);
		}
		if(remoteUrl != null){
			json.put("remoteUrl", remoteUrl);
		}
		if(displayName != null){
			json.put("displayName", displayName);
		}
		
		return json;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String ret = "[com.brightcove.proserve.mediaapi.wrapper.apiobjects.Image (\n" +
			"\tid:'"          + id          + "'\n" +
			"\treferenceId:'" + referenceId + "'\n" +
			"\ttype:'"        + type        + "'\n" +
			"\tremoteUrl:'"   + remoteUrl   + "'\n" +
			"\tdisplayName:'" + displayName + "'\n" +
			")]";
		
		return ret;
	}
}
