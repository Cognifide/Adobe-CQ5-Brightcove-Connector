Adobe-CQ5-Brightcove-Connector
==============================
This connector is a lighter version of orginal connector which is on master branch.

Main differences:
1. It doesn't support managing of Brightcove videos from CQ level.
2. It allows to override styles and javascript for Brightcove.
3. Brightcove Video is responsive, however it doesn't support alignment yet.

Original Cognifide's connector is available here:
https://github.com/Cognifide/Adobe-CQ5-Brightcove-Connector

==============================

To override styles and javascript of Brightcove Video/Playlist component, add new component and:
1. Set sling:resourceSuperType to brightcove/components/content/brightcoveplaylist or brightcove/components/content/brightcovevideo
2. Add brightcovevideoui.jsp or brightcoveplaylistui.jsp and include a clientLib with new styles and javascripts.


