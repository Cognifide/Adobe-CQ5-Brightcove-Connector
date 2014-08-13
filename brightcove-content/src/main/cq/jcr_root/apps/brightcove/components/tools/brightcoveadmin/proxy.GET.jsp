<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page  import = "org.apache.commons.httpclient.*,
    org.apache.commons.fileupload.FileItem,
    org.apache.commons.fileupload.servlet.ServletFileUpload,
    org.apache.commons.fileupload.FileItemStream,
    org.apache.commons.fileupload.FileItemIterator,
    org.apache.commons.fileupload.util.Streams,
    java.io.InputStream,
    java.util.List,
    java.util.Arrays,
    java.io.File,
    java.io.FileOutputStream,
    java.util.Enumeration,
    org.apache.commons.httpclient.methods.PostMethod,
    org.apache.commons.httpclient.methods.GetMethod,
    org.apache.commons.httpclient.methods.multipart.Part,
    org.apache.commons.httpclient.methods.multipart.StringPart,
    org.apache.commons.httpclient.methods.multipart.FilePart,
    org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity,
    org.apache.sling.api.request.RequestParameter,
    org.slf4j.LoggerFactory,
    org.slf4j.Logger,
    java.util.Arrays,
    com.brightcove.proserve.mediaapi.wrapper.apiobjects.*,
    com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.*,
    com.brightcove.proserve.mediaapi.wrapper.utils.*,
    com.brightcove.proserve.mediaapi.wrapper.*,
    org.apache.sling.api.request.RequestParameter,
    com.brightcove.proserve.mediaapi.webservices.*" %>

<%
BrcService brcService = BrcUtils.getSlingSettingService();
String ReadToken = brcService.getReadToken();
String WriteToken = brcService.getWriteToken();

response.reset();
response.setContentType("application/json");

/****************************************************************
*  proxy.jsp -- Media API Proxy
* Takes  requests sent by client and forwards it to external API server
* to avoid cross-domain scripting issues.
*   Also forwards tokens to keep them hidden.
*****************************************************************/

/*****************************************************************
*Media API Strings go Here
*   -Fill in with your info
*       *Make sure you include the '.' at the end of the token*
******************************************************************/
final String apiReadLoc =       "http://api.brightcove.com:80/services/library";
//final String apiReadLoc =         "http://localhost:8080/services/library";
final String apiWriteLoc =      "http://api.brightcove.com:80/services/post";
//final String apiWriteLoc =        "http://localhost:8080/services/post";
final String apiReadToken =     ReadToken;
//final String apiReadToken =   "riBfgveLvpQ5OS4_D9jZxXushEmUmH9WoT4dBuEskrU.";//localhost
final String apiWriteToken =    WriteToken;
//final String apiWriteToken =  "riBfgveLvpRb-rHAkx3mBISAQXs-Q8NmphGxt0z04kE.";//localhost
 /*************************************************************
*Don't do any error checking for paramters here, just forward them along
* since the api server will check them anyway.
**************************************************************/
 /*******************************************************************************
*This list contains the names of all the write methods. It's used to check to see if a command
* sent as a GET request should be forwarded as a multipart/post request.
********************************************************************************/
 final List <String> write_methods = Arrays.asList( new String[] {"update_video", "delete_video", "get_upload_status", "create_playlist", "update_playlist", "share_video"});

 Logger logger = LoggerFactory.getLogger("Brightcove");
/**************************************************************************
* (*) bar is the all purpose utility string.  For write requests it's used to construct and
* hold the formatted JSON request. For read requests it's used to construct and 
* hold the request URL. 
*
*(*) useGet is to determine whether the request should be sent as a GET or POST request,
* since some requests that should be sent as a POST arrive as a GET.
***************************************************************************/
String bar = null;
boolean useGet = false;
String[] ids = null;
try{
    
    String command = slingRequest.getRequestParameter("command").getString();
    logger.info("Command: '" + command +"' ");
    if (write_methods.contains(command) && request.getMethod().equals("GET")){
        WriteApi wapi = new WriteApi(logger);
        switch (write_methods.indexOf(command)) {
           case 1:
               useGet = false;
               ids = slingRequest.getRequestParameter("ids").getString().split(",");
               logger.info("Deleting videos");
               Boolean cascade        = true; // Deletes even if it is in use by playlists/players
               Boolean deleteShares   = true; // Deletes if shared to child accounts
               for(String idStr : ids){
                   Long id = Long.parseLong(idStr);       
                   String  deleteResponse = wapi.DeleteVideo(apiWriteToken, id, null, cascade, deleteShares).toString();
                   logger.info(idStr+" Response from server for delete (no message is perfectly OK): '" + deleteResponse + "'.");
                   
               }
               
               break;
           
           default:
               useGet = false;
                String temp;
                //The method can't be part of the params section, so we write that out first, then the token and then loop through the rest of the parameters.
                bar = "{\"method\": \"" + command + "\", \"params\": {\"token\": \"" + apiWriteToken +"\"";
                for(Enumeration e = request.getParameterNames(); e.hasMoreElements();){
                    temp = (String) e.nextElement();
                    //don't want to include command twice
                    if(!temp.equals("command")){
                       bar += ",\""+temp+"\": \"" +request.getParameter(temp)+"\"";
                    }
                }
                bar += "}}";

                //out.print(bar);
                
                Part[] parts = {new StringPart("data", bar)};
                HttpClient client = new HttpClient();
                PostMethod postreq = new PostMethod(apiWriteLoc);
                postreq.setRequestEntity( new MultipartRequestEntity(parts, postreq.getParams()) );
                client.executeMethod(postreq);
                if(postreq.getStatusCode() == HttpStatus.SC_OK){
                    out.print(postreq.getResponseBodyAsString());
                    postreq.releaseConnection();
                }else{
                    out.print( "Post Failed, error: " + postreq.getStatusLine());
                    postreq.releaseConnection();
                }
             
        }
        
    
    /******************************************************************************
    * The last case is an incoming GET request that should be forwarded as a GET request.
    * We set useGet to true and concatenate the read token at the end of the parameter string.
    * The JSTL  below sends the entire request and sends the response from the API server
    * back to the client.
    *******************************************************************************/
    } else {
        useGet = true;
        bar = apiReadLoc + '?' + request.getQueryString() + "&token=" + apiReadToken;
        /************************************************************************************
        *If you don't want to support JSTL, the block below duplicates the functionality of the JSTL block 
        *at the bottom of this document. It might be faster to use JSTL but this hasn't been verified.
        *************************************************************************************/
        /*HttpClient client = new HttpClient();
        HttpMethod getreq = new GetMethod(bar);
        client.executeMethod(getreq);
        if(getreq.getStatusCode() == HttpStatus.SC_OK){
        out.print(getreq.getResponseBodyAsString());
        getreq.releaseConnection();
        }else{
        out.print( "Get Failed, error: " + getreq.getStatusLine());
        getreq.releaseConnection();
        }*/
    }
} catch(Exception e){
    out.write("{\"error\": \"Proxy Error, please check your tomcat logs.\", \"result\":null, \"id\": null}");
}

%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="<%=useGet%>" >
<c:import url="<%=bar%>" />
</c:if>