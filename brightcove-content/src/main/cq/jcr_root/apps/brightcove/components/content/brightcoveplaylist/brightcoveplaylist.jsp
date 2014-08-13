<%
/*    
    Adobe CQ5 Brightcove Connector  
    
    Copyright (C) 2011 Coresecure Inc.
        
        Authors:    Alessandro Bonfatti
                    Yan Kisen
        
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
%>
<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.components.DropTarget,com.brightcove.proserve.mediaapi.webservices.*,java.util.UUID"%>
<%
    String containerClass = DropTarget.CSS_CLASS_PREFIX + "brightcoveplaylist";

    String position= properties.get("align","center");
    String margLeft = "auto";
    String margRight = "auto";
    BrcService brcService = BrcUtils.getSlingSettingService();
    String playerId = brcService.getDefPlaylistPlayerID();
    String playerKey = brcService.getDefPlaylistPlayerKey();

    if (position.equals("left")) {
        margLeft ="0";
    } else if (position.equals("right")) {
        margRight = "0";
    }
    UUID video_uuid = new UUID(64L,64L);
    String VideoRandomID = new String(video_uuid.randomUUID().toString().replaceAll("-",""));
%>
<div style="margin-bottom: 0;margin-left: <%=margLeft%>;margin-right: <%=margRight%>;margin-top: 0;overflow-x: hidden;overflow-y: hidden;text-align: center;width: <%=properties.get("width","480")%>px;text-align:<%=properties.get("align","center")%>;">
    <script language="JavaScript" type="text/javascript" src="https://sadmin.brightcove.com/js/BrightcoveExperiences.js"></script>
    
    <script type="text/javascript" src="https://sadmin.brightcove.com/js/APIModules_all.js"> </script>

    <script type="text/javascript" src="https://files.brightcove.com/bc-mapi.js"></script>
    
    <script type="text/javascript">
    var BCLplayer;
    var BCLexperienceModule;
    var BCLvideoPlayer;
    var BCLcurrentVideo;

//listener for player error
    function onPlayerError(event) {
        /* */
    }
    
//listener for when player is loaded
    function onPlayerLoaded(id) {
      // newLog();
    //  log("EVENT: onPlayerLoaded");
      BCLplayer = brightcove.getExperience(id);
      BCLexperienceModule = BCLplayer.getModule(APIModules.EXPERIENCE);
    }

//listener for when player is ready
    function onPlayerReady(event) {
     // log("EVENT: onPlayerReady");

      // get a reference to the video player module
      BCLvideoPlayer = BCLplayer.getModule(APIModules.VIDEO_PLAYER);
      // add a listener for media change events
      BCLvideoPlayer.addEventListener(BCMediaEvent.BEGIN, onMediaBegin);
      BCLvideoPlayer.addEventListener(BCMediaEvent.COMPLETE, onMediaBegin);
      BCLvideoPlayer.addEventListener(BCMediaEvent.CHANGE, onMediaBegin);
      BCLvideoPlayer.addEventListener(BCMediaEvent.ERROR, onMediaBegin);
      BCLvideoPlayer.addEventListener(BCMediaEvent.PLAY, onMediaBegin);
      BCLvideoPlayer.addEventListener(BCMediaEvent.STOP, onMediaBegin);
    }
    
// listener for media change events
    function onMediaBegin(event) {
        var BCLcurrentVideoID;
        var BCLcurrentVideoNAME;
        BCLcurrentVideoID = BCLvideoPlayer.getCurrentVideo().id;
        BCLcurrentVideoNAME = BCLvideoPlayer.getCurrentVideo().displayName;
        switch (event.type) {
            case "mediaBegin":
                var currentVideoLength ="0";
                currentVideoLength = BCLvideoPlayer.getCurrentVideo().length;
                if (currentVideoLength != "0") currentVideoLength = currentVideoLength/1000;
                if (typeof _gaq != "undefined") _gaq.push(['_trackEvent', location.pathname, event.type+" - "+currentVideoLength, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
                break;
            case "mediaPlay":
                _gaq.push(['_trackEvent', location.pathname, event.type+" - "+event.position, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
                break;
            case "mediaStop":
                _gaq.push(['_trackEvent', location.pathname, event.type+" - "+event.position, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
                break;
            case "mediaChange":
                _gaq.push(['_trackEvent', location.pathname, event.type+" - "+event.position, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
                break;
            case "mediaComplete":
                _gaq.push(['_trackEvent', location.pathname, event.type+" - "+event.position, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
                break;
            default:
                _gaq.push(['_trackEvent', location.pathname, event.type, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
        }
    }

    </script>
    

<div style="display:none">

</div>


<object id="myExperience_<%= VideoRandomID %>" class="BrightcoveExperience">
  <param name="bgcolor" value="#FFFFFF" />
  <param name="width" value="<%=properties.get("width","480")%>" />
  <param name="height" value="<%=properties.get("height","270")%>" />
  <param name="playerID" value="<%=properties.get("playerID",playerId)%>" />
  <param name="playerKey" value="<%=properties.get("playerKey",playerKey)%>" />
  <param name="isVid" value="true" />
  <param name="isUI" value="true" />
  <param name="dynamicStreaming" value="true" />
  <param name="@playlistTabs"  value="<%=properties.get("videoPlayerPL","")%>" />
  <param name="templateLoadHandler" value="onPlayerLoaded" />
  <param name="templateReadyHandler" value="onPlayerReady" />
  <param name="templateErrorHandler" value="onPlayerError" />
  <param name="includeAPI" value="true" /> 
  <param name="wmode" value="transparent" />
  <param name="htmlFallback" value="true" />
  <param name="secureConnections" value="true" />
  <param name="secureHTMLConnections" value="true" />
</object>
   
    
<script type="text/javascript">brightcove.createExperiences();</script>



</div>