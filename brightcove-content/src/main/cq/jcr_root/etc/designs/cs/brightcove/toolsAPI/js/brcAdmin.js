/*vm_wire.js
*functions for communication
 */
 
/********************** 
*The Ajile library is used *
*to parse json responses  *
**********************/
 
/*!***************************!*
*!! write functionality disabled:      *
* the functions for write methods  *
* have been modified, they are not *
* good examples.  Please see the     *
* actual docs for this page to see   *
* how the write methods work.        *
******************************/
 
//CONFIG 
//This should be set to point to proxy.jsp on your server
var apiLocation = "/apps/brightcove/console/brightcove.proxy.html";


//Default Fields
//specifying a subset of fields cuts down on the amount of data sent over the wire.  If you want to access another field, include it here.
var fields = "id,name,shortDescription,longDescription,publishedDate,lastModifiedDate,linkURL,linkText,tags,thumbnailURL,referenceId,length,economics,videoStillURL";
//END CONFIG

//oCurrentVideoList and oCurrentPlaylistList hold the JSON data about videos and playlists
var oCurrentVideoList;
var oCurrentPlaylistList;

function getAllVideosURL() {
    loadStart();
    //If currentFunction == this function name, then the current view already correspdonds to this function, so use the generic holder value
    //otherwise, we're switching from a different view so use the last stored value.
    paging.allVideos = (paging.currentFunction == getAllVideosURL)?paging.generic:paging.allVideos;
    paging.currentFunction = getAllVideosURL;
    return apiLocation +
     '?command=search_videos&callback=showAllVideosCallBack&sort_by=DISPLAY_NAME'
        + '&get_item_count=true&page_size=' + paging.size + '&page_number='+paging.allVideos
        +'&fields='+fields;
}
function isNumber(n) {
	  return !isNaN(parseFloat(n)) && isFinite(n);
	}
function getAllPlaylistsURL(){
    loadStart();
    paging.allPlaylists = (paging.currentFunction == getAllPlaylistsURL)?paging.generic:paging.allPlaylists;
    paging.currentFunction = getAllPlaylistsURL;
    return apiLocation +
    '?command=find_all_playlists&callback=showAllPlaylistsCallBack'
        + '&get_item_count=true&page_size=' + paging.size + '&page_number='+paging.allPlaylists;
}

function searchVideoURL(){
    loadStart();
    paging.textSearch = (paging.currentFunction == searchVideoURL)?paging.generic:paging.textSearch;
    paging.currentFunction  = searchVideoURL;
    if(searchVal != "" && searchVal != "Search Videos"){
        if (!isNumber(searchVal)) { 
	        return apiLocation +
	        '?command=search_videos&sort_by=DISPLAY_NAME&callback=searchVideoCallBack&any=search_text:'+searchVal
	            + '&any=tag:'+searchVal+'&get_item_count=true&page_size=' + paging.size + '&page_number='+paging.textSearch
	            +'&fields='+fields; 
        } else {
        	return apiLocation +
	        '?command=find_videos_by_ids&callback=searchVideoCallBack&video_ids='+searchVal
	            + '&get_item_count=true&fields='+fields; 
        }
    }
}

function findByTag(){
    loadStart();
    paging.tagSearch = (paging.currentFunction == findByTag)?paging.generic:paging.tagSearch;
    paging.currentFunction = findByTag;
    return apiLocation + '?command=find_videos_by_tags&and_tags='+searchVal+
        '&get_item_count=true&page_size='+paging.size+'&page_number='+paging.tagSearch+'&callback=findByTagCallBack&fields='
        +fields;
}

/*delete_video is a write method, and needs to be submitted to the api server as a multipart/post request.
* however, since there are only a few fields of data being sent from this request, we'll send it as a GEt request to the proxy
* server and let it format the multipart request.~
* the line '}else if ( write_methods.contains(request.getParameter("command")) && request.getMethod() == "GET"){'
* in proxy.jsp handles this kind of request.
*/
function deleteVideoURL(videoId){
    loadStart();
    return apiLocation + '?command=delete_video&ids='+videoId+'&callback=showAllVideosCallBack';
    
}

