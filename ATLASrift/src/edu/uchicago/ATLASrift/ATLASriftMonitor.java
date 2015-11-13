package edu.uchicago.ATLASrift;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



@SuppressWarnings("serial")
public class ATLASriftMonitor extends HttpServlet {

	private static final Logger log = Logger.getLogger(ATLASriftMonitor.class.getName());
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


	// GETS AND STORES CONFIGURATION INFORMATION FROM THE APPLICATION
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("ATLASrift reported Got a POST...");

		String ipAddress = req.getRemoteAddr();

		resp.setContentType("application/json");
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			log.severe("could not get json?");
			return;
		}

		log.info(jb.toString());

		JsonParser jp = new JsonParser();
		JsonElement root = null;
		JsonObject res = null;
		try {
			root = jp.parse(jb.toString());
			res = root.getAsJsonObject();
		} catch (Exception e) {
			log.severe("could not parse json.");
			return;
		}
		Entity visit = new Entity("ATLASriftVisits");

		Date currTime = new Date();
		visit.setProperty("timestamp", currTime);
		visit.setProperty("ip", ipAddress);
		visit.setProperty("3D", res.get("3D").getAsBoolean());
		visit.setProperty("Resolution", res.get("Resolution").getAsFloat());
		visit.setProperty("PostProc", res.get("PostProc").getAsFloat());
		visit.setProperty("Shadows", res.get("Shadows").getAsFloat());
		visit.setProperty("Textures", res.get("Textures").getAsFloat());
		visit.setProperty("Effects", res.get("Effects").getAsFloat());
		visit.setProperty("SpeachVolume", res.get("SpeachVolume").getAsFloat());
		visit.setProperty("EffectsVolume", res.get("EffectsVolume").getAsFloat());
		visit.setProperty("ShowCavern", res.get("ShowCavern").getAsBoolean());
		visit.setProperty("country", req.getHeader("X-AppEngine-Country"));
		visit.setProperty("region", req.getHeader("X-AppEngine-Region"));
		visit.setProperty("city", req.getHeader("X-AppEngine-City"));
		String latlong = req.getHeader("X-AppEngine-CityLatLong");
		visit.setProperty("lat", latlong.split(",")[0]);
		visit.setProperty("long", latlong.split(",")[1]);
		datastore.put(visit);

		return;
	}

	// RETREAVES AND RETURNS AS JSON CONFIGURATION INFORMATION FROM ALL THE VISITS
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");

		log.info("GOT A GET request. Will return all the data on all visits");
		
		List<Entity> lTemp = new ArrayList<Entity>();
		try {
			Query q = new Query("ATLASriftVisits");
			PreparedQuery pq = datastore.prepare(q);
			lTemp = pq.asList(FetchOptions.Builder.withDefaults());
		} catch (Exception e) {
			log.severe("could not reload info on visits." + e.getMessage());
		}
		if (lTemp == null || lTemp.isEmpty()) {
			log.severe("nothing loaded...");
			return;
		}
		log.info(lTemp.size() + " ips to load.");
		JsonArray Jres = new JsonArray();
		for (Entity result : lTemp) {
			String ip = (String) result.getProperty("ip");
			JsonObject r = new JsonObject();
			r.addProperty("ip", ip);
			if (result.hasProperty("long")) {
				r.addProperty("country", result.getProperty("country").toString());
				r.addProperty("city", result.getProperty("city").toString());
				r.addProperty("long", result.getProperty("long").toString());
				r.addProperty("lat", result.getProperty("lat").toString());
			}
			Jres.add(r);
		}
		resp.getWriter().print(Jres);
		return;
	}
}
