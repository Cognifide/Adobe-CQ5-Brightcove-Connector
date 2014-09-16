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
