package edu.uchicago.ATLASrift;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

@SuppressWarnings("serial")
public class EventServer extends HttpServlet {

	private static final Logger log = Logger.getLogger(EventServer.class.getName());
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		// GETS AN EVENT AS JSON AND STORES IT IN THE DATASTORE MOSTLY AS A STRING
		log.warning("EventServer receiving event data ...");
		
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

		Entity ARevent = new Entity("ATLASriftEvent");
		ARevent.setIndexedProperty("eventid", res.get("eventid").getAsInt());
		ARevent.setIndexedProperty("runnr", res.get("runnr").getAsInt());
		ARevent.setIndexedProperty("eventnr", res.get("eventnr").getAsInt());
		ARevent.setUnindexedProperty("description", res.get("description").getAsString());
		Text t=new Text(res.get("xAOD::Type::TrackParticle").toString());
		ARevent.setUnindexedProperty("xAOD::Type::TrackParticle", t);
		Text t1=new Text(res.get("xAOD::Type::Jet").toString());
		ARevent.setUnindexedProperty("xAOD::Type::Jet", t1);
		Text t2=new Text(res.get("xAOD::Type::CaloCluster").toString());
		ARevent.setUnindexedProperty("xAOD::Type::CaloCluster", t2);
		datastore.put(ARevent);
		return;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		// RETRIEVES EVENT FROM THE DATASTORE AND SENDS IT BACK AS JSON
		
		log.info("GOT AN EVEN request.");

		resp.setContentType("application/json");


		String EID = req.getParameter("eventid");
		if (EID != null) {
			log.warning("Requesting event ID: " + EID);
		}else{
			log.severe("no eventid given. Aborting.");
			return;
		}

		List<Entity> lTemp = new ArrayList<Entity>();
		try {
			int ei=Integer.parseInt(EID);
			Filter f = new Query.FilterPredicate("eventid", FilterOperator.EQUAL, ei);
			Query q = new Query("ATLASriftEvent").setFilter(f);
			PreparedQuery pq = datastore.prepare(q);
			lTemp = pq.asList(FetchOptions.Builder.withDefaults());
		} catch (Exception e) {
			log.severe("could not reload info on events." + e.getMessage());
		}
		if (lTemp == null || lTemp.isEmpty()) {
			log.severe("nothing loaded...");
			return;
		}
		log.info(lTemp.size() + " events to load.");
		
		JsonObject Event = new JsonObject();
		Entity ev=lTemp.get(0);
		Event.addProperty("runnr", Integer.parseInt((ev.getProperty("runnr")).toString()));
		Event.addProperty("eventnr", Integer.parseInt((ev.getProperty("eventnr")).toString()));
		Event.addProperty("description", (ev.getProperty("description")).toString());

		JsonParser jp = new JsonParser();
		
		String tp= ev.getProperty("xAOD::Type::TrackParticle").toString();
		JsonObject Jtp = null;
		try {
			JsonElement root = jp.parse(tp);
			Jtp = root.getAsJsonObject();
		} catch (Exception e) {
			log.severe("could not parse to json");
			return;
		}
		Event.add("xAOD::Type::TrackParticle", Jtp);

		String j= ev.getProperty("xAOD::Type::Jet").toString();
		JsonObject Jj = null;
		try {
			JsonElement root = jp.parse(j);
			Jj = root.getAsJsonObject();
		} catch (Exception e) {
			log.severe("could not parse to json");
			return;
		}
		Event.add("xAOD::Type::Jet", Jj);

		String cc= ev.getProperty("xAOD::Type::CaloCluster").toString();
		JsonObject Jcc = null;
		try {
			JsonElement root = jp.parse(cc);
			Jcc = root.getAsJsonObject();
		} catch (Exception e) {
			log.severe("could not parse to json");
			return;
		}
		Event.add("xAOD::Type::CaloCluster", Jcc);
		
		resp.getWriter().print(Event);

	}
}
