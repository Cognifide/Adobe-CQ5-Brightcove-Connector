package com.brightcove.proserve.mediaapi.wrapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Playlist;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Playlists;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Video;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Videos;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.PlaylistFieldEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.SortByTypeEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.SortOrderTypeEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.VideoFieldEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.VideoStateFilterEnum;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.MediaApiException;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.WrapperException;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.WrapperExceptionCode;
import com.brightcove.proserve.mediaapi.wrapper.json.JSONUtils;
import com.brightcove.proserve.mediaapi.wrapper.utils.CollectionUtils;

/**
 * <p>This is a wrapper for the HTTP/JSON Media API provided by Brightcove (<a href="http://www.brightcove.com">http://www.brightcove.com</a>)</p>
 * 
 * <p>While this library has been created by a member of Brightcove Professional Services, it <b>IS NOT</b> provided by Brightcove - no support, guarantees or warantees are provided.  Brightcove may change the Media API at any point, rendering this library crippled or disabled until it is updated.</p>
 * 
 * <p>The purpose of this library is to provide a simple interface from Java applications to the Brightcove Media API.  This class provides the interface to the READ portion of the Media API.</p>
 * 
 * <p>This library relies on the 3rd party JSON library from <a href="http://www.json.org/java/">http://www.json.org/java/</a> which must be on the classpath.</p>
 * 
 * <p>For more information on the Media API, see <a href="http://support.brightcove.com/en/docs/getting-started-media-api">http://support.brightcove.com/en/docs/getting-started-media-api</a>.</p>
 * 
 * @author Sander Gates <three.4.clavins.kitchen @at@ gmail.com>
 *
 */
public class ReadApi {
    private Logger     log;
    private String     charSet;
    private String     readProtocolScheme;
    private String     readHost;
    private Integer    readPort;
    private String     readPath;
    private HttpClient httpAgent;
    
    private Boolean    enableUds;
    
    private static final String  READ_API_DEFAULT_SCHEME = "http";
    private static final String  READ_API_DEFAULT_HOST   = "api.brightcove.com";
    private static final Integer READ_API_DEFAULT_PORT   = 80;
    private static final String  READ_API_DEFAULT_PATH   = "/services/library";
    
    private static final Integer MAX_VIDEOS_PER_PAGE    = 100;
    private static final Integer MAX_PLAYLISTS_PER_PAGE = 100;
    
    /**
     * <p>Default constructor</p>
     * 
     * <p>Creates a new Read API wrapper object with default settings<ul>
     * <li>No logging</li>
     * <li>UTF-8 character set</li>
     * <li>Default path to API URL</li>
     * </ul></p>
     */
    public ReadApi(){
        init();
    }
    
    /**
     * <p>Constructor with logging</p>
     * 
     * <p>Creates a new Read API wrapper object with the following settings<ul>
     * <li>Logging to Logger object provided</li>
     * <li>UTF-8 character set</li>
     * <li>Default path to API URL</li>
     * </ul></p>
     * 
     * @param log java.util.logging.Logger object to log to
     */
    public ReadApi(Logger log){
        init();
        
        this.log = log;
    }
    
    /**
     * <p>Constructor with character set</p>
     * 
     * <p>Creates a new Read API wrapper object with the following settings<ul>
     * <li>No logging</li>
     * <li>Character set specified</li>
     * <li>Default path to API URL</li>
     * </ul></p>
     * 
     * @param characterEncoding Character encoding to use for HTTP URLs and responses
     */
    public ReadApi(String characterEncoding){
        init();
        
        this.charSet = characterEncoding;
    }
    
    /**
     * <p>Constructor with character set and logging</p>
     * 
     * <p>Creates a new Read API wrapper object with the following settings<ul>
     * <li>Logging to Logger object provided</li>
     * <li>Character set specified</li>
     * <li>Default path to API URL</li>
     * </ul></p>
     * 
     * @param log java.util.logging.Logger object to log to
     * @param characterEncoding Character encoding to use for HTTP URLs and responses
     */
    public ReadApi(Logger log, String characterEncoding){
        init();
        
        this.log     = log;
        this.charSet = characterEncoding;
    }
    
    /**
     * <p>Called by constructors to initialize variables.</p>
     */
    private void init(){
        log       = null;
        charSet   = "UTF-8";
        httpAgent = new DefaultHttpClient();
        
        enableUds = false;
        
        readProtocolScheme = READ_API_DEFAULT_SCHEME;
        readHost           = READ_API_DEFAULT_HOST;
        readPort           = READ_API_DEFAULT_PORT;
        readPath           = READ_API_DEFAULT_PATH;
    }
    
