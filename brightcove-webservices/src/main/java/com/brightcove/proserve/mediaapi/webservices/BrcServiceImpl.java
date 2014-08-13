package com.brightcove.proserve.mediaapi.webservices;

import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

@Component(	immediate=true,
			label="Brightcove Service",
            description="Brightcove Service Configuration",
            name="com.brightcove.proserve.mediaapi.webservices.BrcServiceImpl",
            metatype = true
            )
@Service
@Properties({
	@Property(name="readtoken", label="Read Token", description="Read Token", value=""),
	@Property(name="writetoken", label="Write Token", description="Write Token", value=""),
	@Property(name="previewPlayerLoc", label="Preview Video Player", description="Preview Player Path (Videos)", value="https://link.brightcove.com/services/player/bcpid1154829530001"),
	@Property(name="previewPlayerListLoc", label="Preview Playlist Player", description="Preview Player Path (Playlists)", value="https://link.brightcove.com/services/player/bcpid1154829529001"),
	@Property(name="defVideoPlayerID", label="Default Video Player ID", description="Default Video Player ID", value=""),
	@Property(name="defVideoPlayerKey", label="Default Video Player Key", description="Default Video Player Key", value=""),
	@Property(name="defPlaylistPlayerID", label="Default Playlist Player ID", description="Default Playlist Player ID", value=""),
	@Property(name="defPlaylistPlayerKey", label="Default Playlist Player Key", description="Default Playlist Player Key", value="")
})
public class BrcServiceImpl implements BrcService {
    private ComponentContext componentContext;
    private static Logger loggerVar = LoggerFactory.getLogger(BrcService.class);
    private static final String ALGO = "AES";
    private Dictionary<String, Object> prop;
    private Dictionary<String, Object> getProperties() {
        if (prop == null)
            return new Hashtable<String, Object>();
        return prop;
    }
    
    @Activate 
    void activate(ComponentContext aComponentContext) {
        this.componentContext=aComponentContext;
        this.prop = componentContext.getProperties();
    }

	public String getReadToken() {
		// TODO Auto-generated method stub
		return (String) getProperties().get("readtoken");
	}

	public String getWriteToken() {
		return (String) getProperties().get("writetoken");
	}
	
	public String getPreviewPlayerLoc() {
		// TODO Auto-generated method stub
		return (String) getProperties().get("previewPlayerLoc");
	}

	public String getPreviewPlayerListLoc() {
		return (String) getProperties().get("previewPlayerListLoc");
	}
	
	public String getDefVideoPlayerID() {
		// TODO Auto-generated method stub
		return (String) getProperties().get("defVideoPlayerID");
	}

	public String getDefVideoPlayerKey() {
		return (String) getProperties().get("defVideoPlayerKey");
	}
	
	public String getDefPlaylistPlayerID() {
		// TODO Auto-generated method stub
		return (String) getProperties().get("defPlaylistPlayerID");
	}

	public String getDefPlaylistPlayerKey() {
		return (String) getProperties().get("defPlaylistPlayerKey");
	}

}