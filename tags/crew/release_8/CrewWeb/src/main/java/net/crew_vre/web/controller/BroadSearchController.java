package net.crew_vre.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.crew_vre.jena.vocabulary.FOAF;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;
import org.caboto.jena.db.impl.LarqIndexedDatabase;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import edu.emory.mathcs.backport.java.util.Collections;

public class BroadSearchController implements Controller {
	
	final static private Log log = LogFactory.getLog(BroadSearchController.class);
	final private List<Database> databases;
	final private String query;
	final private String itemQuery;
	
	final static Map<String, Resource> typeMap;
	
	static {
		typeMap = new HashMap<String, Resource>();
		typeMap.put("people", FOAF.Person);
		typeMap.put("events", ResourceFactory.createResource("http://www.eswc2006.org/technologies/ontology#Event"));
	}
	
	public BroadSearchController(final List<Database> dbs) {
		this.databases = new LinkedList<Database>();
		// Only include databases which support LARQ querying
		for (Database db: dbs) {
			if (db.getQueryContext() != null && db.getQueryContext().isDefined(LARQ.indexKey)) databases.add(db);
			else log.warn("Database [" + db + "] does not support free text queries");
		}
		this.query = Utils.loadSparql("/sparql/broadSearch.rq");
		this.itemQuery = Utils.loadSparql("/sparql/broadSearchItem.rq");
	}
	
	public BroadSearchController(final Database database) {
		this(Collections.singletonList(database));
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("broadSearch");
		
		String searchTerm = request.getParameter("searchTerm");
		int page = 1;
		String pageNum = request.getParameter("page");
		if (pageNum != null) {
			try { page = Integer.parseInt(pageNum); }
			catch (NumberFormatException e) { }
		}
		Resource type = typeMap.get(request.getParameter("type"));
		Collection<Resource> allowedTypes = (type == null) ?
				typeMap.values() : Collections.singleton(type) ;
		
		int start = page - 1;
		int step = 10;
		int totalResultNum = 0;
		if (searchTerm != null && page >= 0 && searchTerm.matches(".*\\S")) {
			// THE DUMB WAY!
			List<SimpleResult> results = new ArrayList<SimpleResult>();
			for (Database db: databases) {
				QuerySolutionMap prebind = new QuerySolutionMap();
				prebind.add("searchterm", ResourceFactory.createPlainLiteral(searchTerm));
				// Note: we prebind type because it won't affect annotations currently
				if (type != null) prebind.add("type", type);
				Results reses = db.executeSelectQuery(query, prebind);
				try {
					SimpleResult currentReses = null;
					ResultSet res = reses.getResults();
					while (res.hasNext()) {
						QuerySolution soln = res.nextSolution();
						
						RDFNode theType = soln.get("type");
						// Skip rows of wrong type. We expand types, so rows multiply
						if (theType != null && !allowedTypes.contains(theType)) continue;
						
						Resource subj = soln.getResource("s");
						if (currentReses == null || !currentReses.subject.equals(subj)) {
							currentReses = new SimpleResult(subj);
							results.add(currentReses);
						}
						for (String var: (List<String>) res.getResultVars())
							if (soln.get(var) != null) currentReses.properties.put(var, soln.get(var));
					}
				} finally {
					reses.close();
				}
			}
			
			Collections.sort(results);
			totalResultNum = results.size();
			List<ProcessedResult> completeResults = process(results, searchTerm, start, step, type, allowedTypes);
			mav.addObject("results", completeResults);
		} else {
			mav.addObject("results", Collections.emptyList());
		}
		List<Integer> pages = new ArrayList<Integer>();
		int nextPage = ((page+1) * step < totalResultNum) ?  page + 1 : -1;
		for (int i = 1; i < 10 && (i * step) < totalResultNum; i++) pages.add(i);
		mav.addObject("currentPage", page);
		mav.addObject("pages", pages);
		mav.addObject("nextPage", nextPage);
		if (searchTerm == null) searchTerm = "";
		mav.addObject("searchTerm", searchTerm.replace('<', '.')); // Avoid naughtiness
		mav.addObject("type", request.getParameter("type"));
		return mav;
	}
	
	private List<ProcessedResult> process(List<SimpleResult> results, String searchTerm, int start, int step, Resource type, Collection<Resource> allowedTypes) {
		if (start > results.size()) return Collections.emptyList();
		if (start + step > results.size()) step = results.size() - start;
		
		List<ProcessedResult> processed = new ArrayList<ProcessedResult>(step);
		
		for (int i = start; i < start + step; i++) {
			SimpleResult res = results.get(i);
			ProcessedResult result = process(res, searchTerm, type, allowedTypes);
			// TODO this makes paging wonky. Could not increment i in the null case
			if (result != null) processed.add(process(res, searchTerm, type, allowedTypes));
		}
		
		return processed;
	}
	