function showAllVideosCallBack(o) {
    if(null == o.error){
        oCurrentVideoList = o.items;
        buildMainVideoList("All Videos");
        document.getElementById('search').value = 'Search Video';
        doPageList(o.total_count, "Videos");
    }else{
        var message = (null!=o.error.message)?o.error.message:o.error;
        alert("Server Error: "+ message);
    }
    loadEnd();
}

function showAllPlaylistsCallBack(o){
    if(null == o.error){
        oCurrentPlaylistList = o.items;
        buildPlaylistList();
        doPageList(o.total_count, "Playlists");
    }else{
        var message = (null!=o.error.message)?o.error.message:o.error;
        alert("Server Error: "+ message);
    }
    loadEnd();
}

function searchVideoCallBack(o){
    if(null == o.error){
        oCurrentVideoList = o.items;
        buildMainVideoList("Search results");
        var count = (o.total_count >= 0) ? o.total_count : o.items.length;
        doPageList(count, "Videos");
    }else{  
        var message = (null!=o.error.message)?o.error.message:o.error;
        alert("Server Error: "+ message);
    }
    loadEnd();
}

function findByTagCallBack(o){
    if(null == o.error){
        oCurrentVideoList = o.items;
        buildMainVideoList("Tag Search results");
        doPageList(o.total_count, "Videos");
    }else{  
        var message = (null!=o.error.message)?o.error.message:o.error;
        alert("Server Error: "+ message);
    }
    loadEnd();
}

//Function calls update_video to change metadata
//Also see metaEdit in vm_ui.js
function metaSubmit(){
    var form = document.getElementById("metaEditForm");
    form.action = apiLocation;
    form.submit();
    loadStart();
    //noWrite();
    closeBox('metaEditPop');
    Load(getAllVideosURL());
}

function delConfYes(){
    var checkedVideos = $("#tblMainList input:checked");
    var IDs = "";
    checkedVideos.each(function() {
        IDs = IDs+$(this).val() + ",";
    }
    );
    Load(deleteVideoURL(IDs));
    closeBox('delConfPop');
    Load(getAllVideosURL());
}

function buildJSONRequest(form){
    if(document.getElementById('name').value =="" || document.getElementById('shortDescription').value =="" || form.filePath.value ==""){
        alert("Require Name, Short Description and File");
        return;
    }else{
        json = form.video
        //Construct the JSON request:
        json.value = '{"name": "' + 
        document.getElementById('name').value + '", "shortDescription": "' + document.getElementById('shortDescription').value + 
        '"}';
        form.video.value = json.value;
    }
}
function startUpload(){
    var form = document.getElementById("uploadForm");
    buildJSONRequest(form);
    form.action = apiLocation;
    form.submit();
    loadStart();
    //noWrite();
    closeBox('uploadDiv', form);
    Load(getAllVideosURL());
}
function startImageUpload(){
    var form = document.getElementById("uploadImageForm");
    form.action = apiLocation;
    form.submit();
    loadStart();
    //noWrite();
    closeBox('uploadImageDiv', form);
    Load(getAllVideosURL());
}
function startVideoImageUpload(){
    var form = document.getElementById("uploadVideoImageForm");
    form.action = apiLocation;
    form.submit();
    loadStart();
    //noWrite();
    closeBox('uploadVideoImageDiv', form);
    Load(getAllVideosURL());
}
//See createPlaylistBox for more info
function createPlaylistSubmit(){
    var form = document.getElementById("createPlaylistForm");
    
    form.action = apiLocation;
    form.submit();
    $('#createPlstVideoTable').empty();
    loadStart();
    //noWrite();
    closeBox('createPlaylistDiv', form);
    Load(getAllPlaylistsURL());
}

function modPlstSubmit(){
    loadStart();
    noWrite();
    closeBox('modPlstPop');
    Load(getAllPlaylistsURL());
}

function prepareSearchVal() {
	searchVal=document.getElementById('search').value;
}