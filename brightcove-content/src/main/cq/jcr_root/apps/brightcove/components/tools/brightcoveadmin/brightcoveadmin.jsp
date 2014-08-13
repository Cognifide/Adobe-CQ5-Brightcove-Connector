<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Tree Activation

  Implements the tree activation component.

--%><%@ page contentType="text/html"
             pageEncoding="utf-8"
             import="com.day.cq.wcm.foundation.Image,
    com.day.cq.wcm.api.components.DropTarget,
    com.day.cq.wcm.api.components.EditConfig,
    com.day.cq.wcm.commons.WCMUtils,
    com.day.cq.replication.Replicator,
    com.day.cq.replication.Agent,
    com.day.cq.replication.AgentConfig,
    com.day.cq.widget.HtmlLibraryManager,
    com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.api.components.Toolbar,
    com.day.cq.replication.ReplicationQueue,
    com.day.cq.replication.AgentManager, java.util.Iterator,
    com.brightcove.proserve.mediaapi.webservices.*" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
    AgentManager agentMgr = sling.getService(AgentManager.class);
BrcService brcService = BrcUtils.getSlingSettingService();
String previewPlayerLoc = brcService.getPreviewPlayerLoc();
String previewPlayerListLoc = brcService.getPreviewPlayerListLoc();

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; utf-8" />
    <cq:includeClientLib categories="cq.wcm.edit" />
    
    <cq:includeClientLib css="brc.bootstrap"/>
    <cq:includeClientLib css="brc.brightcove-api"/>
    
    <cq:includeClientLib js="brc.bootstrap"/>
<cq:includeClientLib js="brc.brightcove-api"/>
    
    <title>VideoManager - Media API Sample Application</title>
    <script>
    //This should be the direct URL to a preview player,  corresponding to the account of the tokens
    var previewPlayerLoc = "<%=previewPlayerLoc%>";
    var previewPlayerListLoc = "<%=previewPlayerListLoc%>";
    </script>
</head>

<body>
<div id="CQ"></div>
<div id="brightcove">
    <div class="navbar">
      <div class="navbar-inner">
        <div class="container">
          <ul class="nav">
              <li class="active">
                <a id="allVideos" onclick='$(this).parent("li").parent("ul").children("li").attr("class","");$(this).parent("li").attr("class","active");Load(getAllVideosURL());'>All Videos</a>
              </li>
              <li><a id="allPlaylists" onclick='$(this).parent("li").parent("ul").children("li").attr("class","");$(this).parent("li").attr("class","active");Load(getAllPlaylistsURL())'>All Playlists</a></li>
            </ul>
            <div class="pull-right" ><img src="/etc/designs/cs/brightcove/images/logo_brightcove.png" alt="Brightcove Console"></div>
            
        </div>
      </div>
    </div>



<div id="divConsole">
<table width="100%" class="tblConsole" cellspacing="0" callpadding="0" border="0">
<tr>

<!-- Start center column -->
<td width="75%" valign="top" >
<table id="listTable" width="100%" cellspacing="0" callpadding="0">
<tr class="trCenterHeader"><td id="tdOne">
    <div id="headTitle" style="font-weight:bold">All Videos</div><p>
    <div id="divVideoCount" style="float:left"></div>
    <div id="searchDiv" style="float:right;padding:5px">
        <input id="search" type="text" value="Search Video" onClick="this.value=''" >
        <!--Store the search query in searchBut.value so we can use it as the title of the page once the results are returned.  See searchVideoCallBack -->
        <button id="searchBut" onClick="prepareSearchVal();Load(searchVideoURL())">Search</button>
    </div>
