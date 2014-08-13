<%
/*
    Adobe CQ5 Brightcove Connector

    Copyright (C) 2011 Coresecure Inc.

        Authors:    Alessandro Bonfatti
                    Yan Kisen
                    Chris Pilsworth

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
%><%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.components.DropTarget,java.util.UUID,
				 com.brightcove.proserve.mediaapi.webservices.*,
				 com.day.cq.wcm.api.WCMMode"%><%
    String containerClass = DropTarget.CSS_CLASS_PREFIX + "brightcovevideo";

    String position= properties.get("align","center");
    String margLeft = "auto";
    String margRight = "auto";
    BrcService brcService = BrcUtils.getSlingSettingService();
    String playerId = brcService.getDefVideoPlayerID();
    String playerKey = brcService.getDefVideoPlayerKey();

    if (position.equals("left")) {
        margLeft ="0";
    } else if (position.equals("right")) {
        margRight = "0";
    }
    UUID video_uuid = new UUID(64L,64L);
    String VideoRandomID = new String(video_uuid.randomUUID().toString().replaceAll("-",""));
%>

<cq:includeClientLib js="brc.BrightcoveExperiences-custom"/>

<script language="JavaScript" type="text/javascript" src="https://sadmin.brightcove.com/js/BrightcoveExperiences.js"></script>

<% if (properties.get("videoPlayer", "").isEmpty()) {%>
<cq:include script="/apps/brightcove/components/content/brightcovevideo/empty.jsp"/>
<% } else if (WCMMode.fromRequest(slingRequest) == WCMMode.EDIT) {
                %><h1>Right click here to edit</h1><%
   }%>
<div id="<%= VideoRandomID %>" style="style="margin-bottom: 0;margin-left: <%=margLeft%>;margin-right: <%=margRight%>;margin-top: 0;overflow-x: hidden;overflow-y: hidden;text-align: center;text-align:<%=properties.get("align","center")%>">
    <object id="myExperience_<%= VideoRandomID %>" class="BrightcoveExperience">
      <param name="bgcolor" value="#FFFFFF" />
      <param name="width" value='<%=properties.get("width","480")%>' />
      <param name="height" value='<%=properties.get("height","270")%>' />
      <param name="playerID" value="<%=playerId%>" />
      <param name="playerKey" value="<%=playerKey%>" />
      <param name="isVid" value="true" />
      <param name="isUI" value="true" />
      <param name="dynamicStreaming" value="true" />
      <param name="secureConnections" value="true" />
      <param name="secureHTMLConnections" value="true" />

      <param name="@videoPlayer" value='<%=properties.get("videoPlayer",String.class)%>' />

    </object>
</div>

<!--
This script tag will cause the Brightcove Players defined above it to be created as soon
as the line is read by the browser. If you wish to have the player instantiated only after
the rest of the HTML is processed and the page load is complete, remove the line.
-->
<script type="text/javascript">brightcove.createExperiences();</script>