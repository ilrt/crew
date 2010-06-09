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

import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import java.util.List;
import java.util.ArrayList;
import net.crew_vre.events.dao.KmlObjectDao;
import net.crew_vre.events.domain.KmlObject;

/**
 * <p>This is an implementation of <code>StartPointDao</code> that uses the Jena
 * Sematic Web Framework. SPARQL is used for querying the underlying RDF model.</p>
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */

public class KmlObjectDaoImpl implements KmlObjectDao {


    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param database The database to query
     */
    public KmlObjectDaoImpl(final Database database) {
        this.database = database;
        sparqFindKmlObjectsByEventId = Utils.loadSparql("/sparql/kmlobjects-by-eventid.rq");
        sparqFindKmlObjectById = Utils.loadSparql("/sparql/kmlobject-by-id.rq");
    }


    /**
     * <p>Find KML route details for a specific place.</p>
     *
     * @param EventId the identifier of a place
     * @return a list of KML objects identified by the id
     */
    public List<KmlObject> findKmlObjectsByEventId(final String EventId) {

        List<KmlObject> kmlObjects = new ArrayList<KmlObject>();

        // create the bindings
        QuerySolutionMap findKmlObjectInitialBindings = new QuerySolutionMap();
        findKmlObjectInitialBindings.add("id", ModelFactory.createDefaultModel().createResource(EventId));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find KML details:");
            logger.debug(sparqFindKmlObjectsByEventId);
        }

        Results kml_res = database.executeSelectQuery(sparqFindKmlObjectsByEventId,
                findKmlObjectInitialBindings);
        ResultSet kml_rs = kml_res.getResults();

        // there may be multiple kml objects
        KmlObject kml = null;
        while (kml_rs.hasNext()) {
            if (logger.isDebugEnabled())
                logger.debug("Got result ...");
            kml = getKmlObjectDetails(kml_rs.nextSolution());
            if (kml != null) {
                if (logger.isDebugEnabled())
                    logger.debug("Found kml object (from eventId): " + kml.getId() + " : " + kml.getTitle());
                kmlObjects.add(kml);
            }
        }
        kml_res.close();

        return kmlObjects;
    }

    public KmlObject findKmlObjectById(final String kmlId) {
        KmlObject kml = new KmlObject();
        kml.setId(kmlId);

        // create the bindings
        QuerySolutionMap findKmlObjectInitialBindings = new QuerySolutionMap();
        findKmlObjectInitialBindings.add("kmlId", ModelFactory.createDefaultModel().createResource(kmlId));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find kml object details:");
            logger.debug(sparqFindKmlObjectById);
        }

        Results res = database.executeSelectQuery(sparqFindKmlObjectById, findKmlObjectInitialBindings);
        ResultSet rs = res.getResults();

        // should only be one kml object
        while (rs.hasNext()) {
            
            QuerySolution qs = rs.nextSolution();

            if (qs.getResource("graph") != null) {
                kml.setGraph(qs.getResource("graph").getURI());
            }
            if (qs.getLiteral("title") != null) {
                kml.setTitle(qs.getLiteral("title").getLexicalForm());
            }
            if (qs.getLiteral("type") != null) {
                kml.setType(qs.getLiteral("type").getLexicalForm());
            }
            if (kml != null) {
                logger.debug("Found kml object: " + kml.getTitle());
            }
        }

        res.close();
        return kml;
    }


    /**
     * <p>Get the details of a kml object and create a <code>KmlObject</code> object.</p>
     *
     * @param qs the Jena query solution with the results of a sparql query
     * @return a KmlObject
     */
    private KmlObject getKmlObjectDetails(final QuerySolution qs) {

        KmlObject kml = new KmlObject();

        if (qs.getResource("kmlId") != null) {
            Resource resource = qs.getResource("kmlId");
            kml.setId(resource.getURI());
        }

        if (qs.getResource("graph") != null) {
            kml.setGraph(qs.getResource("graph").getURI());
        }

        if (qs.getLiteral("title") != null) {
            kml.setTitle(qs.getLiteral("title").getLexicalForm());
        }

        if (qs.getLiteral("type") != null) {
            kml.setType(qs.getLiteral("type").getLexicalForm());
        }

        return kml;
    }


    /**
     * SPARQL to find a stratPoints
     */
    private String sparqFindKmlObjectsByEventId;
    private String sparqFindKmlObjectById;

    // Logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * The database that is queried
     */
    private final Database database;
}
