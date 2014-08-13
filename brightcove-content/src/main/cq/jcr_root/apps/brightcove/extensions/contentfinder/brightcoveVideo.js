{
    "tabTip": CQ.I18n.getMessage("Brightcove"),
    "id": "cfTab-Brightcove",
    "iconCls": "cq-cft-tab-icon brightcove",
    "xtype": "contentfindertab",
    "ranking": 30,
    "allowedPaths": [
                     "/content/*",
                     "/etc/scaffolding/*",
                     "/etc/workflow/packages/*"
                 ],
    "items": [
       CQ.wcm.ContentFinderTab.getQueryBoxConfig({
            "id": "cfTab-Brightcove-QueryBox",
            "items": [
                CQ.wcm.ContentFinderTab.getSuggestFieldConfig({"url": "/bin/brightcove/suggestions.json?type=videos"})
            ]
        }),
        CQ.wcm.ContentFinderTab.getResultsBoxConfig({
            "itemsDDGroups": [CQ.wcm.EditBase.DD_GROUP_ASSET],
            "itemsDDNewParagraph": {
                "path": "brightcove/components/content/brightcovevideo",
                "propertyName": "./videoPlayer"
            },
            "noRefreshButton": true,
            "items": {
                "tpl":
                    '<tpl for=".">' +
                            '<div class="cq-cft-search-item" title="{thumbnailURL}" ondblclick="window.location=\'/apps/brightcove/console/brightcove.html\';">' +
                                    '<div class="cq-cft-search-thumb-top"' +
                                    ' style="background-image:url(\'{thumbnailURL}\');"></div>' +
                                         '<div class="cq-cft-search-text-wrapper">' +
                                            '<div class="cq-cft-search-title"><p class="cq-cft-search-title">{name}</p><p>{path}</p></div>' +
                                        '</div>' +
                                    '<div class="cq-cft-search-separator"></div>' +
                            '</div>' +
                    '</tpl>',
                "itemSelector": CQ.wcm.ContentFinderTab.DETAILS_ITEMSELECTOR
            },
            "tbar": [
                CQ.wcm.ContentFinderTab.REFRESH_BUTTON,"->",
                {
                    text: "Export CSV",
                    handler: function() {
                        var url='/bin/brightcove/api?a=3&query='+$("#cfTab-Brightcove-QueryBox input[name=query]").val();  
                        window.open(url, 'Download');

                   }
                }
            ]
        },{
            "url": "/bin/brightcove/api?a=2"
        },{
            "autoLoad":false,
            "reader": new CQ.Ext.data.JsonReader({
                "root": "items",
                "fields": [
                    "name", "path", "thumbnailURL"
                ],
                "id": "path"
                
            })
        
        })
    ]

}