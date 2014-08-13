var BCLplayer;
var BCLexperienceModule;
var BCLvideoPlayer;
var BCLcurrentVideo;
var BCLcuePointsModule;
var BCLvideoLength;
var playerType;
var videoLength = 0;
var myExpId;
var divId;

//Map of Chapters
var cuePointData;
var  timeRanges;
var chapterlistTemplate = "{{#cuePointData}}<div class=\"chapterlist-item\" data-time=\"{{time}}\"><span class=\"title\">{{name}}</span><span class=\"cue\">{{metadata}}</span><div class=\"clear\"></div></div>{{/cuePointData}}";
var chapterTickTemplate = "{{#cuePointData}}<span class=\"chapterTickMark\" style=\"left:{{tickPosition}}px;\" data-time=\"{{time}}\"><img height=\"10\" width=\"3\" src=\"/etc/designs/cs/brightcove/images/1x1.png\" alt=\"{{name}}\" /></span>{{/cuePointData}}";
var template;
var data;
var results;
var cuePointDataObj = {};
/**** function to process the cue points ****/
function getCuePoints(videoID) {
	cuePointData = BCLcuePointsModule.getCuePoints(videoID);
    cuePointData.sort( function(a,b) {
      return a.time - b.time;
    });
    // remove the preroll and postroll cue points
    cuePointData.splice(0,1);
    cuePointData.splice((cuePointData.length -1),1);
    /*
    * add tick marks
    * these are based on the actual size of the player
    * and length of the timeline
    * and we don't need one for the
    */
    if (BCLvideoPlayer.getCurrentVideo().length >0) videoLength=BCLvideoPlayer.getCurrentVideo().length/1000;
    for (var i = 0; i < cuePointData.length; i++) {
        cuePointData[i].tickPosition = (cuePointData[i].time / videoLength) * timelineLength;
    };
    cuePointDataObj.cuePointData = cuePointData;
    // build an array of time ranges
    timeRanges = [];
    for (var i = 0; i < cuePointData.length; i++) {
        var obj = {};
        var j = i + 1;
        obj.start = cuePointData[i].time;
        obj.chapter = cuePointData[i].name;
        if (i !== (cuePointData.length - 1)) {
          obj.end = cuePointData[j].time;
        } else {
          obj.end = videoLength;
        }
        timeRanges.push(obj);
    }
    // start the video - won't work on mobile devices!
};
/**** function to build the chapter list ****/
function buildchapterlist() {
  template = Handlebars.compile(chapterlistTemplate);
  data = cuePointDataObj;
  results = template(data);
  $("#BCL_chapterlist").html(results);
  // add event listener for chapterlist items
  $(".chapterlist-item").on("click", function (evt) {
    $this = $(this);
    // highlight selected item
    highlightItem($this);
    // play the video
    playChapter($this.attr("data-time"));
  });
};
/**** function to add chapter tick marks ****/
function addTickMarks() {
  template = Handlebars.compile(chapterTickTemplate);
  data = cuePointDataObj;
  results = template(data);
  $("#BCL_chapterTicks").html(results);
  // hide the tick marks till there's a hover over the player
  hideTicks();
  // add event listener for chapter ticks
  $(".chapterTickMark").on("click", function (evt) {
    var $this = $(this);
    // play the video
    playChapter($this.attr("data-time"));
  });
  // add listen for player hover events to show/hide tick marks
  $("#chapterlist_player").on("mouseover", showTicks);
  $("#chapterlist_player").on("mouseout", hideTicks );
};
/* function to hide tick marks*/
function hideTicks() {
  //bclslog("mouseout");
  $(".chapterTickMark").css("background-color","transparent");
};
/* function to show tick marks */
function showTicks() {
  $(".chapterTickMark").css("background-color", "#F3951D");
  // if flash, wait 5.5 sec, then hide again to match media controls behavior
  if (playerType == "flash") {
    t = setTimeout(hideTicks, 5500);
  }
};
/**** function to highlight current chapter ****/
function highlightItem($item) {
  $item.siblings().removeClass("chapterlist-item-selected");
  $item.addClass("chapterlist-item-selected");
};
/**** function to play a chapter ****/
function playChapter(time) {
  // if the video is not playing, start it and function calls itself again
  
    if (BCLvideoPlayer.isPlaying() == true) {
      BCLvideoPlayer.seek(time);
    }
    else {
      // function recalls itself till result is true
      BCLvideoPlayer.play();
      playChapter(time);
    }
}

//listener for player error
function onPlayerError(event) {
    /* */
}

//listener for when player is loaded
function onPlayerLoaded(id) {
  // newLog();
//  log("EVENT: onPlayerLoaded");
  BCLplayer = brightcove.getExperience(id);
  if (typeof BCLplayer!='undefined') {
	  BCLexperienceModule = BCLplayer.getModule(APIModules.EXPERIENCE);
	  playerType = BCLplayer.type;
      if (playerType == "html") {
        timelineLength = 290;
        $("#BCL_chapterTicks").addClass("htmlPlayer");
      } else {
        timelineLength = 210;
        $("#BCL_chapterTicks").addClass("flashPlayer");
      };
  }

}

