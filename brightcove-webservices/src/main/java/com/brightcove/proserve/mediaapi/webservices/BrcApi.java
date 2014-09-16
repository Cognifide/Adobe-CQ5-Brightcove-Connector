package com.brightcove.proserve.mediaapi.webservices;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

@Service
@Component
@Property(name = "sling.servlet.paths", value = "/bin/brightcove/api")
public class BrcApi extends SlingAllMethodsServlet {

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		api(request, response);
	}

	public void api(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter outWriter = response.getWriter();
		response.setContentType("application/json");

		BrcService brcService = BrcUtils.getSlingSettingService();
		String ReadToken = brcService.getReadToken();

		int requestedAPI = 0;
		if (request.getParameter("a") != null) {
			response.setContentType("application/json");

			requestedAPI = Integer.parseInt(request.getParameter("a"));
			switch (requestedAPI) {
				case 0: // no commands
					outWriter.write("aaaaa");
					break;
				case 1:
					outWriter.write(BrcUtils.getList(ReadToken, "name,id,thumbnailURL", false,
							request.getParameter("start"), request.getParameter("limit"),
							request.getParameter("query")));
					break;
				case 2:
					if (request.getParameter("query") != null
							&& !request.getParameter("query").trim().isEmpty()) {
						outWriter.write(BrcUtils.getList(ReadToken, "name,id,thumbnailURL", false,
								request.getParameter("start"), request.getParameter("limit"),
								request.getParameter("query")));
					} else {
						outWriter.write(BrcUtils.getListSideMenu(ReadToken, request.getParameter("limit")));
					}
					break;
				case 3:
					response.reset();
					response.setHeader("Content-type", "application/xls");
					response.setHeader("Content-disposition",
							"inline; filename=Brightcove_Library_Export.csv");
					outWriter.write(BrcUtils.getList(ReadToken, "name,id,thumbnailURL", true,
							request.getParameter("query")));
					break;
				case 4:
					if (request.getParameter("query") != null
							&& !request.getParameter("query").trim().isEmpty()) {
						outWriter.write(BrcUtils.getPlaylistByID(request.getParameter("query"),
								request.getParameter("start"), request.getParameter("limit")));
					} else {
						outWriter.write(BrcUtils.getListPlaylistsSideMenu(ReadToken,
								request.getParameter("limit")));
					}
					break;
				case 5:
					if ("true".equals(request.getParameter("isID"))) {
						outWriter.write(BrcUtils.getSelectedVideo(request.getParameter("query")));
					} else {
						outWriter.write(BrcUtils.searchVideo(request.getParameter("query"),
								request.getParameter("start"), request.getParameter("limit")));
					}
					break;

				default:
					break;
			}
		} else {
			outWriter.write("{\"items\":[],\"results\":0}");
		}

	}

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		api(request, response);

	}

}