</td></tr>
    <tr><td id="tdTwo">
    <div name="butDiv" class="butDiv">
        <span name="buttonRow"><!--The buttons in buttonRow are hidden in playlist view -->
            <button id="delButton" class="delButton" onClick="openBox('delConfPop')">Delete Checked</button> 
            <button id="uplButton" class="uplButton" onClick="openBox('uploadDiv')">Upload Video</button> 
            <button id="newplstButton" onClick="createPlaylistBox()">Create Playlist</button> 
        </span>
        <button class="btn" name="delFromPlstButton" onClick="openBox('modPlstPop')"> Remove From Playlist</button>
    </div>
    <div name="pageDiv" class="pageDiv">
        Page Number: <select name="selPageN" onchange="changePage(this.selectedIndex)">
    </div>
    </td>
    </tr>
    <tr><td>
    
        <!-- Start Main list -->
        <!-- buildMainVideoList, buildPlaylistList and showPlaylist populate this section, -->
        <table id="tblMainList" cellpadding="3" cellspacing="0">
        <thead>
            <tr id="trHeader"> 
                <th id="checkCol" class="tdMainTableHead" style="border-right:0px;color:#e5e5e5;font-size:1px">
                    <input type="checkbox" onclick="toggleSelect(this)" id="checkToggle"/>?
                </th>
                <th id="nameCol" class="tdMainTableHead" style="border-left:0px">
                    Video Name
                </th>
                <th id="lastUpdated" class="tdMainTableHead">
                    Last Updated
                </th>
                <th class="tdMainTableHead">
                    ID</th>
                <th class="tdMainTableHead">
                    Reference Id
                </th>
            </tr>
        </thead>
        <tbody id="tbData">
        </tbody>
        </table>
        </td></tr>
    <tr><td>
    <div name="butDiv" class="butDiv">
        <span name="buttonRow"><!--The buttons in buttonRow are hidden in playlist view -->
            <button id="delButton" class="delButton" onClick="openBox('delConfPop')">Delete Checked</button> 
            <button id="uplButton" class="uplButton" onClick="openBox('uploadDiv')">Upload Video</button>
            <button id="newplstButton" onClick="createPlaylistBox()">Create Playlist</button>
        </span>
        <button class="btn" name="delFromPlstButton" onClick="openBox('modPlstPop')"> Remove From Playlist</button>
    </div>
    <div name="pageDiv" class="pageDiv">
        Page Number: <select name="selPageN" onchange="changePage(this.selectedIndex)">
    </div>
    </td></tr>
    </table>
</td>

<!-- start right metadata column, fields filled in by showMetaData() -->
<!--Element Id's have the meta. prefix followed by the actual name of the Video object field.  This allows the fields to be easily compared.  See  metaSubmit()  -->
<td id="tdMeta" valign="top" class="tdMetadata">
<br/>
<div id="divMeta.name" style="font-weight:bold"></div><br/>
Last Updated: <div id="divMeta.lastModifiedDate"></div>
<hr/>
<div id="divMeta.previewDiv" >
<ul class="thumbnails">
        <li class="span3">
          <div class="thumbnail">
            <img id="divMeta.videoStillURL" alt=" No Image " style="width:260px" />
             <a onClick="changeVideoImage(document.getElementById('divMeta.id').innerHTML)" href="#" class="btn">Change Video Still Image</a>
          </div>
        </li>
        <li class="span2">
          <div class="thumbnail">
            <img id="divMeta.thumbnailURL" alt=" No Thumbnail " style="width:160px" />
            <a onClick="changeImage(document.getElementById('divMeta.id').innerHTML)" href="#" class="btn">Change Thumbnail</a>
          </div>
        </li>
        
      </ul>
<p><a href="#"  onClick="doPreview(document.getElementById('divMeta.id').innerHTML)" class="btn btn-primary">Video Preview</a></p>
</div><br/>
Duration: <div id="divMeta.length"></div><br/>
Video ID: <div id="divMeta.id"></div><br/>
<hr>
Short Description: <div id="divMeta.shortDescription"></div><br/>
Tags: <div id="divMeta.tags"></div><br/>
Related Link: <br/><a id="divMeta.linkURL" ></a><br/><br/>
<div style="display:none" id="divMeta.linkText"></div>
Economics: <div id="divMeta.economics"></div><br/>
Date Published: <div id="divMeta.publishedDate"></div><br/>
Reference ID: <div id="divMeta.referenceId"></div><br/>
<center><button id="bmetaEdit" onClick="metaEdit()" style="display:block">Edit</button></center>
</td>
</tr>
</table>
<div id="loading" class="alert">
  <strong>Loading!</strong> Please wait while your page loads.