//listener for when player is ready
function onPlayerReady(event) {
 // log("EVENT: onPlayerReady");
  if (typeof BCLplayer=='undefined') onPlayerLoaded(myExpId);
  // get a reference to the video player module
  BCLvideoPlayer = BCLplayer.getModule(APIModules.VIDEO_PLAYER);
  BCLcuePointsModule = BCLplayer.getModule(APIModules.CUE_POINTS);
  
  //fetch the video data and process the cuepoint
  getCuePoints(BCLvideoPlayer.getCurrentVideo().id);
  buildchapterlist();
  addTickMarks();
  // add a listener for media change events
  BCLvideoPlayer.addEventListener(BCMediaEvent.BEGIN, onMediaBegin);
  BCLvideoPlayer.addEventListener(BCMediaEvent.COMPLETE, onMediaBegin);
  BCLvideoPlayer.addEventListener(BCMediaEvent.CHANGE, onMediaBegin);
  BCLvideoPlayer.addEventListener(BCMediaEvent.ERROR, onMediaBegin);
  BCLvideoPlayer.addEventListener(BCMediaEvent.PLAY, onMediaBegin);
  BCLvideoPlayer.addEventListener(BCMediaEvent.STOP, onMediaBegin);
  BCLvideoPlayer.play();

}  
//listener for media change events
function onMediaBegin(event) {
    var BCLcurrentVideoID;
    var BCLcurrentVideoNAME;
    BCLcurrentVideoID = BCLvideoPlayer.getCurrentVideo().id;
    BCLcurrentVideoNAME = BCLvideoPlayer.getCurrentVideo().displayName;
    switch (event.type) {
        case "mediaBegin":
        	// populate the template with data
	      	buildchapterlist();
	      	// add tick marks
	      	addTickMarks();
            var currentVideoLength ="0";
            currentVideoLength = BCLvideoPlayer.getCurrentVideo().length;
            if (currentVideoLength != "0") currentVideoLength = currentVideoLength/1000;
            if (typeof _gaq != "undefined") _gaq.push(['_trackEvent', location.pathname, event.type+" - "+currentVideoLength, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
            break;
        case "mediaPlay":
            if (typeof _gaq != "undefined") _gaq.push(['_trackEvent', location.pathname, event.type+" - "+event.position, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
            break;
        case "mediaStop":
        	if (typeof _gaq != "undefined") _gaq.push(['_trackEvent', location.pathname, event.type+" - "+event.position, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
            break;
        case "mediaChange":
        	if (typeof _gaq != "undefined") _gaq.push(['_trackEvent', location.pathname, event.type+" - "+event.position, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
            break;
        case "mediaComplete":
        	if (typeof _gaq != "undefined") _gaq.push(['_trackEvent', location.pathname, event.type+" - "+event.position, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
            //$("#BCL_chapterlist").html("");
            //$("#BCL_chapterTicks").html("");
            break;
        default:
        	if (typeof _gaq != "undefined") _gaq.push(['_trackEvent', location.pathname, event.type, BCLcurrentVideoNAME+" - "+BCLcurrentVideoID]);
    }
      
}

if (customBC == undefined) {
    var customBC = {};
    customBC.createElement = function (el) {
        if (document.createElementNS) {
            return document.createElementNS('http://www.w3.org/1999/xhtml', el);
        } else {
            return document.createElement(el);
        }
    };
    customBC.createVideo = function (width,height,playerID,playerKey,videoPlayer,VideoRandomID) {
    	var innerhtml = '<object id="myExperience_'+VideoRandomID+'" class="BrightcoveExperience">';
		innerhtml += '<param name="bgcolor" value="#FFFFFF" />';
		innerhtml += '<param name="width" value="'+width+'" />';
		innerhtml += '<param name="height" value="'+height+'" />';
		innerhtml += '<param name="playerID" value="'+playerID+'" />';
		innerhtml += '<param name="playerKey" value="'+playerKey+'" />';
		innerhtml += '<param name="isVid" value="true" />';
		innerhtml += '<param name="isUI" value="true" />';
		innerhtml += '<param name="dynamicStreaming" value="true" />';
		innerhtml += '<param name="@videoPlayer" value="'+videoPlayer+'" />';
		if ( window.location.protocol == 'https:') innerhtml += '<param name="secureConnections" value="true" /> ';
		innerhtml += '<param name="templateLoadHandler" value="onPlayerLoaded" />';
		innerhtml += '<param name="templateReadyHandler" value="onPlayerReady" />';
		innerhtml += '<param name="templateErrorHandler" value="onPlayerError" />';
		innerhtml += '<param name="includeAPI" value="true" /> ';
		innerhtml += '<param name="wmode" value="transparent" />';
		//innerhtml += '<param name="htmlFallback" value="true" />';
		innerhtml += '</object>';
		var objID = document.getElementById(VideoRandomID);
		objID.innerHTML = innerhtml;
		
		var apiInclude = customBC.createElement('script');
		apiInclude.type = "text/javascript";
		apiInclude.src = "https://sadmin.brightcove.com/js/BrightcoveExperiences.js";
		objID.parentNode.appendChild(apiInclude);
		
		apiInclude = customBC.createElement('script');
		apiInclude.type = "text/javascript";
		apiInclude.src = "https://sadmin.brightcove.com/js/APIModules_all.js";
		objID.parentNode.appendChild(apiInclude);
		
		apiInclude = customBC.createElement('script');
		apiInclude.type = "text/javascript";
		apiInclude.src = "https://files.brightcove.com/bc-mapi.js";
		objID.parentNode.appendChild(apiInclude);
		
		apiInclude = customBC.createElement('script');
		apiInclude.type = "text/javascript";
		apiInclude.text  = "window.onload = function() {brightcove.createExperiences();};";
		objID.parentNode.appendChild(apiInclude);
		myExpId = 'myExperience_'+VideoRandomID;
		divId=VideoRandomID;
    };
}