    /**
     * <p>Overrides the standard Read API Server settings to make calls against a test/staging server.</p>
     * 
     * @param scheme Protocol to use for call (usually http)
     * @param host Host/IP address to call
     * @param port Port to call on server
     * @param path Path to API application on server
     */
    public void OverrideReadApiServerSettings(String scheme, String host, Integer port, String path){
        this.readProtocolScheme = scheme;
        this.readHost           = host;
        this.readPort           = port;
        this.readPath           = path;
    }
    
    /**
     * <p>Builds a Read Media API request URL using the list of parameters provided</p>
     * 
     * <p>Leverages static URL beginning for all Read Media API requests and the character set specified by the constructor</p>
     * 
     * @param parameters URL parameters to pass, including the command name
     * @return URL in the form of a String
     * @throws BrightcoveException if the URL built is syntactically invalid.
     */
    private URI BuildCommandUrl(List<NameValuePair> parameters) throws BrightcoveException {
        try{
            URI ret = URIUtils.createURI(readProtocolScheme, readHost, readPort, readPath, URLEncodedUtils.format(parameters, charSet), null);
            return ret;
        }
        catch(URISyntaxException urise){
            throw new WrapperException(WrapperExceptionCode.INVALID_URL_SYNTAX, "Exception: '" + urise + "'");
        }
    }
    
    /**
     * <p>Issues the command URL to the Media API and returns the response as a String</p>
     * 
     * @param parameters URL parameters to pass, including the command name
     * @return Response from server in the form of a String
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Command URL couldn't be properly built from the parameters given</li>
     *  <li>Client communication to Media API uses improper protocol</li>
     *  <li>I/O exception thrown trying to communicate with Media API</li>
     *  <li>Response from Media API can't be parsed</li>
     *  <li>Media API indicates that there is an error with the request</li>
     * </ul>
     */
    private JSONObject executeCommand(List<NameValuePair> parameters) throws  BrightcoveException {
        if(enableUds){
            parameters.add(new BasicNameValuePair("media_delivery", "http"));
        }
        
        // Build up URL from the parameters provided
        URI commandUrl = BuildCommandUrl(parameters);
        
        if(log != null){
            log.info("JSON Command to execute: '" + commandUrl + "'.");
        }
        
        // Make the request
        HttpGet      httpGet  = new HttpGet(commandUrl);
        HttpResponse response = null;
        try{
            response = httpAgent.execute(httpGet);
        }
        catch(ClientProtocolException cpe){
            throw new WrapperException(WrapperExceptionCode.CLIENT_PROTOCOL_EXCEPTION, "Exception: '" + cpe + "'");
        }
        catch(IOException ioe){
            throw new WrapperException(WrapperExceptionCode.MAPI_IO_EXCEPTION, "Exception: '" + ioe + "'");
        }
        
        // Make sure the HTTP communication was OK (not the same as an error in the Media API reponse)
        Integer statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != 200){
            throw new WrapperException(WrapperExceptionCode.HTTP_ERROR_RESPONSE_CODE, "Response code from HTTP server: '" + statusCode + "'");
        }
        
        // Parse the response
        HttpEntity entity = response.getEntity();
        String     buffer = JSONUtils.parseHttpEntity(entity);
        
        if(log != null){
            log.info("Raw response from server: '" + buffer + "'.");
        }
        
        // Certain responses from the cache don't really return useful
        // JSON - e.g. an invalid reference id in a find_video_by_reference_id
        // will simply return the string "null"
        if("null".equals(buffer)){
            return null;
        }
        
