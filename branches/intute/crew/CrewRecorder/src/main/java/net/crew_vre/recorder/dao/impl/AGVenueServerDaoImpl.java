/**
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
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

package net.crew_vre.recorder.dao.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.recorder.dao.AGVenueServerDao;
import net.crew_vre.recorder.domain.VenueServer;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import ag3.interfaces.types.ConnectionDescription;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * An implementation of the AGVenueServer DAO
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AGVenueServerDaoImpl implements AGVenueServerDao {

    private final String findVenueServersSparql;

    private final String findVenueServerSparql;

    private Database database = null;

    /**
     * Creates a new AGVenueServerDaoImpl
     * @param database The database to connect to
     */
    public AGVenueServerDaoImpl(Database database) {
        this.database = database;
        findVenueServersSparql = Utils.loadSparql(
                "/sparql/findVenueServers.rq");
        findVenueServerSparql = Utils.loadSparql(
        "/sparql/findVenueServer.rq");
    }

    private void addModelForServer(Model model, VenueServer server) {
        Resource resource = model.createResource(server.getUri());
        resource.addProperty(RDF.type, Crew.TYPE_VENUE_SERVER);
        List<ConnectionDescription> venues = server.getVenues();
        Iterator<ConnectionDescription> iter = venues.iterator();
        while (iter.hasNext()) {
            ConnectionDescription venue = iter.next();
            Resource venueResource = model.createResource(venue.getUri());
            venueResource.addProperty(RDF.type, Crew.TYPE_VENUE);
            venueResource.addProperty(DC.title, venue.getName());
            venueResource.addProperty(DC.description, venue.getDescription());
            resource.addProperty(Crew.HAS_VENUE, venueResource);
        }
    }

    /**
     *
     * @see net.crew_vre.recorder.dao.AGVenueServerDao#addVenueServer(
     *     net.crew_vre.recorder.domain.VenueServer)
     */
    public void addVenueServer(VenueServer venueServer) {
        Model model = database.getUpdateModel();
        addModelForServer(model, venueServer);
        database.addModel(null, model);
    }

    /**
     *
     * @see net.crew_vre.recorder.dao.AGVenueServerDao#deleteVenueServer(
     *     java.lang.String)
     */
    public void deleteVenueServer(String uri) {
        VenueServer server = findVenueServer(uri);
        if (server != null) {
            Model model = database.getUpdateModel();
            addModelForServer(model, server);
            database.deleteModel(null, model);
        }
    }

    /**
     *
     * @see net.crew_vre.recorder.dao.AGVenueServerDao#findVenueServer(
     *     java.lang.String)
     */
    public VenueServer findVenueServer(String uri) {
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("venueServer",
                ModelFactory.createDefaultModel().createResource(uri));
        Results res = database.executeSelectQuery(findVenueServerSparql,
                initialBindings);
        ResultSet results = res.getResults();
        Vector<ConnectionDescription> venues =
            new Vector<ConnectionDescription>();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            ConnectionDescription venue = new ConnectionDescription();
            venue.setUri(solution.getResource("uri").getURI());
            venue.setName(solution.getLiteral("name").getString());
            venue.setDescription(
                    solution.getLiteral("description").getString());
            venues.add(venue);
        }
        res.close();
        if (venues.size() > 0) {
            VenueServer server = new VenueServer();
            server.setUri(uri);
            server.setVenues(venues);
            return server;
        }
        return null;
    }

    /**
     *
     * @see net.crew_vre.recorder.dao.AGVenueServerDao#findVenueServers()
     */
    public List<String> findVenueServers() {
        Vector<String> uris = new Vector<String>();
        Results res = database.executeSelectQuery(findVenueServersSparql,
                null);
        ResultSet results = res.getResults();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            Resource uri = solution.getResource("uri");
            uris.add(uri.getURI());
        }
        res.close();
        return uris;
    }
}
