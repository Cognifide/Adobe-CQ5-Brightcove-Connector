package com.brightcove.proserve.mediaapi.webservices;

import com.brightcove.proserve.mediaapi.wrapper.ReadApi;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Playlist;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Video;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Videos;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.PlaylistFieldEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.SortByTypeEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.SortOrderTypeEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.VideoFieldEnum;
import com.brightcove.proserve.mediaapi.wrapper.utils.CollectionUtils;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BrcUtils {

	public static BrcService getSlingSettingService() {
		BundleContext bundleContext = FrameworkUtil.getBundle(BrcService.class).getBundleContext();
		return (BrcService) bundleContext.getService(bundleContext.getServiceReference(BrcService.class.getName()));
	}

	static List sortByValue(final Map m) {
		List keys = new ArrayList();
		keys.addAll(m.keySet());
		Collections.sort(keys, new Comparator() {
			public int compare(Object o1, Object o2) {
				try {
					JSONObject v1 = (JSONObject) m.get(o1);
					String s1 = (String) v1.get("name");
					JSONObject v2 = (JSONObject) m.get(o2);
					String s2 = (String) v2.get("name");

					if (s1 == null) {
						return (s2 == null) ? 0 : 1;
					} else if (s1 instanceof Comparable) {
						return ((Comparable) s1).compareTo(s2);
					} else {
						return 0;
					}

				} catch (JSONException e) {
					return 0;
				}
			}
		});
		return keys;
	}

	static String getLength(String videoId, String tokenID) {
		String result = "";

		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		BufferedReader rd;

		try {

			URL serverAddress = new URL("http://api.brightcove.com/services/library?command=find_video_by_id&video_id=" + videoId + "&video_fields=name,length&token=" + tokenID);
			//set up out communications stuff
			connection = null;

			//Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}

			JSONObject js = new JSONObject(sb.toString());

			String temp_result = js.getString("length");
			long millis = Long.parseLong(temp_result);
			result = String.format("%02d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(millis),
					TimeUnit.MILLISECONDS.toSeconds(millis) -
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
			);

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//close the connection, set all objects to null
			if (connection != null) connection.disconnect();
			rd = null;
			wr = null;
			connection = null;
		}
		return result;
	}

	static String getName(String videoId, String tokenID) {
		String result = "";


		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder sb = null;
		String line = null;
		URL serverAddress = null;

		try {

			serverAddress = new URL("http://api.brightcove.com/services/library?command=find_video_by_id&video_id=" + videoId + "&video_fields=name,length&token=" + tokenID);
			//set up out communications stuff
			connection = null;

			//Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}

			JSONObject js = new JSONObject(sb.toString());

			result = js.getString("name");


		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//close the connection, set all objects to null
			connection.disconnect();
			rd = null;
			sb = null;
			wr = null;
			connection = null;
		}
		return result;
	}

	public static String getList(String token, String params, Boolean exportCSV, String query) {

		String result = "";
		Logger loggerBRi = LoggerFactory.getLogger("Brightcove");
		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder sb = null;
		String line = null;
		URL serverAddress = null;
		JSONObject js = null;
		try {

			java.util.Map<String, JSONObject> sortedjson = new HashMap();

			int pageNumber = 0;
			double totalPages = 0;

			while (pageNumber <= totalPages) {

				if (query != null && !query.trim().isEmpty()) {
					if (isLong(query)) {
						serverAddress = new URL("http://api.brightcove.com/services/library?command=find_videos_by_ids&video_ids=" + URLEncoder.encode(query.trim(), "UTF-8") + "&video_fields=name,id,thumbnailURL&token=" + token);
					} else {
						serverAddress = new URL("http://api.brightcove.com/services/library?command=search_videos&sort_by=DISPLAY_NAME&any=search_text:" + URLEncoder.encode(query.trim(), "UTF-8") + "&any=tag:" + URLEncoder.encode(query.trim(), "UTF-8") + "&video_fields=name,id,thumbnailURL&get_item_count=true&page_size=20&page_number=" + pageNumber + "&token=" + token);
					}
				} else {
					serverAddress = new URL("http://api.brightcove.com/services/library?command=search_videos&sort_by=DISPLAY_NAME&video_fields=" + params + "&get_item_count=true&page_number=" + pageNumber + "&token=" + token);
				}
				loggerBRi.debug(query);
				//set up out communications stuff
				connection = null;

				//Set up the initial connection
				connection = (HttpURLConnection) serverAddress.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setReadTimeout(10000);

				connection.connect();

				rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				sb = new StringBuilder();

				while ((line = rd.readLine()) != null) {
					sb.append(line + '\n');
				}

				js = new JSONObject(sb.toString());

				totalPages = Math.floor(js.getInt("total_count") / 100);

				JSONArray jsa = new JSONArray(js.get("items").toString());
				for (int i = 0; i < jsa.length(); i++) {
					JSONObject row = jsa.getJSONObject(i);
					sortedjson.put(row.getString("id"), row);
				}

				pageNumber++;
			}

			if (exportCSV) {
				JSONObject tempJSON;
				String csvString = "\"Video Name\",\"Video ID\"\r\n";

				for (Iterator i = sortByValue(sortedjson).iterator(); i.hasNext(); ) {
					String key = (String) i.next();
					tempJSON = sortedjson.get(key);
					csvString += "\"" + tempJSON.getString("name") + "\",\"" + tempJSON.getString("id") + "\"\r\n";
				}
				result = csvString;
			} else {
				JSONObject jsTotal = new JSONObject();

				for (Iterator i = sortByValue(sortedjson).iterator(); i.hasNext(); ) {
					String key = (String) i.next();
					jsTotal.accumulate("items", sortedjson.get(key));
				}
				jsTotal.put("totals", js.getInt("total_count"));
				result = jsTotal.toString();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//close the connection, set all objects to null
			connection.disconnect();
			rd = null;
			sb = null;
			wr = null;
			connection = null;
		}
		return result;
	}

	public static String getList(String token, String params, Boolean exportCSV, String start, String limit, String query) {

		String result = "";
		Logger loggerBRi = LoggerFactory.getLogger("Brightcove");
		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder sb = null;
		String line = null;
		URL serverAddress = null;
		JSONObject jsTotal = new JSONObject();
		try {

			java.util.Map<String, JSONObject> sortedjson = new HashMap();

			int pageNumber = 0;
			int firstElement = 0;
			if (start != null && !start.trim().isEmpty() && Integer.parseInt(start) > 0) {
				firstElement = Integer.parseInt(start);
				if (limit != null && !limit.trim().isEmpty())
					pageNumber = (firstElement + Integer.parseInt(limit)) / 20;

			}
			int totalPages = 0;

			if (query != null && !query.trim().isEmpty()) {
				if (isLong(query)) {
					serverAddress = new URL("http://api.brightcove.com/services/library?command=find_videos_by_ids&video_ids=" + URLEncoder.encode(query.trim(), "UTF-8") + "&video_fields=name,id,thumbnailURL&token=" + token);
				} else {
					serverAddress = new URL("http://api.brightcove.com/services/library?command=search_videos&sort_by=DISPLAY_NAME&any=search_text:" + URLEncoder.encode(query.trim(), "UTF-8") + "&any=tag:" + URLEncoder.encode(query.trim(), "UTF-8") + "&video_fields=name,id,thumbnailURL&get_item_count=true&page_size=20&page_number=" + pageNumber + "&token=" + token);
				}
				loggerBRi.debug(serverAddress.toString());
			} else {
				serverAddress = new URL("http://api.brightcove.com/services/library?command=search_videos&sort_by=DISPLAY_NAME&video_fields=name,id,thumbnailURL&get_item_count=true&page_size=20&page_number=" + pageNumber + "&token=" + token);
			}
			//set up out communications stuff
			connection = null;

			//Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}


			JSONObject js = new JSONObject(sb.toString().replace("\"id\"", "\"videoid\"").replaceAll("\"videoid\":([0-9]*)", "\"path\":\"$1\""));
			totalPages = js.getInt("total_count");
			if (isLong(query)) {

				totalPages = js.getJSONArray("items").length();
			}

			if (exportCSV) {
				JSONObject tempJSON;
				String csvString = "\"Video Name\",\"Video ID\"\r\n";

				for (int i = 0; i < totalPages; i++) {
					tempJSON = js.getJSONArray("items").getJSONObject(i);
					csvString += "\"" + tempJSON.getString("name") + "\",\"" + tempJSON.getString("id") + "\"\r\n";
				}
				result = csvString;
			} else {


				if (firstElement < totalPages) {
					jsTotal.put("items", js.get("items"));
					jsTotal.put("results", totalPages);
				} else {
					jsTotal = new JSONObject("{\"items\":[],\"results\":0}");
				}

			}


			result = jsTotal.toString();


		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//close the connection, set all objects to null
			connection.disconnect();
			rd = null;
			sb = null;
			wr = null;
			connection = null;
		}
		return result;
	}

	public static boolean isLong(String input) {
		if (input == null || input.trim().isEmpty()) return false;
		try {
			Long.parseLong(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String searchVideo(String querystr, String start, String limit) {
		JSONObject jsTotal = new JSONObject();
		Logger loggerBRi = LoggerFactory.getLogger("Brightcove");

		try {
			BrcService brcService = getSlingSettingService();
			String readToken = brcService.getReadToken();
			ReadApi rapi = new ReadApi(loggerBRi);
			// Return only name,id,thumbnailURL
			EnumSet<VideoFieldEnum> videoFields = VideoFieldEnum.CreateEmptyEnumSet();
			videoFields.add(VideoFieldEnum.ID);
			videoFields.add(VideoFieldEnum.NAME);
			videoFields.add(VideoFieldEnum.THUMBNAILURL);
			// Return no custom fields on all videos
			Set<String> customFields = CollectionUtils.CreateEmptyStringSet();

			List<String> all = new ArrayList();
			List<String> any = new ArrayList();
			List<String> none = new ArrayList();
			Boolean exact = false;

			int pageNumber = 0;
			int firstElement = 0;
			if (start != null && !start.trim().isEmpty() && Integer.parseInt(start) > 0) {
				firstElement = Integer.parseInt(start);
				if (limit != null && !limit.trim().isEmpty())
					pageNumber = (firstElement + Integer.parseInt(limit)) / 20;

			}
			int totalVideos = 0;
			Videos videos = null;
			if (querystr != null && !querystr.trim().isEmpty()) {
				any.add("tag:" + querystr);
				any.add("search_text:" + querystr);

				videos = rapi.SearchVideos(readToken, all, any, none, exact, SortByTypeEnum.DISPLAY_NAME, SortOrderTypeEnum.ASC, 20, pageNumber, videoFields, customFields);
				if (isLong(querystr)) {
					Set<Long> videoIds = new HashSet<Long>();
					videoIds.add(Long.parseLong(querystr));
					videos.addAll(rapi.FindVideosByIds(readToken, videoIds, videoFields, customFields));
				}
			} else {
				loggerBRi.error("noQuery");
				videos = rapi.SearchVideos(readToken, all, any, none, exact, SortByTypeEnum.DISPLAY_NAME, SortOrderTypeEnum.ASC, 20, pageNumber, videoFields, customFields);
				//videos = rapi.FindAllVideos(readToken, 20, pageNumber, SortByTypeEnum.DISPLAY_NAME, SortOrderTypeEnum.ASC, videoFields, customFields);
			}
			JSONArray items = new JSONArray();
			if (videos != null) {

				for (Video vid : videos) {
					JSONObject item = new JSONObject();

					item.put("id", vid.getId());
					item.put("name", vid.getName());
					item.put("thumbnailURL", vid.getThumbnailUrl());
					items.put(item);
				}
				totalVideos = videos.size();
			}
			jsTotal.put("items", items);
			jsTotal.put("results", totalVideos);
		} catch (Exception e) {
			loggerBRi.error(e.getMessage());
		}
		return jsTotal.toString();

	}

	public static String getSelectedVideo(String videoIdstr) {
		JSONObject jsTotal = new JSONObject();
		try {
			Long videoId = Long.parseLong(videoIdstr);
			BrcService brcService = getSlingSettingService();
			String readToken = brcService.getReadToken();
			ReadApi rapi = new ReadApi();
			// Return only name,id,thumbnailURL
			EnumSet<VideoFieldEnum> videoFields = VideoFieldEnum.CreateEmptyEnumSet();
			videoFields.add(VideoFieldEnum.ID);
			videoFields.add(VideoFieldEnum.NAME);
			videoFields.add(VideoFieldEnum.THUMBNAILURL);
			// Return no custom fields on all videos
			Set<String> customFields = CollectionUtils.CreateEmptyStringSet();
			JSONArray items = new JSONArray();
			JSONObject item = new JSONObject();

			Video selectedVideo = rapi.FindVideoById(readToken, videoId, videoFields, customFields);
			if (selectedVideo != null) {
				item.put("id", selectedVideo.getId());
				item.put("name", selectedVideo.getName());
				item.put("thumbnailURL", selectedVideo.getThumbnailUrl());
				items.put(item);
			}
			jsTotal.put("items", items);
			jsTotal.put("results", 1);
		} catch (Exception e) {

		}
		return jsTotal.toString();

	}

	public static String getListSideMenu(String token, String limit) {
		String result = "";

		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder sb = null;
		String line = null;
		URL serverAddress = null;
		JSONObject jsTotal = new JSONObject();
		try {

			java.util.Map<String, JSONObject> sortedjson = new HashMap();

			int pageNumber = 0;
			int firstElement = 0;
			if (limit != null && !limit.trim().isEmpty() && limit.split("\\.\\.")[0] != null) {
				pageNumber = Integer.parseInt(limit.split("\\.\\.")[0]) / 20;
				firstElement = Integer.parseInt(limit.split("\\.\\.")[0]);
			}
			int totalPages = 0;

			serverAddress = new URL("http://api.brightcove.com/services/library?command=search_videos&sort_by=DISPLAY_NAME&video_fields=name,id,thumbnailURL&get_item_count=true&page_size=20&page_number=" + pageNumber + "&token=" + token);
			//set up out communications stuff
			connection = null;

			//Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}


			JSONObject js = new JSONObject(sb.toString());
			totalPages = js.getInt("total_count");
			if (firstElement < totalPages) {
				JSONArray jsa = new JSONArray(js.get("items").toString());
				for (int i = 0; i < jsa.length(); i++) {
					JSONObject row = jsa.getJSONObject(i);
					sortedjson.put(row.getString("id"), row);
				}


				for (Iterator i = sortByValue(sortedjson).iterator(); i.hasNext(); ) {
					String key = (String) i.next();
					jsTotal.accumulate("items", sortedjson.get(key));
				}
				jsTotal.put("results", js.getInt("total_count"));
			} else {
				jsTotal = new JSONObject("{\"items\":[],\"results\":0}");
			}


			result = jsTotal.toString().replace("\"id\"", "\"videoid\"").replaceAll("\"videoid\":([0-9]*)", "\"path\":\"$1\"");


		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//close the connection, set all objects to null
			connection.disconnect();
			rd = null;
			sb = null;
			wr = null;
			connection = null;
		}
		return result;
	}

	public static String getSuggestions(String querystr, String start, String limit) {
		JSONObject jsTotal = new JSONObject();
		Logger loggerBRi = LoggerFactory.getLogger("Brightcove");

		try {
			BrcService brcService = getSlingSettingService();
			String readToken = brcService.getReadToken();
			ReadApi rapi = new ReadApi(loggerBRi);
			// Return only name,id,thumbnailURL
			EnumSet<VideoFieldEnum> videoFields = VideoFieldEnum.CreateEmptyEnumSet();
			videoFields.add(VideoFieldEnum.ID);
			videoFields.add(VideoFieldEnum.NAME);
			videoFields.add(VideoFieldEnum.THUMBNAILURL);
			// Return no custom fields on all videos
			Set<String> customFields = CollectionUtils.CreateEmptyStringSet();

			List<String> all = new ArrayList();
			List<String> any = new ArrayList();
			List<String> none = new ArrayList();
			Boolean exact = false;

			int pageNumber = 0;
			int firstElement = 0;
			if (start != null && !start.trim().isEmpty() && Integer.parseInt(start) > 0) {
				firstElement = Integer.parseInt(start);
				if (limit != null && !limit.trim().isEmpty())
					pageNumber = (firstElement + Integer.parseInt(limit)) / 20;

			}
			int totalVideos = 0;
			Videos videos = null;
			if (querystr != null && !querystr.trim().isEmpty()) {
				any.add("tag:" + querystr);
				any.add("search_text:" + URLEncoder.encode(querystr, "UTF-8"));

				videos = rapi.SearchVideos(readToken, all, any, none, exact, SortByTypeEnum.DISPLAY_NAME, SortOrderTypeEnum.ASC, 20, pageNumber, videoFields, customFields);
				if (isLong(querystr)) {
					Set<Long> videoIds = new HashSet<Long>();
					videoIds.add(Long.parseLong(querystr));
					videos.addAll(rapi.FindVideosByIds(readToken, videoIds, videoFields, customFields));
				}
			} else {
				loggerBRi.error("noQuery");
				videos = rapi.SearchVideos(readToken, all, any, none, exact, SortByTypeEnum.DISPLAY_NAME, SortOrderTypeEnum.ASC, 20, pageNumber, videoFields, customFields);
				//videos = rapi.FindAllVideos(readToken, 20, pageNumber, SortByTypeEnum.DISPLAY_NAME, SortOrderTypeEnum.ASC, videoFields, customFields);
			}
			JSONArray items = new JSONArray();
			if (videos != null) {

				for (Video vid : videos) {
					JSONObject item = new JSONObject();

					item.put("name", vid.getId());
					item.put("value", vid.getName());
					item.put("title", vid.getName());
					item.put("thumbnailURL", vid.getThumbnailUrl());
					items.put(item);
				}
				totalVideos = videos.size();
			}
			jsTotal.put("suggestions", items);
			jsTotal.put("results", totalVideos);
		} catch (Exception e) {
			loggerBRi.error(e.getMessage());
		}
		return jsTotal.toString();

	}

	public static String getPlaylistByID(String querystr, String start, String limit) {
		JSONObject jsTotal = new JSONObject();
		Logger loggerBRi = LoggerFactory.getLogger("Brightcove");
		int totalPlaylists = 0;

		JSONArray items = new JSONArray();
		try {
			BrcService brcService = getSlingSettingService();
			String readToken = brcService.getReadToken();
			ReadApi rapi = new ReadApi(loggerBRi);
			// Return only name,id,thumbnailURL
			EnumSet<VideoFieldEnum> videoFields = VideoFieldEnum.CreateEmptyEnumSet();
			videoFields.add(VideoFieldEnum.ID);
			videoFields.add(VideoFieldEnum.NAME);
			videoFields.add(VideoFieldEnum.THUMBNAILURL);
			EnumSet<PlaylistFieldEnum> playlistFields = PlaylistFieldEnum.CreateEmptyEnumSet();
			playlistFields.add(PlaylistFieldEnum.ID);
			playlistFields.add(PlaylistFieldEnum.NAME);
			playlistFields.add(PlaylistFieldEnum.THUMBNAILURL);
			// Return no custom fields on all videos
			Set<String> customFields = CollectionUtils.CreateEmptyStringSet();

			List<String> all = new ArrayList();
			List<String> any = new ArrayList();
			List<String> none = new ArrayList();
			Boolean exact = false;

			int pageNumber = 0;
			int firstElement = 0;
			if (start != null && !start.trim().isEmpty() && Integer.parseInt(start) > 0) {
				firstElement = Integer.parseInt(start);
				if (limit != null && !limit.trim().isEmpty())
					pageNumber = (firstElement + Integer.parseInt(limit)) / 20;

			}
			Playlist playlist = null;
			if (querystr != null && !querystr.trim().isEmpty() && isLong(querystr)) {

				Long playlistId = Long.parseLong(querystr);


				playlist = rapi.FindPlaylistById(readToken, playlistId, videoFields, customFields, playlistFields);
			}

			if (playlist != null) {

				JSONObject item = new JSONObject();

				item.put("path", String.valueOf(playlist.getId()));
				item.put("name", playlist.getName());
				item.put("thumbnailURL", playlist.getThumbnailUrl());
				items.put(item);

				totalPlaylists = 1;
			} else {
				totalPlaylists = 0;
			}

		} catch (Exception e) {
			loggerBRi.error(e.getMessage());
		}
		try {
			jsTotal.put("items", items);
			jsTotal.put("results", totalPlaylists);
		} catch (JSONException e) {
			loggerBRi.error(e.getMessage());
		}

		return jsTotal.toString();

	}

	//Returns JSON of the video information based on a comma separated string of their ids.
	static JSONArray getVideosJsonByIds(String videoIds, String videoProperties, String tokenID) {
		JSONArray jsa = new JSONArray();


		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder sb = null;
		String line = null;
		URL serverAddress = null;

		try {

			serverAddress = new URL("http://api.brightcove.com/services/library?command=find_videos_by_ids&video_ids=" + videoIds + "&video_fields=" + videoProperties + "&token=" + tokenID);

			//set up connection
			connection = null;

			//Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}


			JSONObject js = new JSONObject(sb.toString());
			jsa = new JSONArray(js.get("items").toString());


		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//close the connection, set all objects to null
			connection.disconnect();
			rd = null;
			sb = null;
			wr = null;
			connection = null;
		}
		return jsa;
	}

	//FindAllPlaylists(String readToken, Integer pageSize, Integer pageNumber, SortByTypeEnum sortBy, SortOrderTypeEnum sortOrderType, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields, EnumSet<PlaylistFieldEnum> playlistFields)
	public static String getListPlaylistsSideMenu(String token, String limit) {
		String result = "";

		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder sb = null;
		String line = null;
		URL serverAddress = null;
		JSONObject jsTotal = new JSONObject();
		try {

			java.util.Map<String, JSONObject> sortedjson = new HashMap();

			int pageNumber = 0;
			int firstElement = 0;
			if (limit != null && !limit.trim().isEmpty() && limit.split("\\.\\.")[0] != null) {
				pageNumber = Integer.parseInt(limit.split("\\.\\.")[0]) / 20;
				firstElement = Integer.parseInt(limit.split("\\.\\.")[0]);
			}
			int totalPages = 0;

			serverAddress = new URL("http://api.brightcove.com/services/library?command=find_all_playlists&playlist_fields=name,id,thumbnailURL&get_item_count=true&page_number=" + pageNumber + "&token=" + token);
			//set up out communications stuff
			connection = null;

			//Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sb = new StringBuilder();

			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}


			JSONObject js = new JSONObject(sb.toString());
			totalPages = js.getInt("total_count");
			if (firstElement < totalPages) {
				JSONArray jsa = new JSONArray(js.get("items").toString());
				for (int i = 0; i < jsa.length(); i++) {
					JSONObject row = jsa.getJSONObject(i);
					sortedjson.put(row.getString("id"), row);
				}

				JSONArray jarr = new JSONArray();
				for (Iterator i = sortByValue(sortedjson).iterator(); i.hasNext(); ) {
					String key = (String) i.next();
					jarr.put(sortedjson.get(key));

				}
				jsTotal.put("items", jarr);
				jsTotal.put("results", js.getInt("total_count"));
			} else {
				jsTotal = new JSONObject("{\"items\":[],\"results\":0}");
			}


			result = jsTotal.toString().replace("\"id\"", "\"videoid\"").replaceAll("\"videoid\":([0-9]*)", "\"path\":\"$1\"");


		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//close the connection, set all objects to null
			connection.disconnect();
			rd = null;
			sb = null;
			wr = null;
			connection = null;
		}
		return result;
	}

}
