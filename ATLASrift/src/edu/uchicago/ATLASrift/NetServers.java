package edu.uchicago.ATLASrift;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings("serial")
public class NetServers extends HttpServlet {

	private static final Logger log = Logger.getLogger(NetServers.class.getName());
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		// GETS AN EVENT AS JSON AND STORES IT IN THE DATASTORE MOSTLY AS A STRING
		log.warning("NetServers receiving new server data ...");
		
		String ipAddress = req.getRemoteAddr();
		
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
			log.severe("received: "+jb.toString());
			return;
		}
		
		Date currTime = new Date();
		
		String encryptedAC=res.get("accesscode").getAsString();
		
//		MessageDigest messageDigest;
//		try {
//			messageDigest = MessageDigest.getInstance("SHA-256");
//			messageDigest.update(encryptedAC.getBytes());
//			encryptedAC = new String(messageDigest.digest());
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			log.severe("some other issue. " + e.getMessage());
//		}
		
		Entity ARserver = new Entity("ATLASriftServer",ipAddress);
		ARserver.setUnindexedProperty("description", res.get("description").getAsString());
		ARserver.setUnindexedProperty("accesscode", encryptedAC);
		ARserver.setIndexedProperty("ip", ipAddress);
		ARserver.setIndexedProperty("created", currTime);
		ARserver.setIndexedProperty("clients", 0);
		ARserver.setIndexedProperty("active", true);
		ARserver.setIndexedProperty("lastheartbeat", currTime);
		datastore.put(ARserver);
		return;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		// RETRIEVES SERVERS FROM THE DATASTORE AND SENDS IT BACK AS JSON
		
		log.severe("GOT A SERVER request.");

		resp.setContentType("application/json");


		List<Entity> lTemp = new ArrayList<Entity>();
		try {
			// only servers that were alive less than 1 min ago.
			Date currTime = new Date();
			Date cutoffTime = new Date(currTime.getTime() - 60 * 60000l); 
			Filter f = new Query.FilterPredicate("lastheartbeat", FilterOperator.GREATER_THAN, cutoffTime);

			Query q = new Query("ATLASriftServer").setFilter(f);
			PreparedQuery pq = datastore.prepare(q);
			
			lTemp = pq.asList(FetchOptions.Builder.withDefaults());
		} catch (Exception e) {
			log.severe("could not load servers." + e.getMessage());
		}
		if (lTemp == null || lTemp.isEmpty()) {
			log.warning("nothing loaded...");
			return;
		}
		log.severe(lTemp.size() + " servers to load.");

		JsonObject Servers = new JsonObject();
		for (Entity ev:lTemp){
			JsonObject ServerDetails = new JsonObject();
			//ServerDetails.addProperty("ip", ev.getProperty("ip").toString());
			ServerDetails.addProperty("description", ev.getProperty("description").toString());
			ServerDetails.addProperty("clients", ev.getProperty("clients").toString());
			if (ev.hasProperty("accesscode")){
				ServerDetails.addProperty("ac", ev.getProperty("accesscode").toString());
			}
			Servers.add(ev.getProperty("ip").toString(),ServerDetails);
		}
		
		resp.getWriter().print(Servers);

	}
}