        // Parse JSON
        JSONObject jsonObj = null;
        try{
            jsonObj = new JSONObject(buffer);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_RESPONSE, "JSON Exception: '" + jsone + "'");
        }
        
        // Try to see if there was an error
        MediaApiException mapie  = new MediaApiException(jsonObj);
        if((mapie != null) && (mapie.getResponseCode() != null)){
            throw mapie;
        }
        
        return jsonObj;
    }
    
    // --------------------- Video Read API Methods --------------------------
    
    /**
     * <p>Looks in the specified account for a video with the specified video id (<b>not</b> the reference id).</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param videoId Brightcove id for the video to look for
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return Video object matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>Video can not be found</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Video FindVideoById(String readToken, Long videoId, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",  "find_video_by_id"));
        parameters.add(new BasicNameValuePair("video_id", ""+videoId));
        parameters.add(new BasicNameValuePair("token",    readToken));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
        	log.error("Couldn't find video by Brightcove Id '" + videoId + "'.");
        	return null;
        	//throw new WrapperException(WrapperExceptionCode.MAPI_VIDEO_NOT_FOUND, "Couldn't find video by Brightcove Id '" + videoId + "'.");
        }
        
        Video ret = null;
        try{
            ret = new Video(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEO, "Couldn't parse video from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for all videos</p>
     * 
     * <p>Note that the Media API will only return at most 100 video per request.  So to truly "find all videos", the caller must request "pages" of 1-100 videos at a time.</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param pageSize Number of videos per page (affects the number of videos returned by this call and the offset dictated by the pageNumber parameter)
     * @param pageNumber Number of page to request (page size 100 and page number 1 returns videos 100-199)
     * @param sortBy Sets the field to sort the videos on before cutting up the pages
     * @param sortOrderType Sets the order to sort the videos in before cutting up the pages
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many videos per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos FindAllVideos(String readToken, Integer pageSize, Integer pageNumber, SortByTypeEnum sortBy, SortOrderTypeEnum sortOrderType, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        if(pageSize > MAX_VIDEOS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_VIDEOS_PER_PAGE, "User error - requested " + pageSize + " videos per page; maximum allowed is " + MAX_VIDEOS_PER_PAGE + " videos per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",        "find_all_videos"));
        parameters.add(new BasicNameValuePair("token",          readToken));
        parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        parameters.add(new BasicNameValuePair("sort_by",        ""+sortBy));
        parameters.add(new BasicNameValuePair("sort_order",     ""+sortOrderType));
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for related videos.</p>
     * 
     * <p>From <a href="http://docs.brightcove.com/en/media/">http://docs.brightcove.com/en/media/</a>:<br/>
     * <code>Finds videos related to the given video. Combines the name and short description of the given video and searches for any partial matches in the name, description, and tags of all videos in the Brightcove media library for this account. More precise ways of finding related videos include tagging your videos by subject and using the find_videos_by_tags method to find videos that share the same tags: or creating a playlist that includes videos that you know are related.</code>
     * </p>
     * 
     * @param readToken The authentication token provided to authorize using the Media APIs. A string, generally ending in . (dot).
     * @param videoId The id of the video we'd like related videos for.  Optional (pass null to omit)
     * @param referenceId The publisher-assigned reference id of the video we'd like related videos for.  Optional (pass null to omit)
     * @param pageSize Number of items returned per page. A page is a subset of all of the items that satisfy the request. The maximum page size is 100; if you do not set this argument, or if you set it to an integer > 100, your results will come back as if you had set page_size=100.  Optional (pass null to omit)
     * @param pageNumber The zero-indexed number of the page to return.  Optional (pass null to omit)
     * @param videoFields Set of video fields to populate on the returned Video object.
     * @param customFields Set of custom fields to populate on the returned Video object.
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many videos per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos FindRelatedVideos(String readToken, Long videoId, String referenceId, Integer pageSize, Integer pageNumber, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        if(pageSize > MAX_VIDEOS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_VIDEOS_PER_PAGE, "User error - requested " + pageSize + " videos per page; maximum allowed is " + MAX_VIDEOS_PER_PAGE + " videos per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",        "find_related_videos"));
        parameters.add(new BasicNameValuePair("token",          readToken));
        if(videoId != null){
            parameters.add(new BasicNameValuePair("video_id",       ""+videoId));
        }
        if(referenceId != null){
            parameters.add(new BasicNameValuePair("reference_id",   ""+referenceId));
        }
        if(pageSize != null){
            parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        }
        if(pageNumber != null){
            parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        }
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for videos with the specified video ids (<b>not</b> the reference id).</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param videoIds Brightcove ids for the videos to look for
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos FindVideosByIds(String readToken, Set<Long> videoIds, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",  "find_videos_by_ids"));
        parameters.add(new BasicNameValuePair("token",    readToken));
        
        String videoIdsString = CollectionUtils.JoinToString((Set<?>)videoIds, ",");
        parameters.add(new BasicNameValuePair("video_ids", ""+videoIdsString));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for a video with the specified reference id (<b>not</b> the video id).</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param referenceId Custom reference id for the video to look for
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return Video object matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>Video can not be found</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Video FindVideoByReferenceId(String readToken, String referenceId, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",  "find_video_by_reference_id"));
        parameters.add(new BasicNameValuePair("reference_id", ""+referenceId));
        parameters.add(new BasicNameValuePair("token",    readToken));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_VIDEO_NOT_FOUND, "Couldn't find video by Reference Id '" + referenceId + "'.");
        }
        
        Video ret = null;
        try{
            ret = new Video(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEO, "Couldn't parse video from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for videos with the specified reference ids (<b>not</b> the video id).</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param referenceIds Reference ids for the videos to look for
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>One or more Reference Ids contains a comma</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos FindVideosByReferenceIds(String readToken, Set<String> referenceIds, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        for(String referenceId : referenceIds){
            if(referenceId.contains(",")){
                throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_REFERENCE_ID_WITH_COMMA, "Reference Id '" + referenceId + "' contained a comma.");
            }
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",  "find_videos_by_reference_ids"));
        parameters.add(new BasicNameValuePair("token",    readToken));
        
        String referenceIdsString = CollectionUtils.JoinToString((Set<?>)referenceIds, ",");
        parameters.add(new BasicNameValuePair("reference_ids", ""+referenceIdsString));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Retrieves the videos uploaded by the specified user id. This method can be used to find videos submitted using the consumer- generated media (CGM) module.</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param userId The id of the user whose videos we'd like to retrieve
     * @param pageSize Number of items returned per page. A page is a subset of all of the items that satisfy the request. The maximum page size is 100; if you do not set this argument, or if you set it to an integer > 100, your results will come back as if you had set page_size=100.  Optional (pass null to omit)
     * @param pageNumber The zero-indexed number of the page to return.  Optional (pass null to omit)
     * @param sortBy Sets the field to sort the videos on before cutting up the pages
     * @param sortOrderType Sets the order to sort the videos in before cutting up the pages
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many videos per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos FindVideosByUserId(String readToken, String userId, Integer pageSize, Integer pageNumber, SortByTypeEnum sortBy, SortOrderTypeEnum sortOrderType, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        if(pageSize > MAX_VIDEOS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_VIDEOS_PER_PAGE, "User error - requested " + pageSize + " videos per page; maximum allowed is " + MAX_VIDEOS_PER_PAGE + " videos per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",        "find_videos_by_user_id"));
        parameters.add(new BasicNameValuePair("user_id",        ""+userId));
        parameters.add(new BasicNameValuePair("token",          readToken));
        parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        parameters.add(new BasicNameValuePair("sort_by",        ""+sortBy));
        parameters.add(new BasicNameValuePair("sort_order",     ""+sortOrderType));
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Gets all the videos associated with the given campaign id. Campaigns are a feature of the consumer-generated media (CGM) module</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param campaignId The id of the campaign you'd like to fetch videos for
     * @param pageSize Number of items returned per page. A page is a subset of all of the items that satisfy the request. The maximum page size is 100; if you do not set this argument, or if you set it to an integer > 100, your results will come back as if you had set page_size=100.  Optional (pass null to omit)
     * @param pageNumber The zero-indexed number of the page to return.  Optional (pass null to omit)
     * @param sortBy Sets the field to sort the videos on before cutting up the pages
     * @param sortOrderType Sets the order to sort the videos in before cutting up the pages
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many videos per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos FindVideosByCampaignId(String readToken, String campaignId, Integer pageSize, Integer pageNumber, SortByTypeEnum sortBy, SortOrderTypeEnum sortOrderType, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        if(pageSize > MAX_VIDEOS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_VIDEOS_PER_PAGE, "User error - requested " + pageSize + " videos per page; maximum allowed is " + MAX_VIDEOS_PER_PAGE + " videos per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",        "find_videos_by_campaign_id"));
        parameters.add(new BasicNameValuePair("campaign_id",        ""+campaignId));
        parameters.add(new BasicNameValuePair("token",          readToken));
        parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        parameters.add(new BasicNameValuePair("sort_by",        ""+sortBy));
        parameters.add(new BasicNameValuePair("sort_order",     ""+sortOrderType));
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Gets all the videos that have been modified since the given time</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param fromDate The date, specified in minutes since January 1st, 1970 00:00:00 GMT, of the oldest Video which you would like returned
     * @param filter Filters the set of videos to return
     * @param pageSize Number of items returned per page. A page is a subset of all of the items that satisfy the request. The maximum page size is 100; if you do not set this argument, or if you set it to an integer > 100, your results will come back as if you had set page_size=100.  Optional (pass null to omit)
     * @param pageNumber The zero-indexed number of the page to return.  Optional (pass null to omit)
     * @param sortBy Sets the field to sort the videos on before cutting up the pages
     * @param sortOrderType Sets the order to sort the videos in before cutting up the pages
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many videos per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos FindModifiedVideos(String readToken, Long fromDate, Set<VideoStateFilterEnum> filter, Integer pageSize, Integer pageNumber, SortByTypeEnum sortBy, SortOrderTypeEnum sortOrderType, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        if(pageSize > MAX_VIDEOS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_VIDEOS_PER_PAGE, "User error - requested " + pageSize + " videos per page; maximum allowed is " + MAX_VIDEOS_PER_PAGE + " videos per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",        "find_modified_videos"));
        parameters.add(new BasicNameValuePair("from_date",      ""+fromDate));
        parameters.add(new BasicNameValuePair("token",          readToken));
        parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        parameters.add(new BasicNameValuePair("sort_by",        ""+sortBy));
        parameters.add(new BasicNameValuePair("sort_order",     ""+sortOrderType));
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        String filterString = CollectionUtils.JoinToString((Set<?>)filter, ",");
        if(!("".equals(filterString))){
            parameters.add(new BasicNameValuePair("filter", filterString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }

    /**
     * <p>Searches videos according to the criteria provided by the user. The criteria are constructed using field/value pairs specified in the command.</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param all (optional)        Specifies the field:value pairs for search criteria that MUST be present in the index in order to return a hit in the result set. The format is fieldName:value. If the field's name is not present, it is assumed to be name and shortDescription.
     * @param any (optional)        Specifies the field:value pairs for search criteria AT LEAST ONE of which must be present to return a hit in the result set. The format is fieldName:value. If the field's name is not present, it is assumed to be name and shortDescription.
     * @param none (optional)       Specifies the field:value pairs for search criteria that MUST NOT be present to return a hit in the result set. The format is fieldName:value. If the field's name is not present, it is assumed to be name and shortDescription.
     * @param exact (optional)  Boolean If true, disables fuzzy search and requires an exact match of search terms. A fuzzy search does not require an exact match of the indexed terms, but will return a hit for terms that are closely related based on language-specific criteria. The fuzzy search is available only if your account is based in the United States.
     * @param sortByCriteria (optional)     Specifies the field to sort by, and the direction to sort in. This is specified as: sortFieldName:direction If the direction is not provided, it is assumed to be in ascending order Specify the direction as "asc" for ascending or "desc" for descending. You can sort by the following fields: PUBLISH_DATE, CREATION_DATE, MODIFIED_DATE, PLAYS_TRAILING_WEEK, PLAYS_TOTAL.
     * @param page_size (optional)  Integer Number of items returned per page. A page is a subset of all of the items that satisfy the request. The maximum page size is 100; if you do not set this argument, or if you set it to an integer > 100, your results will come back as if you had set page_size=100.
     * @param page_number (optional)    Integer The zero-indexed number of the page to return.
     * @param get_item_count (optional) Boolean If true, also return how many total results there are.
     * @param video_fields (optional)   EnumSet A comma-separated list of the fields you wish to have populated in the Videos contained in the returned object. If you omit this parameter, the method returns the following fields of the video: id, name, shortDescription, longDescription, creationDate, publisheddate, lastModifiedDate, linkURL, linkText, tags, videoStillURL, thumbnailURL, referenceId, length, economics, playsTotal, playsTrailingWeek. If you use a token with URL access, this method also returns FLVURL, renditions, FLVFullLength, videoFullLength.
     * @param custom_fields (optional)  Set A comma-separated list of the custom fields you wish to have populated in the videos contained in the returned object. If you omit this parameter, no custom fields are returned, unless you include the value 'customFields' in the video_fields parameter.
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many videos per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos SearchVideos(String readToken, List<String> all, List<String> any, List<String> none, Boolean exact, SortByTypeEnum sortBy, SortOrderTypeEnum sortOrderType, Integer pageSize, Integer pageNumber, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        if(pageSize > MAX_VIDEOS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_VIDEOS_PER_PAGE, "User error - requested " + pageSize + " videos per page; maximum allowed is " + MAX_VIDEOS_PER_PAGE + " videos per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",        "search_videos"));
        if(all != null && all.size() > 0){
            for(String allParam : all) {
                parameters.add(new BasicNameValuePair("all", allParam));
            }
        }
        if(any != null && any.size() > 0){
              for(String anyParam : any) {
                parameters.add(new BasicNameValuePair("any", anyParam));
            }
        }
        if(none != null && none.size() > 0){
            parameters.add(new BasicNameValuePair("none", CollectionUtils.JoinToString(none, ",")));
        }
        if(exact != null) {
            parameters.add(new BasicNameValuePair("exact", String.valueOf(exact)));
        }
        parameters.add(new BasicNameValuePair("sort_by",        ""+sortBy + ":" + sortOrderType));
        parameters.add(new BasicNameValuePair("token",          readToken));
        parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Searches through all the videos in this account, and returns a collection of videos whose name, short description, or long description contain a match for the specified text</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param text The text we're searching for
     * @param pageSize Number of items returned per page. A page is a subset of all of the items that satisfy the request. The maximum page size is 100; if you do not set this argument, or if you set it to an integer > 100, your results will come back as if you had set page_size=100.  Optional (pass null to omit)
     * @param pageNumber The zero-indexed number of the page to return.  Optional (pass null to omit)
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many videos per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos FindVideosByText(String readToken, String text, Integer pageSize, Integer pageNumber, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        if(pageSize > MAX_VIDEOS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_VIDEOS_PER_PAGE, "User error - requested " + pageSize + " videos per page; maximum allowed is " + MAX_VIDEOS_PER_PAGE + " videos per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",        "find_videos_by_text"));
        parameters.add(new BasicNameValuePair("text",           ""+text));
        parameters.add(new BasicNameValuePair("token",          readToken));
        parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Performs a search on all the tags of the videos in this account, and returns a collection of videos that contain the specified tags. Note that tags are not case-sensitive</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param andTags Limit the results to those that contain all of these tags
     * @param orTags Limit the results to those that contain at least one of these tags
     * @param pageSize Number of items returned per page. A page is a subset of all of the items that satisfy the request. The maximum page size is 100; if you do not set this argument, or if you set it to an integer > 100, your results will come back as if you had set page_size=100.  Optional (pass null to omit)
     * @param pageNumber The zero-indexed number of the page to return.  Optional (pass null to omit)
     * @param videoFields Set of video fields to populate on the returned Video object
     * @param customFields Set of custom fields to populate on the returned Video object
     * @return List of Video objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many videos per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Videos FindVideosByTags(String readToken, Set<String> andTags, Set<String> orTags, Integer pageSize, Integer pageNumber, SortByTypeEnum sortBy, SortOrderTypeEnum sortOrderType, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields) throws BrightcoveException {
        if(pageSize > MAX_VIDEOS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_VIDEOS_PER_PAGE, "User error - requested " + pageSize + " videos per page; maximum allowed is " + MAX_VIDEOS_PER_PAGE + " videos per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",        "find_videos_by_tags"));
        parameters.add(new BasicNameValuePair("token",          readToken));
        parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        parameters.add(new BasicNameValuePair("sort_by",        ""+sortBy));
        parameters.add(new BasicNameValuePair("sort_order",     ""+sortOrderType));
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        String andString = CollectionUtils.JoinToString((Set<?>)andTags, ",");
        if(!("".equals(andString))){
            parameters.add(new BasicNameValuePair("and_tags", andString));
        }
        
        String orString = CollectionUtils.JoinToString((Set<?>)orTags, ",");
        if(!("".equals(orString))){
            parameters.add(new BasicNameValuePair("or_tags", orString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find videos.");
        }
        
        Videos ret = null;
        try{
            ret = new Videos(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_VIDEOS, "Couldn't parse video list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    
    // --------------------- Playlist Read API Methods -----------------------
    
    /**
     * <p>Looks in the specified account for all playlists</p>
     * 
     * <p>Note that the Media API will only return at most 100 playlists per request.  So to truly "find all videos", the caller must request "pages" of 1-100 playlists at a time.</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param pageSize Number of playlists per page (affects the number of playlists returned by this call and the offset dictated by the pageNumber parameter)
     * @param pageNumber Number of page to request (page size 100 and page number 1 returns playlists 100-199)
     * @param sortBy Sets the field to sort the playlists on before cutting up the pages
     * @param sortOrderType Sets the order to sort the playlists in before cutting up the pages
     * @param videoFields Set of video fields to populate on the returned Video objects
     * @param customFields Set of custom fields to populate on the returned Video objects
     * @param playlistFields A comma-separated list of the fields you wish to have populated in the playlists contained in the returned object. Passing null populates with all fields.
     * @return List of Playlist objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many playlists per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Playlists FindAllPlaylists(String readToken, Integer pageSize, Integer pageNumber, SortByTypeEnum sortBy, SortOrderTypeEnum sortOrderType, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields, EnumSet<PlaylistFieldEnum> playlistFields) throws BrightcoveException {
        if(pageSize > MAX_PLAYLISTS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_PLAYLISTS_PER_PAGE, "User error - requested " + pageSize + " playlists per page; maximum allowed is " + MAX_PLAYLISTS_PER_PAGE + " playlists per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",        "find_all_playlists"));
        parameters.add(new BasicNameValuePair("token",          readToken));
        parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        parameters.add(new BasicNameValuePair("sort_by",        ""+sortBy));
        parameters.add(new BasicNameValuePair("sort_order",     ""+sortOrderType));
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        String playlistFieldsString = CollectionUtils.JoinToString((Set<?>)playlistFields, ",");
        if(!("".equals(playlistFieldsString))){
            parameters.add(new BasicNameValuePair("playlist_fields", playlistFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find playlists.");
        }
        
        Playlists ret = null;
        try{
            ret = new Playlists(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_PLAYLISTS, "Couldn't parse playlist list from JSON.  Exception caught: '" + jsone + "'." + response);
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for the specified playlist</p>
     * 
     * <p>Finds a particular playlist based on its id.</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param playlistId The id of the playlist requested.
     * @param videoFields Set of video fields to populate on the returned Video objects
     * @param customFields Set of custom fields to populate on the returned Video objects
     * @param playlistFields A comma-separated list of the fields you wish to have populated in the playlists contained in the returned object. Passing null populates with all fields.
     * @return Playlist requested
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>Playlist requested does not exist</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Playlist FindPlaylistById(String readToken, Long playlistId, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields, EnumSet<PlaylistFieldEnum> playlistFields) throws BrightcoveException {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",     "find_playlist_by_id"));
        parameters.add(new BasicNameValuePair("token",       readToken));
        parameters.add(new BasicNameValuePair("playlist_id", ""+playlistId));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        String playlistFieldsString = CollectionUtils.JoinToString((Set<?>)playlistFields, ",");
        if(!("".equals(playlistFieldsString))){
            parameters.add(new BasicNameValuePair("playlist_fields", playlistFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find playlist.");
        }
        
        Playlist ret = null;
        try{
            ret = new Playlist(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_PLAYLIST, "Couldn't parse playlist from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for the specified playlist</p>
     * 
     * <p>Finds a particular playlist based on its reference id.</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param referenceId The reference id of the playlist requested.
     * @param videoFields Set of video fields to populate on the returned Video objects
     * @param customFields Set of custom fields to populate on the returned Video objects
     * @param playlistFields A comma-separated list of the fields you wish to have populated in the playlists contained in the returned object. Passing null populates with all fields.
     * @return Playlist requested
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>Playlist requested does not exist</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Playlist FindPlaylistByReferenceId(String readToken, String referenceId, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields, EnumSet<PlaylistFieldEnum> playlistFields) throws BrightcoveException {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",      "find_playlist_by_reference_id"));
        parameters.add(new BasicNameValuePair("token",        readToken));
        parameters.add(new BasicNameValuePair("reference_id", referenceId));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        String playlistFieldsString = CollectionUtils.JoinToString((Set<?>)playlistFields, ",");
        if(!("".equals(playlistFieldsString))){
            parameters.add(new BasicNameValuePair("playlist_fields", playlistFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find playlist.");
        }
        
        Playlist ret = null;
        try{
            ret = new Playlist(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_PLAYLIST, "Couldn't parse playlist from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for the specified playlists</p>
     * 
     * <p>Finds particular playlists based on ids.</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param playlistIds The ids of the playlists requested.
     * @param videoFields Set of video fields to populate on the returned Video objects
     * @param customFields Set of custom fields to populate on the returned Video objects
     * @param playlistFields A comma-separated list of the fields you wish to have populated in the playlists contained in the returned object. Passing null populates with all fields.
     * @return List of Playlist objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many playlists per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Playlists FindPlaylistsByIds(String readToken, Set<Long> playlistIds, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields, EnumSet<PlaylistFieldEnum> playlistFields) throws BrightcoveException {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",     "find_playlists_by_ids"));
        parameters.add(new BasicNameValuePair("token",       readToken));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        String playlistFieldsString = CollectionUtils.JoinToString((Set<?>)playlistFields, ",");
        if(!("".equals(playlistFieldsString))){
            parameters.add(new BasicNameValuePair("playlist_fields", playlistFieldsString));
        }
        
        String playlistIdString = CollectionUtils.JoinToString((Set<?>)playlistIds, ",");
        if(!("".equals(playlistIdString))){
            parameters.add(new BasicNameValuePair("playlist_ids", playlistIdString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find playlists.");
        }
        
        Playlists ret = null;
        try{
            ret = new Playlists(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_PLAYLISTS, "Couldn't parse playlist list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for the specified playlists</p>
     * 
     * <p>Finds particular playlists based on reference ids.</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param referenceIds The reference ids of the playlists requested.
     * @param videoFields Set of video fields to populate on the returned Video objects
     * @param customFields Set of custom fields to populate on the returned Video objects
     * @param playlistFields A comma-separated list of the fields you wish to have populated in the playlists contained in the returned object. Passing null populates with all fields.
     * @return List of Playlist objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Playlists FindPlaylistsByReferenceIds(String readToken, Set<String> referenceIds, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields, EnumSet<PlaylistFieldEnum> playlistFields) throws BrightcoveException {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        parameters.add(new BasicNameValuePair("command",     "find_playlists_by_reference_ids"));
        parameters.add(new BasicNameValuePair("token",       readToken));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        String playlistFieldsString = CollectionUtils.JoinToString((Set<?>)playlistFields, ",");
        if(!("".equals(playlistFieldsString))){
            parameters.add(new BasicNameValuePair("playlist_fields", playlistFieldsString));
        }
        
        String referenceIdString = CollectionUtils.JoinToString((Set<?>)referenceIds, ",");
        if(!("".equals(referenceIdString))){
            parameters.add(new BasicNameValuePair("reference_ids", referenceIdString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find playlists.");
        }
        
        Playlists ret = null;
        try{
            ret = new Playlists(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_PLAYLISTS, "Couldn't parse playlist list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p>Looks in the specified account for the specified playlists</p>
     * 
     * <p>Finds particular playlists based on player id.</p>
     * 
     * @param readToken Read Media API token for the account.  Note that the privileges of the token can change the fields populated in the returned Video object.
     * @param playerId The player id whose playlists we want to return.
     * @param pageSize Number of playlists per page (affects the number of playlists returned by this call and the offset dictated by the pageNumber parameter)
     * @param pageNumber Number of page to request (page size 100 and page number 1 returns playlists 100-199)
     * @param videoFields Set of video fields to populate on the returned Video objects
     * @param customFields Set of custom fields to populate on the returned Video objects
     * @param playlistFields A comma-separated list of the fields you wish to have populated in the playlists contained in the returned object. Passing null populates with all fields.
     * @return List of Playlist objects matching criteria
     * @throws BrightcoveException If any of the following are true:<ul>
     *  <li>Request to the Media API fails</li>
     *  <li>Media API reports an error with the request</li>
     *  <li>User requests too many playlists per page</li>
     *  <li>Response from the Media API couldn't be parsed</li>
     * </ul>
     */
    public Playlists FindPlaylistsForPlayerId(String readToken, String playerId, Integer pageSize, Integer pageNumber, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields, EnumSet<PlaylistFieldEnum> playlistFields) throws BrightcoveException {
        if(pageSize > MAX_PLAYLISTS_PER_PAGE){
            throw new WrapperException(WrapperExceptionCode.USER_REQUESTED_TOO_MANY_PLAYLISTS_PER_PAGE, "User error - requested " + pageSize + " playlists per page; maximum allowed is " + MAX_PLAYLISTS_PER_PAGE + " playlists per page.");
        }
        
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("command",        "find_playlists_for_player_id"));
        parameters.add(new BasicNameValuePair("token",          readToken));
        parameters.add(new BasicNameValuePair("page_size",      ""+pageSize));
        parameters.add(new BasicNameValuePair("page_number",    ""+pageNumber));
        parameters.add(new BasicNameValuePair("get_item_count", "true"));
        parameters.add(new BasicNameValuePair("player_id",      playerId));
        
        String videoFieldsString = CollectionUtils.JoinToString((Set<?>)videoFields, ",");
        if(!("".equals(videoFieldsString))){
            parameters.add(new BasicNameValuePair("video_fields", videoFieldsString));
        }
        
        String customFieldsString = CollectionUtils.JoinToString((Set<?>)customFields, ",");
        if(!("".equals(customFieldsString))){
            parameters.add(new BasicNameValuePair("custom_fields", customFieldsString));
        }
        
        String playlistFieldsString = CollectionUtils.JoinToString((Set<?>)playlistFields, ",");
        if(!("".equals(playlistFieldsString))){
            parameters.add(new BasicNameValuePair("playlist_fields", playlistFieldsString));
        }
        
        JSONObject response = executeCommand(parameters);
        if(response == null){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNKNOWN_NULL, "Couldn't find playlists.");
        }
        
        Playlists ret = null;
        try{
            ret = new Playlists(response);
        }
        catch(JSONException jsone){
            throw new WrapperException(WrapperExceptionCode.MAPI_UNPARSABLE_PLAYLISTS, "Couldn't parse playlist list from JSON.  Exception caught: '" + jsone + "'.");
        }
        
        return ret;
    }
    
    /**
     * <p><i><u>For advanced users...</u></i></p>
     * <p>If true, forces the returned URLs in Renditions to be Progressive
     *    Download instead of streaming.  Note that in order for this to work,
     *    your account must be enabled for Universal Delivery - something that
     *    can only be done by Brightcove Support.</p>
     */
    public void setEnableUds(Boolean enableUds){
        this.enableUds = enableUds;
    }
    
    /**
     * <p><i><u>For advanced users...</u></i></p>
     * <p>If true, forces the returned URLs in Renditions to be Progressive
     *    Download instead of streaming.  Note that in order for this to work,
     *    your account must be enabled for Universal Delivery - something that
     *    can only be done by Brightcove Support.</p>
     * 
     * @return Whether or not UDS URLs should be returned by the Media API
     */
    public Boolean getEnableUds(){
        return enableUds;
    }
}