</div>
<!-- Dimmed "Screen" over content behind overlays -->
<div id="screen" style="display:none"></div>

<!-- Edit Metadata Overlay -->
<div id="metaEditPop" style="display:none" class="overlay tbInput">
<form id="metaEditForm" method="POST" enctype="multipart/form-data" target="postFrame">
<br/><center><span class="title">Edit Metadata</span><br/><br/>
<table>
<!--the div below creates a horizontal line.  it is repeated throughout this page. -->
<div class="hLine"></div>
<tr><td>Title:</td><td><input type="text" name="meta.name" id="meta.name"/></td></tr>
<tr><td>Last Updated:</td><td id="meta.lastModifiedDate"></td></tr>
<tr><td>Preview</td><td style="cursor:pointer" id="meta.preview" onClick="doPreview(this.value)">Click Here to Preview</td></tr>
<tr><td>Duration:</td><td id="meta.length"></td></tr>
<tr><td>Video ID:</td><td name="tdmeta.id" id="tdmeta.id"></td></tr>
<tr><td>Short Description:</td><td><textarea type="text" name="meta.shortDescription" id="meta.shortDescription" maxLength="256"></textarea></td></tr>
<tr><td>Tags:</td><td><input type="text" name="meta.tags" id="meta.tags"/></td></tr>
<tr><td>Related Link URL:</td><td><input type="text" name="meta.linkURL" id="meta.linkURL"/></td></tr>
<tr><td>Related Link Text:</td><td><input type="text" name="meta.linkText" id="meta.linkText"/></td></tr>
<tr><td>Economics:</td><td><select name="meta.economics" id="meta.economics" style="width:100%">
<option value="AD_SUPPORTED">Ad Enabled</option><option value="FREE">No Ads</option></select></td></tr>
<tr><td>Date Published:</td><td id="meta.publishedDate"></td></tr>
<tr><td>Reference ID:</td><td><input type="text" name="meta.referenceId" id="meta.referenceId"/></td></tr>
</table>
<div class="hLine"></div><br/>
<center> 
    <button type="button" id="bmetaSubmit" onClick="metaSubmit()" >Submit</button> 
    <button type="button" id="bmetaCancel" onClick="closeBox('metaEditPop', this.form)">Cancel</button>
</center>
<!-- This section holds data processed by proxy.jsp  -->
<input type="hidden" name="meta.id" id="meta.id">
<input type="hidden" name="command" value="update_video">
</form>
</div>


<!--Upload Form-->
<div id="uploadDiv" style="display:none" class="overlay tbInput">
<form id="uploadForm" method="POST" enctype="multipart/form-data" target="postFrame">
<br/><center><span class="title">Upload A New Video:</span><br/><br/>
<span class="subTitle">Title and Short Description are Required</span></center>
<div class="hLine"></div>
<table>
<tr class="requiredFields"><td>Title:</td><td  style="width:100%"><input type="text" name="name" id="name"/></td></tr>
<tr class="requiredFields"><td>Short Description:</td><td><input type="text" name="shortDescription" id="shortDescription" maxLength="256"/></td></tr>
<tr><td>Link to Related Item:</td><td><input type="text" name="linkURL" id="linkURL"/></td></tr>
<tr><td>Text for Related Item:</td><td><input type="text" name="linkText" id="linkText"/></td></tr>
<tr><td>Tags:</td><td><input type="text" name="tags" id="tags"/></td></tr>
<tr><td>Reference ID:</td><td><input type="text" name="referenceId"/></td></tr>
<tr><td>Long Description:</td><td style="width:100%"><textarea name="longDescription"  id="longDescription" style="width:100%"></textarea></td></tr>
<tr class="requiredFields"><td>File:</td><td><input type="file" name="filePath" id="filePath" style="width:100%" />
<input type="hidden" name="video"/>
<input type="hidden" name="command"  id="command" value="create_video"/></td></tr>
</table>
<div class="hLine"></div><br/>
<center>
    <div class="subTitle">
        Delays up to 20 minutes may occur before changes are reflected
    </div><br/>
    <button type="button" id="startUploadButton" onClick="startUpload()">Start Upload</button>   
    <button type="button" id="cancelUploadButton" onClick="closeBox('uploadDiv',this.form)">Cancel</button>
