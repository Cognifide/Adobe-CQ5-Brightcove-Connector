<script type="text/javascript">
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