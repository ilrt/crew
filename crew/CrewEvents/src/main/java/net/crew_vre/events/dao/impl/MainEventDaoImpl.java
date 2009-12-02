/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
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
package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import net.crew_vre.events.Utility;
import net.crew_vre.events.dao.LocationDao;
import net.crew_vre.events.dao.MainEventDao;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.events.domain.PlacePart;
import net.crew_vre.events.domain.Subject;
import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.jena.vocabulary.ESWC2006;
import net.crew_vre.jena.vocabulary.IUGO;

import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is an implementation of <code>MainEventDao</code> that uses the Jena
 * Sematic Web Framework. SPARQL is used for querying the underlying RDF model.</p>
 * <p/>
 * <p>This DAO finds "main" events. In the ESWC2006 schema there are many different types of
 * events - conferences, workshops, tracks, sessions, talks, tea breaks etc. An event can be
 * constructed of other events. For example, a conference might have tracks, the tracks might
 * have sessions and the sessions might have keynote talks or presentations. The Iugo schema
 * has the concept of the "main" event, i.e. the parent event that all other events belong.
 * In the example above, this would be the conference.</p>
 * <p/>
 * <p>The RDF might look like:</p>
 * <p/>
 * <pre><code>
 * &lt;rdf:Description rdf:about="http://foo.com/1"&gt;
 *     &lt;rdf:type rdf:resource="http://www.eswc2006.org/technologies/ontology#WorkshopEvent"/&gt;
 *     &lt;rdf:type rdf:resource="http://www.ilrt.bristol.ac.uk/iugo#MainEvent"/&gt;
 * </code></pre>
 * <p/>
 * <p>The DAO just finds basic information for an event - just the information that is
 * needed in a list of search results. The EventDao is used to find all information related
 * to an event. Both DAOs store information about an event in the Event bean.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: MainEventDaoImpl.java 1188 2009-03-31 13:09:20Z cmmaj $
 * @see net.crew_vre.events.dao.impl.EventDaoImpl
 */
public class MainEventDaoImpl implements MainEventDao {


    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param database    The database to query
     * @param locationDao DAO that provides access to location details
     */
    public MainEventDaoImpl(final Database database,
                            final LocationDao locationDao) {
        this.database = database;
        this.locationDao = locationDao;

        this.sparqlEvents = Utils.loadSparql("/sparql/mainevent-fragment.rq");
    }


    // --- PUBLIC METHODS .... these implement the interface

    /**
     * <p>Return the details of the event that matches an identifier.</p>
     *
     * @param id the identifier of an event
     * @return an event that mathes the identifier
     */
    public EventPart findEventById(final String id) {

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(id));
        List<EventPart> results = findEvents(createSparql(), initialBindings);

        if (results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public List<EventPart> findAllEvents() {
        return findEvents(createSparql());
    }

    public List<EventPart> findAllEvents(final int limit, final int offset) {
        return findEvents(createSparql(limit, offset));
    }

    public List<EventPart> findEventsByDate(final LocalDate startDate, final LocalDate endDate) {
        return findEvents(createSparql(createDateFilter(dateType, startDate.toString(),
                "$startDate", endDate.toString(), "$endDate")));
    }

    public List<EventPart> findEventsByDate(final LocalDate startDate, final LocalDate endDate,
                                        final int limit, final int offset) {
        return findEvents(createSparql(createDateFilter(dateType, startDate.toString(),
                "$startDate", endDate.toString(), "$endDate"), limit, offset));
    }

    public List<EventPart> findEventsWithConstraint(String constraint) {
        return findEvents(createConstraint(constraint));
    }

    public List<EventPart> findEventsWithConstraint(String constraint, int limit, int offset) {
        return findEvents(createConstraint(constraint, limit, offset));
    }

    public List<EventPart> findEventsByCreationDate(final DateTime startDate,
                                                final DateTime endDate) {
        return findEvents(createSparql("$id dc:creation-date $creationDate .",
                createDateFilter(dateTimeType, startDate.toString(),
                "$creationDate", endDate.toString(), "$creationDate")));
    }

    public List<EventPart> findEventsByCreationDate(DateTime startDate, DateTime endDate,
                                                int limit, int offset) {
        return findEvents(createSparql("$id dc:creation-date $creationDate .",
                createDateFilter(dateTimeType, startDate.toString(),
                "$creationDate", endDate.toString(), "$creationDate"), limit, offset));
    }


    // --- PRIVATE METHODS


    private List<EventPart> findEvents(final String sparql) {
        return findEvents(sparql, null);
    }

    private List<EventPart> findEvents(final String sparql, final QuerySolutionMap initialBindings) {

        // list to hold the results
        List<EventPart> results = new ArrayList<EventPart>();

        // show sparql
        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find event details:");
            logger.debug(sparql);
        }

        Results res = database.executeSelectQuery(sparql,
                initialBindings);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {
            QuerySolution qs = rs.nextSolution();
            EventPart event = getEventDetails(qs);
            results.add(event);
        }

        res.close();

        return results;
    }

