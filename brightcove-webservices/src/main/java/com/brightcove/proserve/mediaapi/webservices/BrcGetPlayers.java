package com.brightcove.proserve.mediaapi.webservices;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.io.JSONWriter;

import com.brightcove.proserve.mediaapi.webservices.BrcService;
import com.brightcove.proserve.mediaapi.webservices.BrcUtils;

@Service
@Component
@Property(name = "sling.servlet.paths", value = "/bin/brightcove/players")
public class BrcGetPlayers extends SlingAllMethodsServlet {
	
	@Override
	protected void doPost(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response) throws ServletException,
            IOException {
			getPlayers(request,response);
			

    }
	
	protected void getPlayers(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response) throws IOException {
			JSONObject root = new JSONObject();
			JSONArray items = new JSONArray();
			PrintWriter outWriter = response.getWriter();
			BrcService brcService = BrcUtils.getSlingSettingService();
			String playerId = "";
			String playerKey = "";
			if ("video".equals(request.getParameter("group"))) {
				playerId = brcService.getDefVideoPlayerID();
	    		playerKey = brcService.getDefVideoPlayerKey();
			} else if ("playlist".equals(request.getParameter("group"))) {
				playerId = brcService.getDefPlaylistPlayerID();
	    		playerKey = brcService.getDefPlaylistPlayerKey();
			}
			JSONObject item = new JSONObject();
			try {
				item.put("playerId", playerId);
				item.put("playerKey", playerKey);
				items.put(item);
				root.put("items", items);
				outWriter.write(root.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//outWriter.write("{\"items\":[],\"results\":0}");
			}
	}
	
    @Override
    protected void doGet(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response) throws ServletException,
            IOException {
    		getPlayers(request,response);

    }

}
