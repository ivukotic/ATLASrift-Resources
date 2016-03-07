package edu.uchicago.ATLASrift;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

@SuppressWarnings("serial")
public class KeepAlive extends HttpServlet {

	private static final Logger log = Logger.getLogger(KeepAlive.class.getName());
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		// finds the server. Update its lastheartbeat with the current datetime
		String ipAddress = req.getRemoteAddr();
		log.info("GOT A heartbeat from: "+ipAddress);
		String clients = req.getParameter("clients");
		Date currTime = new Date();
		
		Entity res = null;
		try {
			Filter f1 = new Query.FilterPredicate("ip", FilterOperator.EQUAL, ipAddress);
		//	Filter f2 = new Query.FilterPredicate("active", FilterOperator.EQUAL, true);
		//	Filter cF = CompositeFilterOperator.and(f1, f2);
			Query q = new Query("ATLASriftServer").setFilter(f1);
			PreparedQuery pq = datastore.prepare(q);
			res = pq.asSingleEntity();
		}catch (TooManyResultsException e){
			log.severe("found multiple active servers with the same IP.");
		} catch (Exception e) {
			log.severe("could not load servers." + e.getMessage());
		}
		if (res == null) {
			log.severe("could not find an active server with that IP to heartbeat...");
			return;
		}
		res.setIndexedProperty("lastheartbeat", currTime);
		res.setIndexedProperty("active", true);
		
		if (clients != null){
			try{
				res.setUnindexedProperty("clients", Integer.parseInt(clients));
			}catch(NumberFormatException e){
				log.severe("could not parse number of clients.");
			}
		}
		else {
			res.setUnindexedProperty("clients", 0);
			}
		datastore.put(res);
		
	}
}