    private EventPart getEventDetails(final QuerySolution qs) {

        // create an event object and make the URI the id
        EventPart event = new EventPart();
        Resource r = qs.getResource("id");
        event.setGraph(qs.getResource("graph").getURI());
        event.setId(r.getURI());
        event.setTitle(qs.getLiteral("title").getLexicalForm());

        // add the start and end dates times
        DateTime startDateTime = null;
        DateTime endDateTime = null;

        if (qs.getLiteral("startDateTime") != null) {
            try {
                startDateTime =
                        Utility.parseStringToDateTime(qs.getLiteral("startDate")
                                .getLexicalForm());
                endDateTime =
                        Utility.parseStringToDateTime(qs.getLiteral("endDate")
                                .getLexicalForm());
            } catch (ParseException e) {
                logger.error(e.getMessage());
            }

            event.setStartDateTime(startDateTime);
            event.setEndDateTime(endDateTime);
        }

        // add the start and end dates - needed for Intute data
        if (qs.getLiteral("startDate") != null) {
            LocalDate startDate = null;
            LocalDate endDate = null;

            try {
                startDate =
                        Utility.parseStringToLocalDate(qs.getLiteral("startDate").getLexicalForm());
                endDate = Utility.parseStringToLocalDate(qs.getLiteral("endDate").getLexicalForm());
            } catch (ParseException e) {
                logger.error(e.getMessage());
            }

            event.setStartDate(startDate);
            event.setEndDate(endDate);
        }

        if (qs.getLiteral("description") != null) {
            event.setDescription(qs.getLiteral("description").getLexicalForm());
        }


        // add the location details -> skos taxonomy
        //event.setLocations(locationDao.findLocationByEvent(event.getId()));

        return event;
    }

    private String createSparql() {
        return createSparql(null);
    }

    // Variant so we can insert something before filter (pldms 2009)
    private String createSparql(final String clause, final String filter) {

        StringBuffer buffer = new StringBuffer(sparqlEvents);

        if (clause != null) { // Insert before first optional
            int x = buffer.indexOf("OPTIONAL");
            buffer.insert(x, clause + "\n\t");
        }

        if (filter != null) {
            buffer.append("FILTER (").append(filter).append(")");
        }

        buffer.append("} }");
        return buffer.toString();
    }

    private String createSparql(final String filter) {

        StringBuffer buffer = new StringBuffer(sparqlEvents);

        if (filter != null) {
            buffer.append("FILTER (").append(filter).append(")");
        }

        buffer.append("} }");

        return buffer.toString();
    }

    private String createConstraint(final String constraint) {

        StringBuffer buffer = new StringBuffer(sparqlEvents)
                .append(constraint)
                .append("}\n}\n")
                .append("ORDER BY DESC(?startDate)\n");

        return buffer.toString();
    }

    private String createConstraint(final String constraint, final int limit, final int offset) {

        StringBuffer buffer = new StringBuffer(createConstraint(constraint))
                .append("LIMIT ").append(limit)
                .append("\nOFFSET ").append(offset);

        return buffer.toString();
    }

    private String createSparql(final int limit, final int offset) {
        return createSparql(null, limit, offset);
    }

    private String createSparql(final String filter, final int limit, final int offset) {

        StringBuffer buffer = new StringBuffer(createSparql(filter));
        buffer.append("\nLIMIT ").append(limit);
        buffer.append("\nOFFSET ").append(offset);

        return buffer.toString();
    }

    // Variant so we can insert something before filter (pldms 2009)
    private String createSparql(final String clause, final String filter, final int limit, final int offset) {

        StringBuffer buffer = new StringBuffer(createSparql(clause, filter));
        buffer.append("\nLIMIT ").append(limit);
        buffer.append("\nOFFSET ").append(offset);

        return buffer.toString();
    }

    private String createDateFilter(final String dateType, final String startDateVal,
                                        final String startDateVar, final String endDateVal,
                                        final String endDateVar) {

        StringBuffer buffer = new StringBuffer("");

        if (startDateVal != null) {
            buffer.append(dateType).append("(").append(startDateVar)
                    .append(") >= ").append(dateType).append("(\"")
                    .append(startDateVal).append("\")");
        }

        if (endDateVal != null) {
            if (startDateVal != null) {
                buffer.append(" && ");
            }

            buffer.append(dateType).append("(").append(endDateVar)
                    .append(") <= ").append(dateType).append("(\"")
                    .append(endDateVal).append("\")");
        }

        return buffer.toString();
    }


    final private String dateType = "xsd:date";

    final private String dateTimeType = "xsd:dateTime";

    // sparql for the main event details
    private String sparqlEvents;

    // logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * DAO to find location details.
     */
    private LocationDao locationDao;

    /**
     * The database that is queried
     */
    private final Database database;
}
