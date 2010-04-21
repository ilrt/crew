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
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import net.crew_vre.events.dao.StartPointDao;
import net.crew_vre.events.domain.StartPoint;

import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>This is an implementation of <code>StartPointDao</code> that uses the Jena
 * Sematic Web Framework. SPARQL is used for querying the underlying RDF model.</p>
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class StartPointDaoImpl implements StartPointDao {


    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param database The database to query
     */
    public StartPointDaoImpl(final Database database) {
        this.database = database;
        sparqFindStartPointsByEventId = Utils.loadSparql("/sparql/startpoints-by-eventid.rq");
        sparqFindStartPointById = Utils.loadSparql("/sparql/startpoint-by-id.rq");
    }


    /**
     * <p>Find the details for a specific place.</p>
     *
     * @param id the identifier of a place
     * @return a place identified by the id
     */
    public List<StartPoint> findStartPointsByEventId(final String EventId) {

        List<StartPoint> startPoints = new ArrayList<StartPoint>();

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(EventId));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find startpoint details:");
            logger.debug(sparqFindStartPointsByEventId);
        }

        Results res = database.executeSelectQuery(sparqFindStartPointsByEventId,
                initialBindings);
        ResultSet rs = res.getResults();

        // there may be multiple start points
        while (rs.hasNext()) {
            StartPoint startPoint = getStartPointDetails(rs.nextSolution());
            if (startPoint != null) {
                logger.debug("Found startPoint: " + startPoint.getTitle());
                startPoints.add(startPoint);
            }
        }

        res.close();

        return startPoints;
    }

    public StartPoint findStartPointById(final String StartPointId) {
        StartPoint startPoint = null;

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("startpoint", ModelFactory.createDefaultModel().createResource(StartPointId));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find startpoint details:");
            logger.debug(sparqFindStartPointById);
        }

        Results res = database.executeSelectQuery(sparqFindStartPointById,
                initialBindings);
        ResultSet rs = res.getResults();

        // should only be one
        while (rs.hasNext()) {
            startPoint = getStartPointDetails(rs.nextSolution());
            if (startPoint != null) {
                logger.debug("Found startPoint: " + startPoint.getTitle());
            }
        }

        res.close();
        return startPoint;
    }


    /**
     * <p>Get the details of a startpoint and create a <code>StartPoint</code> object.</p>
     *
     * @param qs the Jena query solution with the results of a sparql query
     * @return a StartPoint
     */
    private StartPoint getStartPointDetails(final QuerySolution qs) {

        StartPoint startPoint = new StartPoint();

        if (qs.getResource("startPoint") != null) {
            Resource resource = qs.getResource("startPoint");
            startPoint.setId(resource.getURI());
        }

        if (qs.getResource("graph") != null) {
            startPoint.setGraph(qs.getResource("graph").getURI());
        }

        if (qs.getLiteral("name") != null) {
            startPoint.setTitle(qs.getLiteral("name").getLexicalForm());
        }

        if (qs.getLiteral("longitude") != null && !qs.getLiteral("longitude").getLexicalForm().equals("")) {
            startPoint.setLongitude(Float.valueOf(qs.getLiteral("longitude").getLexicalForm()));
        }

        if (qs.getLiteral("latitude") != null && !qs.getLiteral("latitude").getLexicalForm().equals("")) {
            startPoint.setLatitude(Float.valueOf(qs.getLiteral("latitude").getLexicalForm()));
        }

        return startPoint;
    }


    /**
     * SPARQL to find a stratPoints
     */
    private String sparqFindStartPointsByEventId;
    private String sparqFindStartPointById;

    // Logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * The database that is queried
     */
    private final Database database;
}
