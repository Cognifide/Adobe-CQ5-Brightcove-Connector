
function doFileUpload(){
	$CQ.bcUploaded = false;
    $CQ("#postFrame").load(
    		function(){
    			$CQ($CQ("#bcUpload").parents(".x-panel-bwrap")[0]).find(".cq-siteadmin-refresh").click();
    			}
    		);
	form = $CQ("#create_video_sample");
    form.attr('target', 'postFrame');
    buildJSONRequest(form);
    form.attr('action', "http://api.brightcove.com/services/post");
    form.submit();
    $CQ("#bcUpload").hide();
    $CQ("#waiting").show();
    
    
    
    
    
    
    
}

function buildJSONRequest(form){
    if($CQ('#name').val() =="" || $CQ('#shortDescription').val() =="" || $CQ("#filePath").val() ==""){
        alert("Require Name, Short Description and File");
        return;
    }else{
        $CQ('#JSONRPC').val('{"method": "create_video", "params": {"video": {"name": "' + $CQ('#name').val() + '", "shortDescription": "' + $CQ('#shortDescription').val() + '"},"token": "c9hG9CFjGaY6mguNiD7BKaYBZ2YCrCdoMlgV1y8LRgKNKgl-38duog.."}}');
    }
}

function showModalWindow() {
         //transition effect
		$CQ("#tagBCUpload").html('<p><a id="tagHref" href="#bcUpload" onclick="hideModalWindow(); return false;">Close Upload Window</a></p>');
		//$CQ("#bcUpload").parent().children(".cq-cft-search-item").remove();
        $CQ("#bcUpload").slideToggle(1000); 
        
}

function hideModalWindow() {
    //$CQ('#bcUpload').hide();
    //$CQ("#tagBCUpload").html('<p><a id="tagHref" href="#bcUpload" onclick="showModalWindow(); return false;">Upload a New Video</a></p>');
    $CQ($CQ("#bcUpload").parents(".x-panel-bwrap")[0]).find(".cq-siteadmin-refresh").click();
}