</center>
</form>
</div>
<!--Upload Image-->
<div id="uploadImageDiv" style="display:none" class="overlay tbInput">
<form id="uploadImageForm" method="POST" enctype="multipart/form-data" target="postFrame">
<br/><center><span class="title">Upload A New Image:</span><br/><br/>
<span class="subTitle">Title and Reference ID are Required</span></center>
<div class="hLine"></div>
<table>
<tr class="requiredFields"><td>Title:</td><td  style="width:100%"><input type="text" name="name" id="name"/></td></tr>
<tr><td>Reference ID:</td><td><input type="text" name="referenceId"/></td></tr>
<tr class="requiredFields"><td>File:</td><td><input type="file" name="filePath" id="filePath" style="width:100%" />
<input type="hidden" name="image"/>
<input type="hidden" id="videoidthumb" name="videoidthumb"/>
<input type="hidden" name="command"  id="command" value="add_image"/></td></tr>
</table>
<div class="hLine"></div><br/>
<center>
    <div class="subTitle">
        Delays up to 20 minutes may occur before changes are reflected
    </div><br/>
    <button type="button" id="startUploadButton" onClick="startImageUpload()">Start Upload</button>   
    <button type="button" id="cancelUploadButton" onClick="closeBox('uploadImageDiv',this.form)">Cancel</button>
</center>
</form>
</div>

<!--Upload Video Image-->
<div id="uploadVideoImageDiv" style="display:none" class="overlay tbInput">
<form id="uploadVideoImageForm" method="POST" enctype="multipart/form-data" target="postFrame">
<br/><center><span class="title">Upload A New Image:</span><br/><br/>
<span class="subTitle">Title and Reference ID are Required</span></center>
<div class="hLine"></div>
<table>
<tr class="requiredFields"><td>Title:</td><td  style="width:100%"><input type="text" name="name" id="name"/></td></tr>
<tr><td>Reference ID:</td><td><input type="text" name="referenceId"/></td></tr>
<tr class="requiredFields"><td>File:</td><td><input type="file" name="filePath" id="filePath" style="width:100%" />
<input type="hidden" name="image"/>
<input type="hidden" id="videoidthumb" name="videoidthumb"/>
<input type="hidden" name="command"  id="command" value="add_video_image"/></td></tr>
</table>
<div class="hLine"></div><br/>
<center>
    <div class="subTitle">
        Delays up to 20 minutes may occur before changes are reflected
    </div><br/>
    <button type="button" id="startUploadButton" onClick="startVideoImageUpload()">Start Upload</button>   
    <button type="button" id="cancelUploadButton" onClick="closeBox('uploadVideoImageDiv',this.form)">Cancel</button>
</center>
</form>
</div>
<!--Create Playlist -->
<div id="createPlaylistDiv" style="display:none" class="overlay tbInput">
    <form id="createPlaylistForm" method="POST" enctype="multipart/form-data" target="postFrame">
        <br/><center><span class="title">Create Playlist:</span><br/><br/>
        <span class="subTitle">Title is Required</span></center>
        <table>
        <div class="hLine"></div>
        <tr class="requiredFields"><td>Title:</td><td  style="width:100%"><input type="text" name="plst.name" id="plst.name"/></tr></td>
        <tr><td>Short Description:</td><td><input type="text" name="plst.shortDescription" id="plst.shortDescription"/></td></tr>
        <tr><td>Reference ID:</td><td><input type="text" name="plst.referenceId" id="plst.referenceId"/></td></tr>
        <tr><td>Thumbnail URL:</td><td><input type="text" name="plst.thumbnailURL" id="plst.thumbnailURL"/></td></tr>
        </table>
        <fieldset><legend>Videos</legend>
            <table id="createPlstVideoTable">
                <tr style="background-color:#e5e5e5">
                    <td class="tdMainTableHead" style="width:100%">Video Name</td>
                    <td class="tdMainTableHead" style="width:40%">ID</td>
                </tr>
            </table> 
            <input type="hidden" name="command" value="create_playlist"/>
            <input type="hidden" name="playlist" id="playlist"/>
        </fieldset>
        <div class="hLine"></div>
        <br><center>
        <button type="button" id="createPlstSubmit" onClick="createPlaylistSubmit()">Create Playlist</button>
        <button type="button" id="createPlstCancel" onClick="$('#createPlstVideoTable').empty();closeBox('createPlaylistDiv',this.form)">Cancel</button>
        </center>
    </form>
