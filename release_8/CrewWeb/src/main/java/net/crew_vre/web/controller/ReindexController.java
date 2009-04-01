package net.crew_vre.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.LarqIndexedDatabase;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.emory.mathcs.backport.java.util.Collections;

public class ReindexController implements Controller {
	
	final static private Log log = LogFactory.getLog(ReindexController.class);
	final private List<Database> databases;
	
	public ReindexController(final List<Database> databases) {
		this.databases = databases;
	}
	
	public ReindexController(final Database database) {
		this(Collections.singletonList(database));
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("reindex");
		boolean indexingAttempted = false;
		int errors = 0;
		int attempted = 0;
		if ("POST".equals(request.getMethod())) {
			// Have to consider doing this asynchronously in future
			log.info("Reindex requested");
			indexingAttempted = true;
			long start = System.currentTimeMillis();
			synchronized(databases) {
				for (Database database: databases) {
					if (database instanceof LarqIndexedDatabase) {
						attempted++;
						try { ((LarqIndexedDatabase) database).reindex(); }
						catch (Throwable e) { log.error("Error reindexing " + database, e); errors++; }
					} else {
						log.warn("Not indexed: " + database);
					}
				}
			}
			log.info("Reindex completed. (" + (System.currentTimeMillis() - start) + "ms)");
		}
		mav.addObject("indexRan", indexingAttempted);
		mav.addObject("attempted", attempted);
		mav.addObject("errors", errors);
		return mav;
	}
	
	
	
}