	/**
	 * Dig through the result and coerce into a form suitable for rendering
	 * @param res
	 * @param allowedTypes 
	 * @param type 
	 * @return
	 */
	private ProcessedResult process(SimpleResult res, String searchTerm, Resource type, Collection<Resource> allowedTypes) {
		String label = "[unknown]";
		String description = null;
		String itemId = null;
		String itemType = null;
		String annoAuthor = null;
		String annoTitle = null;
		String annoDesc = null;
		String recordingId = null;
		if (res.properties.containsKey("target")) { // an annotation on something
			// Get info about the target
			itemId = getValue("target", res.properties);
			String[] details = getDetailsForItem(res.properties.get("target"), type, allowedTypes);
			if (details[0] == null) return null; // Not the kind of thing we want
			label = details[0];
			description = details[1];
			itemType = details[2];
			recordingId = details[3];
			
			// Annotation bits;
			annoTitle = getValue("title", res.properties);
			annoDesc = getValue("desc", res.properties);
			annoAuthor = getValue("author", res.properties);
		} else {
			itemId = res.subject.getURI();
			if (res.properties.containsKey("title")) label = getValue("title", res.properties);
			else label = getValue("given_name", res.properties) + " " + 
				getValue("family_name", res.properties);
			description = getValue("desc", res.properties);
			itemType = getValue("type", res.properties);
			recordingId = getValue("recordingId", res.properties);
		}
		
		ProcessedResult result = new ProcessedResult(searchTerm, itemId, label, description, itemType, recordingId);
		if (res.properties.containsKey("target")) {
			result.isAnnotated = true;
			result.annoTitle = annoTitle;
			result.annoDesc = annoDesc;
			result.annoAuthor = annoAuthor;
		}
		return result;
	}
	
	private String[] getDetailsForItem(RDFNode node, Resource type, Collection<Resource> allowedTypes) {
		String[] fields = {null,null,null,null};
		QuerySolutionMap prebind = new QuerySolutionMap();
		prebind.add("s", node);
		if (type != null) prebind.add("type", type);
		// TODO: we take the first database as the 'core data' db
		Results reses = databases.get(0).executeSelectQuery(itemQuery, prebind);
		try {
			ResultSet r = reses.getResults();
			
			while (r.hasNext()) {
				QuerySolution nextSoln = r.nextSolution();
				RDFNode rType = nextSoln.get("type");
				if (!allowedTypes.contains(rType)) continue;
				if (nextSoln.get("title") != null) {
					fields[0] = getValue(nextSoln.get("title"));
					fields[1] = getValue(nextSoln.get("desc"));
					fields[3] = getValue(nextSoln.get("recordingId"));
				} else {
					fields[0] = getValue(nextSoln.get("given_name")) + " " +
						getValue(nextSoln.get("family_name"));
				}
				fields[2] = getValue(rType);
			}
		} finally {
			reses.close();
		}
		return fields;
	}

	private String getValue(String prop, Map<String, RDFNode> map) {
		return getValue(map.get(prop));
	}
	
	private String getValue(RDFNode node) {
		if (node == null) return null;
		if (node.isLiteral()) return ((Literal) node).getLexicalForm();
		else if (node.isURIResource()) return ((Resource) node).getURI();
		else return "[blank]";
	}
	
	private static class SimpleResult implements Comparable<SimpleResult> {
		final Resource subject; final Map<String, RDFNode> properties;
		
		public SimpleResult(Resource subject) { 
			this.subject = subject;
			this.properties = new HashMap<String, RDFNode>();
		}

		public int compareTo(SimpleResult arg0) {
			return subject.getURI().compareTo(arg0.subject.getURI());
		}
		
	}
	
	/**
	 * A collection of fields and a little logic to make rendering pleasant
	 * 
	 * @author pldms
	 *
	 */
	public static class ProcessedResult {
		final public String id, label, description, type;
		public boolean isAnnotated = false;
		public String annoTitle, annoDesc, annoAuthor;
		private Pattern searchPattern;
		private String highlighted;
		private String recordingId;
		
		public ProcessedResult(String searchTerm, String id, String label, String description, String type, String recordingId) {
			this.id = id;
			this.label = label;
			this.type = type;
			this.description = description;
			this.recordingId = recordingId;
			this.searchPattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
			this.highlighted = "<span class=\"search-hit\">" + searchTerm.toUpperCase() + "</span>";
		}
		
		public String getId() { return id; }
		public String getLabel() { return highlight(label); }
		public String getDescription() { return highlight(description); }
		public String getType() { return type; }
		public String getAnnoTitle() { return highlight(annoTitle); }
		public String getAnnoDescription() { return highlight(annoDesc); }
		public boolean getIsAnnotated() { return isAnnotated; }
		public boolean getHasRecording() { return recordingId != null; }
		
		final private String highlight(String string) {
			if (string == null) return string;
			return searchPattern.matcher(string).replaceAll(highlighted);
		}
		
		public String getLink() throws UnsupportedEncodingException {
			String target = null;
			if ("http://xmlns.com/foaf/0.1/Person".equals(type))
				target="./displayPerson.do?personId=";
			else
				target="./displayEvent.do?eventId=";
			return target + URLEncoder.encode(id, "UTF-8");
		}
		
		public String getRecordingLink() throws UnsupportedEncodingException {
			return "./displayRecording.do?recordingId=" + recordingId +
			"&eventId=" + URLEncoder.encode(id, "UTF-8");
		}
		
		public String getAnnoAuthor() {
			String label = annoAuthor;
			if (label.endsWith("/")) label = label.substring(0,label.length() - 1);
			label = label.substring(label.lastIndexOf('/') + 1);
			return label;
		}
	}

}