</div>

<!--Get Upload status-->
<div id='getUplStatus' style="display:none" class="overlay">
<center>Get Upload Status By VideoId<br><br>
VideoId: <input type="text" id="uplStatusId">
<br><br>
<button onClick="Load(getUploadStatusById())">Submit</button> 
<button type="button" onClick="closeBox('getUplStatus')">Cancel</button>
</div>

<!--Upload Status Bar-->
<div id="uploadStatus" style="display:none" class="overlay">
<center>Uploading...</center>
<div style="background-color:black;overflow:hidden;width:100%;position:relative">
    <div id="progress"></div>
</div>
</div>

<!-- Delete confirmation dialog -->
<div id="delConfPop" style="display:none" class="delConfPop overlay">
<center><strong style="font-size:14px;">DELETE</strong><br/>Are you sure you want to proceed?
<br><span class="subTitle">Delays up to 20 minutes may occur before changes are reflected</span>
<br/><br/>
<button name="deleteConfBut" onClick="delConfYes()" class="btn">Yes</button>
<button type="button" class="btn" name="deleteConfBut" onClick="closeBox('delConfPop')">No</button>
</center>
</div>

<!-- Modify Playlist confirmation dialog -->
<div id="modPlstPop" style="display:none" class="delConfPop overlay">
<center>Remove Selected From Playlist?<br/>
<span class="subTitle">Are you sure you want to proceed?</span>
<br/><br/>
<form id="modPlstForm" method="POST" enctype="multipart/form-data" target="postFrame">
<button onClick="modPlstSubmit()">Yes</button>
<button type="button" class="btn" onClick="closeBox('modPlstPop')">No</button></center>
<input type="hidden" name="command" value="update_playlist"/>
<input type="hidden" name="playlist" id="playlist"/>
</form>
</div>

<!--Player Preview  -->
<!-- This window is populated by doPreview -->
<div id="playerDiv" style="display:none" class="overlay">
<div id="playerdrag" class="hLine"></div>
<div id="playerTitle" style="text-transform:uppercase; font-weight:bold; font-size:14px;"></div>
<div class="hLine"></div>
</div>

<!--Share Video -->
<!--this functionality has been disabled (commented out), and has not been fully maintained -->
<div id="shareVideoDiv" style="display:none" class="overlay tbInput">
<form id="shareVideoForm" method="POST" enctype="multipart/form-data" target="postFrame" >
<br/><center><span class="title">Share Video</span><br/><br/>
<span class="subTitle">Sharee Account Id's Should be Comma Separated </span></center>
<table>
<div class="hLine"></div>
<tr class="requiredFields"><td>Sharee Account Id's:</td><td  style="width:100%"><input type="text" name="sharees" id="sharees"  style="width:100%"/></tr></td>
</table>
<fieldset><legend>Video</legend>
<table id="shareVideoTable">
<tr style="background-color:#e5e5e5"><td class="tdMainTableHead" style="width:100%">Video Name</td><td class="tdMainTableHead" style="width:40%">ID</td></tr>
</table> 
</fieldset>
<div class="hLine"></div>
<br><center>
<button id="shareSubmit" onClick="shareVidSubmit()">Share Video</button>
<button type="button" id="shareCancel" onClick="$('shareVideoTable').empty();closeBox('shareVideoDiv',this.form)">Cancel</button>
</center>
<input type="hidden" name="command" value="share_video"/>
<input type="hidden" name="data"/>
</form>
</div>
<iframe id="postFrame" name="postFrame" style="width:100%;border:none;display:none;"></iframe>
</div></div>

</body>
</html>