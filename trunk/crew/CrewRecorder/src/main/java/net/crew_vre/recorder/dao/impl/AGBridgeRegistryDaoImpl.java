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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import ag3.interfaces.types.BridgeDescription;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.recorder.dao.AGBridgeRegistryDao;
import net.crew_vre.recorder.domain.BridgeRegistry;

/**
 * An implementation of a DAO for AG Bridge Registries
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AGBridgeRegistryDaoImpl implements AGBridgeRegistryDao {

    private final String findRegistriesSparql;

    private final String findRegistrySparql;

    private Database database = null;

    /**
     * Creates a new DAO
     * @param database The database to connect to
     */
    public AGBridgeRegistryDaoImpl(Database database) {
        this.database = database;
        findRegistriesSparql = Utils.loadSparql(
                "/sparql/findBridgeRegistries.rq");
        findRegistrySparql = Utils.loadSparql(
                "/sparql/findBridgeRegistry.rq");
    }

    private void addModelForRegistry(Model model, BridgeRegistry registry) {
        Resource resource = model.createResource(registry.getUri());
        resource.addProperty(RDF.type, Crew.TYPE_BRIDGE_REGISTRY);
        List<BridgeDescription> bridges = registry.getBridges();
        Iterator<BridgeDescription> iter = bridges.iterator();
        while (iter.hasNext()) {
            BridgeDescription bridge = iter.next();
            String uri = "http://" + bridge.getHost() + ":" + bridge.getPort()
                + "/";
            Resource bridgeResource = model.createResource(uri);
            bridgeResource.addProperty(RDF.type, Crew.TYPE_BRIDGE);
            bridgeResource.addProperty(DC.title, bridge.getName());
            bridgeResource.addProperty(DC.description, bridge.getDescription());
            bridgeResource.addProperty(DC.identifier, bridge.getGuid());
            bridgeResource.addProperty(DC.type, bridge.getServerType());
            resource.addProperty(Crew.HAS_BRIDGE, bridgeResource);
        }
    }

    /**
     *
     * @see net.crew_vre.recorder.dao.AGBridgeRegistryDao#addBridgeRegistry(
     *     net.crew_vre.recorder.domain.BridgeRegistry)
     */
    public void addBridgeRegistry(BridgeRegistry registry) {
        Model model = database.getUpdateModel();
        addModelForRegistry(model, registry);
        database.addModel(null, model);
    }

    /**
     *
     * @see net.crew_vre.recorder.dao.AGBridgeRegistryDao#findBridgeRegistries()
     */
    public List<String> findBridgeRegistries() {
        Vector<String> uris = new Vector<String>();
        Results res = database.executeSelectQuery(findRegistriesSparql,
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

    /**
     *
     * @see net.crew_vre.recorder.dao.AGBridgeRegistryDao#findBridgeRegistry(
     *     java.lang.String)
     */
    public BridgeRegistry findBridgeRegistry(String uri) {
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("bridgeRegistry",
                ModelFactory.createDefaultModel().createResource(uri));
        Results res = database.executeSelectQuery(findRegistrySparql,
                initialBindings);
        ResultSet results = res.getResults();
        Vector<BridgeDescription> bridges = new Vector<BridgeDescription>();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            BridgeDescription bridge = new BridgeDescription();
            try {
                URL bridgeUrl = new URL(solution.getResource("uri").getURI());
                bridge.setHost(bridgeUrl.getHost());
                bridge.setPort(bridgeUrl.getPort());
                bridge.setGuid(solution.getLiteral("guid").getString());
                bridge.setName(solution.getLiteral("name").getString());
                bridge.setDescription(
                        solution.getLiteral("description").getString());
                bridge.setServerType(solution.getLiteral("type").getString());
                bridges.add(bridge);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        res.close();
        if (bridges.size() > 0) {
            BridgeRegistry registry = new BridgeRegistry();
            registry.setUri(uri);
            registry.setBridges(bridges);
            return registry;
        }
        return null;
    }

    /**
     *
     * @see net.crew_vre.recorder.dao.AGBridgeRegistryDao#removeBridgeRegistry(
     *     java.lang.String)
     */
    public void removeBridgeRegistry(String uri) {
        BridgeRegistry registry = findBridgeRegistry(uri);
        if (registry != null) {
            Model model = database.getUpdateModel();
            addModelForRegistry(model, registry);
            database.deleteModel(null, model);
        }
    }

}
