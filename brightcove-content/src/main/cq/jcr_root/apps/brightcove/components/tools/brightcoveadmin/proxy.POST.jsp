<%@ page trimDirectiveWhitespaces="true" %>
<%@page  import = "java.util.ArrayList,
   java.util.Date,
   java.util.EnumSet,
   java.util.List,
   java.util.Set,
   org.slf4j.LoggerFactory,
   org.slf4j.Logger,
   java.util.Arrays,
   com.brightcove.proserve.mediaapi.wrapper.apiobjects.*,
   com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.*,
   com.brightcove.proserve.mediaapi.wrapper.utils.*,
   com.brightcove.proserve.mediaapi.wrapper.*,
   org.apache.sling.api.request.RequestParameter,
   java.io.*,
   java.util.UUID,
   com.brightcove.proserve.mediaapi.webservices.*" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
BrcService brcService = BrcUtils.getSlingSettingService();
String ReadToken = brcService.getReadToken();
String WriteToken = brcService.getWriteToken();
response.reset();
response.setContentType("application/json");
UUID uuid = new UUID(64L,64L);
String RandomID = new String(uuid.randomUUID().toString().replaceAll("-",""));

final List <String> write_methods = Arrays.asList( new String[] {"create_video", "update_video", "get_upload_status", "create_playlist", "update_playlist", "share_video","add_image","add_video_image"});
   
final String apiReadToken = ReadToken;
final String apiWriteToken = WriteToken;
String apiToken = apiReadToken;
String[] ids = null;
Logger logger = LoggerFactory.getLogger("Brightcove");

