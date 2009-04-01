/**
 * Copyright (c) 2009, University of Bristol
 * Copyright (c) 2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.crew_vre.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.crew_vre.jena.vocabulary.FOAF;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class BroadSearchController implements Controller {
	
	final static int RESULTS_PER_PAGE = 10;
	
	final static private Log log = LogFactory.getLog(BroadSearchController.class);
	final private List<Database> databases;
	final private String query;
	final private String itemQuery;
	final private GateKeeper gatekeeper;
	
	final static Map<String, Resource> typeMap;
	
	static {
		typeMap = new HashMap<String, Resource>();
		typeMap.put("people", FOAF.Person);
		typeMap.put("events", ResourceFactory.createResource("http://www.eswc2006.org/technologies/ontology#Event"));
	}
	
	public BroadSearchController(final List<Database> dbs, GateKeeper gatekeeper) {
		this.gatekeeper = gatekeeper;
		this.databases = new LinkedList<Database>();
		log.warn("Gatekeeper is: " + gatekeeper);
		// Only include databases which support LARQ querying
		for (Database db: dbs) {
			if (db.getQueryContext() != null && db.getQueryContext().isDefined(LARQ.indexKey)) databases.add(db);
			else log.warn("Database [" + db + "] does not support free text queries");
		}
		this.query = Utils.loadSparql("/sparql/broadSearch.rq");
		this.itemQuery = Utils.loadSparql("/sparql/broadSearchItem.rq");
	}
	
	public BroadSearchController(final List<Database> dbs) {
		this(dbs, null);
	}
	
	public BroadSearchController(final Database database) {
		this(Collections.singletonList(database));
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("broadSearch");
		
		String searchTerm = request.getParameter("searchTerm");
		int start = 0;
		String startNum = request.getParameter("start");
		if (startNum != null) {
			try { start = Integer.parseInt(startNum); }
			catch (NumberFormatException e) { }
		}
		Resource type = typeMap.get(request.getParameter("type"));
		Collection<Resource> allowedTypes = (type == null) ?
				typeMap.values() : Collections.singleton(type) ;
				
		List<ProcessedResult> resultsForDisplay = null;
		if (searchTerm != null && start >= 0 && searchTerm.matches(".*\\S")) {
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
						
						// Skip if user doesn't have permission for this graph
						if (gatekeeper != null && !gatekeeper.userHasPermissionFor(
							SecurityContextHolder.getContext().getAuthentication(), 
			                Permission.READ, 
			                soln.getResource("graph").getURI())) continue;
						
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
			resultsForDisplay = process(results, searchTerm, start, RESULTS_PER_PAGE, type, allowedTypes);
		} else {
			resultsForDisplay = Collections.emptyList();
		}
		
		mav.addObject("results", resultsForDisplay);
		
		int next = (resultsForDisplay.size() < RESULTS_PER_PAGE) ?
				-1 :
				start + resultsForDisplay.size() ;
		
		mav.addObject("next", next);
		if (searchTerm == null) searchTerm = "";
		mav.addObject("searchTerm", searchTerm.replace('<', '.')); // Avoid naughtiness
		mav.addObject("type", request.getParameter("type"));
		return mav;
	}
	
	private List<ProcessedResult> process(List<SimpleResult> results, String searchTerm, int start, int step, Resource type, Collection<Resource> allowedTypes) {
		if (start > results.size()) return Collections.emptyList();
		
		List<ProcessedResult> processed = new ArrayList<ProcessedResult>(step);
		
		int i = start;
		while (i < results.size() && processed.size() < step) {
			SimpleResult res = results.get(i);
			ProcessedResult result = process(res, searchTerm, type, allowedTypes);
			if (result != null) { // We have a genuine hit
				processed.add(process(res, searchTerm, type, allowedTypes));
				i++;
			}
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
				// Skip if user doesn't have permission for this graph
				if (gatekeeper != null && !gatekeeper.userHasPermissionFor(
                    SecurityContextHolder.getContext().getAuthentication(), 
                    Permission.READ, 
                    nextSoln.getResource("graph").getURI())) continue;
				
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