WriteApi wapi = new WriteApi(logger);
ReadApi rapi = new ReadApi(logger);
//try{
    
    if(slingRequest.getMethod().equals("POST")){
        String command = slingRequest.getRequestParameter("command").getString();
        logger.info(command + "   "+ String.valueOf(write_methods.indexOf(command)));
        if (write_methods.contains(command)) {
            apiToken = apiWriteToken;
            Long VideoId = null;
            File tempImageFile = null;
            InputStream fileImageStream;
            RequestParameter thumbnailFile = null;
            String thumbnailFilename = null;
            FileOutputStream outImageStream =null;
            byte [] imagebuf = null;
            
            
            switch (write_methods.indexOf(command)) {
                case 0:
                    File tempFile = null;
                    InputStream fileStream;
                    Video video = new Video();
                    RequestParameter videoFile = slingRequest.getRequestParameter("filePath");
                    //String videoFilename = "/tmp/"+RandomID+"_"+videoFile.getFileName();
                    String videoFilename = "/tmp/"+RandomID+"_"+videoFile.getFileName();
                    fileStream = videoFile.getInputStream();
                    tempFile = new File(videoFilename);
                    FileOutputStream outStream = new FileOutputStream(tempFile);
                    byte [] buf = new byte [1024];
                    for(int byLen = 0; (byLen = fileStream.read(buf, 0, 1024)) > 0;){
                        outStream.write(buf, 0, byLen);
                        //if(tempFile.length()/1000 > 2){}//maximum file size is 2gigs
                    }
                    outStream.close();
                    // Required fields
                    //video.setName(xssAPI.encodeForHTML(request.getParameter("name")));
                    video.setName(xssAPI.encodeForHTML(request.getParameter("name")));
                    video.setShortDescription(xssAPI.encodeForHTML(request.getParameter("shortDescription")));
                    
                    // Optional fields
                    //video.setAccountId(accountId);
                    //video.setEconomics(EconomicsEnum.FREE);
                    video.setItemState(ItemStateEnum.ACTIVE);
                    video.setLinkText(xssAPI.encodeForHTML(request.getParameter("linkText")));
                    video.setLinkUrl(xssAPI.encodeForHTML(request.getParameter("linkURL")));
					video.setLongDescription(xssAPI.encodeForHTML(request.getParameter("longDescription")));
                    video.setReferenceId(xssAPI.encodeForHTML(request.getParameter("referenceId")));
                    //video.setStartDate(new Date((new Date()).getTime() - 30*1000*60 )); // 30 minutes ago
                      
                    // Complex fields (all optional)
                    //Date endDate = new Date();
                    //endDate.setTime(endDate.getTime() + (30*1000*60)); // 30 minutes from now
                    //video.setEndDate(endDate);
                      
                    //video.setGeoFiltered(true);
                    //List<GeoFilterCodeEnum> geoFilteredCountries = new ArrayList<GeoFilterCodeEnum>();
                    //geoFilteredCountries.add(GeoFilterCodeEnum.lookupByName("UNITED STATES"));
                    //geoFilteredCountries.add(GeoFilterCodeEnum.CA);
                    //video.setGeoFilteredCountries(geoFilteredCountries);
                    //video.setGeoFilteredExclude(false);
                    
                    if (request.getParameter("tags") != null && request.getParameter("tags").trim().length()>0) {
                            
                        List<String> tags = Arrays.asList(xssAPI.encodeForHTML(request.getParameter("tags")).split(","));
                        video.setTags(tags);
                    
                    }
                    // Some miscellaneous fields for the Media API (not the video objects)
                    Boolean createMultipleRenditions = false;
                    Boolean preserveSourceRendition  = false;
                    Boolean h264NoProcessing         = false;
                      
                    // Image meta data
                    /*Image thumbnail  = new Image();
                    Image videoStill = new Image();
                      
                    thumbnail.setReferenceId("this is the thumbnail refid");
                    videoStill.setReferenceId("this is the video still refid");
                      
                    thumbnail.setDisplayName("this is the thumbnail");
                    videoStill.setDisplayName("this is the video still");
                      
                    thumbnail.setType(ImageTypeEnum.THUMBNAIL);
                    videoStill.setType(ImageTypeEnum.VIDEO_STILL);
                    */
                    try{
                       // Write the video
                       logger.info("Writing video to Media API");
                       Long newVideoId = wapi.CreateVideo(apiToken, video, videoFilename, TranscodeEncodeToEnum.FLV, createMultipleRenditions, preserveSourceRendition, h264NoProcessing);
                       logger.info("New video id: '" + newVideoId + "'.");
                       tempFile.delete();
                       
                       /*
                       // Delete the video
                       log.info("Deleting created video");
                       Boolean cascade        = true; // Deletes even if it is in use by playlists/players
                       Boolean deleteShares   = true; // Deletes if shared to child accounts
                       String  deleteResponse = wapi.DeleteVideo(writeToken, newVideoId, null, cascade, deleteShares);
                       log.info("Response from server for delete (no message is perfectly OK): '" + deleteResponse + "'.");
                       */
                        
                   }
                   catch(Exception e){
                       logger.error("Exception caught: '" + e + "'.");
                       
                   }
                   break;
                case 1:

                    EnumSet<VideoFieldEnum> videoFields = VideoFieldEnum.CreateEmptyEnumSet();
                    videoFields.add(VideoFieldEnum.ID);
                    videoFields.add(VideoFieldEnum.NAME);
                    videoFields.add(VideoFieldEnum.SHORTDESCRIPTION);
                    videoFields.add(VideoFieldEnum.LINKTEXT);
                    videoFields.add(VideoFieldEnum.LINKURL);
                    videoFields.add(VideoFieldEnum.ECONOMICS);
                    videoFields.add(VideoFieldEnum.REFERENCEID);
                    videoFields.add(VideoFieldEnum.TAGS);
                    
                    Set<String> customFields = CollectionUtils.CreateEmptyStringSet();
                    Long videoId = Long.parseLong(slingRequest.getRequestParameter("meta.id").getString());
                    
                    video = rapi.FindVideoById(apiReadToken,videoId,videoFields,customFields);
                    // Required fields
                    String name = new String(xssAPI.encodeForHTML(request.getParameter("meta.name")).getBytes("iso-8859-1"), "UTF-8");
                    video.setName(name);
                	String shortDescription = new String(xssAPI.encodeForHTML(request.getParameter("meta.shortDescription")).getBytes("iso-8859-1"), "UTF-8");
                    shortDescription = shortDescription.replaceAll("\n","");
                    video.setShortDescription(shortDescription);
                    logger.info("description: "+shortDescription);
                    // Optional fields
                    video.setLinkText(xssAPI.encodeForHTML(request.getParameter("meta.linkText")));
                    video.setLinkUrl(xssAPI.encodeForHTML(request.getParameter("meta.linkURL")));
                    video.setEconomics(EconomicsEnum.valueOf(request.getParameter("meta.economics")));
                    video.setReferenceId(xssAPI.encodeForHTML(request.getParameter("meta.referenceId")));
                    
                    //video.setGeoFiltered(null);
                    //video.setGeoFilteredCountries(null);
                    //video.setGeoFilteredExclude(null);
                    
                    if (request.getParameter("meta.tags") != null && request.getParameter("meta.tags").trim().length()>0) {
                            
                        List<String> tags = Arrays.asList(xssAPI.encodeForHTML(request.getParameter("meta.tags")).split(","));
                        video.setTags(tags);
                    
                    }
                    
                    try{
                       // Write the video
                       logger.info("Updating video to Media API "+shortDescription);
                       Video responseUpdate = wapi.UpdateVideo(apiToken, video);
                       logger.info("Updated video: '" + responseUpdate.getId() + "'.");
                       
                        
                   }
                   catch(Exception e){
                       logger.error("Exception caught: '" + e + "'.");
                       
                   }
                   break;
                case 3:
                       ids = slingRequest.getRequestParameter("playlist").getString().split(",");
                       
                       logger.info("Creating a Playlist");
                       Playlist playlist = new Playlist();
                       // Required fields
                       playlist.setName(xssAPI.encodeForHTML(request.getParameter("plst.name")));
                       playlist.setShortDescription(xssAPI.encodeForHTML(request.getParameter("plst.shortDescription")));
                       playlist.setPlaylistType(PlaylistTypeEnum.EXPLICIT);
                       // Optional Fields
                       if (request.getParameter("plst.referenceId") != null && request.getParameter("plst.referenceId").trim().length()>0) playlist.setReferenceId(xssAPI.encodeForHTML(request.getParameter("plst.referenceId")));
                       if (request.getParameter("plst.thumbnailURL") != null && request.getParameter("plst.thumbnailURL").trim().length()>0)playlist.setThumbnailUrl(xssAPI.encodeForHTML(request.getParameter("plst.thumbnailURL")));
                       
                       List<Long> videoIDs = new ArrayList<Long>();
                       for(String idStr : ids){
                           Long id = Long.parseLong(idStr);       
                           logger.info("Video ID: "+idStr);
                           videoIDs.add(id);
                       }
                       logger.info("Writing Playlist to Media API");
                      
                       playlist.setVideoIds(videoIDs);
                       Long newPlaylistId = wapi.CreatePlaylist(apiToken,playlist);
                       logger.info("New Playlist id: '" + newPlaylistId + "'.");
                       
                       break;
                case 6:
                	VideoId = Long.valueOf(request.getParameter("videoidthumb"));
                	thumbnailFile = slingRequest.getRequestParameter("filePath");
                    thumbnailFilename = "/tmp/"+RandomID+"_"+thumbnailFile.getFileName();
                    fileImageStream = thumbnailFile.getInputStream();
                    tempImageFile = new File(thumbnailFilename);
                    outImageStream = new FileOutputStream(tempImageFile);
                    imagebuf = new byte [1024];
                    for(int byLen = 0; (byLen = fileImageStream.read(imagebuf, 0, 1024)) > 0;){
                    	outImageStream.write(imagebuf, 0, byLen);
                        //if(tempFile.length()/1000 > 2){}//maximum file size is 2gigs
                    }
                    outImageStream.close();
                    // Required fields
                    // Image meta data
                    Image thumbnail  = new Image();
                    //Image videoStill = new Image();
                      
                    thumbnail.setReferenceId(xssAPI.encodeForHTML(request.getParameter("referenceId")));
                    //videoStill.setReferenceId(request.getParameter("referenceId"));
                      
                    thumbnail.setDisplayName(xssAPI.encodeForHTML(request.getParameter("name")));
                    //videoStill.setDisplayName(request.getParameter("name"));
                      
                    thumbnail.setType(ImageTypeEnum.THUMBNAIL);
                    //videoStill.setType(ImageTypeEnum.VIDEO_STILL);
                    
                    try{
                       // Write the image
                       Boolean resizeImage = false;
       
                       Image thumbReturn = wapi.AddImage(apiWriteToken, thumbnail, thumbnailFilename, VideoId, null, resizeImage);
				       logger.info("Thumbnail image: " + thumbReturn + ".");
				       //Image stillReturn = wapi.AddImage(apiWriteToken, videoStill, thumbnailFilename, VideoId, null, resizeImage);
				       //logger.info("Video still image: " + stillReturn + ".");
				       
				       tempImageFile.delete();
                       
                       /*
                       // Delete the video
                       log.info("Deleting created video");
                       Boolean cascade        = true; // Deletes even if it is in use by playlists/players
                       Boolean deleteShares   = true; // Deletes if shared to child accounts
                       String  deleteResponse = wapi.DeleteVideo(writeToken, newVideoId, null, cascade, deleteShares);
                       log.info("Response from server for delete (no message is perfectly OK): '" + deleteResponse + "'.");
                       */
                        
                   }
                   catch(Exception e){
                       logger.error("Exception caught: '" + e + "'.");
                       
                   }
                   break;
                case 7:
                    VideoId = Long.valueOf(request.getParameter("videoidthumb"));
                    thumbnailFile = slingRequest.getRequestParameter("filePath");
                    thumbnailFilename = "/tmp/"+RandomID+"_"+thumbnailFile.getFileName();
                    fileImageStream = thumbnailFile.getInputStream();
                    tempImageFile = new File(thumbnailFilename);
                    outImageStream = new FileOutputStream(tempImageFile);
                    imagebuf = new byte [1024];
                    for(int byLen = 0; (byLen = fileImageStream.read(imagebuf, 0, 1024)) > 0;){
                        outImageStream.write(imagebuf, 0, byLen);
                        //if(tempFile.length()/1000 > 2){}//maximum file size is 2gigs
                    }
                    outImageStream.close();
                    // Required fields
                    // Image meta data
                    Image videoStill = new Image();
                      
                    videoStill.setReferenceId(xssAPI.encodeForHTML(request.getParameter("referenceId")));
                      
                    videoStill.setDisplayName(xssAPI.encodeForHTML(request.getParameter("name")));
                      
                    videoStill.setType(ImageTypeEnum.VIDEO_STILL);
                    
                    try{
                       // Write the image
                       Boolean resizeImage = false;
       
                       Image stillReturn = wapi.AddImage(apiWriteToken, videoStill, thumbnailFilename, VideoId, null, resizeImage);
                       logger.info("Video still image: " + stillReturn + ".");
                       
                       tempImageFile.delete();
                       
                       /*
                       // Delete the video
                       log.info("Deleting created video");
                       Boolean cascade        = true; // Deletes even if it is in use by playlists/players
                       Boolean deleteShares   = true; // Deletes if shared to child accounts
                       String  deleteResponse = wapi.DeleteVideo(writeToken, newVideoId, null, cascade, deleteShares);
                       log.info("Response from server for delete (no message is perfectly OK): '" + deleteResponse + "'.");
                       */
                        
                   }
                   catch(Exception e){
                       logger.error("Exception caught: '" + e + "'.");
                       
                   }
                   break;
            }
        }
        
    } 
//} catch(Exception e){
//  out.write("{\"error\": \"Proxy Error, please check your tomcat logs.\", \"result\":null, \"id\": null}"),
   
//}

%>
