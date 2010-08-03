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
package net.crew_vre.harvester.impl;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterDao;
import net.crew_vre.harvester.Vocab;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A DAO for handling harvester sources.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvesterDaoImpl.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class HarvesterDaoImpl implements HarvesterDao {

    public HarvesterDaoImpl(Database database) {
        this.database = database;
        this.findSourcesAsConstruct = Utils.loadSparql("/sparql/findSource.rql");
        this.findPermittedSourcesAsConstruct =
                Utils.loadSparql("/sparql/findPermittedSources.rql");
    }

    // ---------- PUBLIC METHODS

    public HarvestSource createHarvestSource(String location, String name, String description,
                                             boolean isBlocked) {

        Model model = database.getUpdateModel();
        model.setNsPrefix("harvester", Vocab.NS);

        Resource resource = model.createResource(location);

        if (name != null) {
            resource.addLiteral(DC.title, name);
        }

        if (description != null) {
            resource.addLiteral(DC.description, description);
        }
        resource.addLiteral(Vocab.isBlocked, isBlocked);


        model.add(model.createStatement(resource, RDF.type, Vocab.Source));
        database.addModel(null, model);

        // we could actually populate the bean with the method arguments, but it feels right
        // to pull them off the model
        HarvestSource source = new HarvestSourceImpl(resource.getURI(),
                resource.getProperty(DC.title).getLiteral().getLexicalForm(),
                resource.getProperty(DC.description).getLiteral().getLexicalForm(),
                resource.getProperty(Vocab.isBlocked).getLiteral().getBoolean());

        model.close();

        return source;
    }

    public void updateHarvestSource(String location, String name, String description,
                                    Date lastVisited, String lastStatus, boolean blocked) {


        // title
        if (name != null) {
            database.updateProperty(null, location, DC.title,
                    ResourceFactory.createTypedLiteral(name, XSDDatatype.XSDstring));
        }

        // description
        if (description != null) {
            database.updateProperty(null, location, DC.description,
                    ResourceFactory.createTypedLiteral(description, XSDDatatype.XSDstring));
        }

        // last visited
        if (lastVisited != null) {
            database.updateProperty(null, location, Vocab.lastVisited,
                    ResourceFactory.createTypedLiteral(lastVisited.getTime()));
        }

        if (lastStatus != null) {
            database.updateProperty(null, location, Vocab.lastStatus,
                    ResourceFactory.createTypedLiteral(lastStatus));
        }

        // is blocked
        database.updateProperty(null, location, Vocab.isBlocked,
                ResourceFactory.createTypedLiteral(blocked));
    }

    public HarvestSource findSource(String location) {

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("location", ModelFactory.createDefaultModel().createResource(location));

        List<HarvestSource> results = findSources(database.executeConstructQuery(
                findSourcesAsConstruct, initialBindings));

        if (results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public List<HarvestSource> findAllSources() {

        return findSources(database.executeConstructQuery(findSourcesAsConstruct, null));
    }

    public List<HarvestSource> findAllPermittedSources() {
        return findSources(database.executeConstructQuery(findPermittedSourcesAsConstruct, null));
    }

    public void deleteSource(String location) {

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("location", ModelFactory.createDefaultModel().createResource(location));
        Model model = database.executeConstructQuery(findSourcesAsConstruct, initialBindings);
        database.deleteModel(null, model);
        model.close();
    }

    public void deleteData(String location) {
        database.deleteAll(location);
    }

    // ---------- PRIVATE METHODS


    private List<HarvestSource> findSources(Model model) {

        List<HarvestSource> sources = new ArrayList<HarvestSource>();

        ResIterator iter = model.listSubjects();

        while (iter.hasNext()) {
            sources.add(findSource(iter.nextResource()));
        }

        return sources;
    }

    private HarvestSource findSource(Resource resource) {

        String location, name = null, description = null, lastStatus = null;
        Date lastVisited = null;
        boolean isBlocked = false;

        location = resource.getURI();

        if (resource.getProperty(DC.title) != null) {
            name = resource.getProperty(DC.title).getLiteral().getLexicalForm();
        }

        if (resource.getProperty(DC.description) != null) {
            description = resource.getProperty(DC.description).getLiteral().getLexicalForm();
        }

        if (resource.getProperty(Vocab.lastVisited) != null) {
            lastVisited = new Date(resource.getProperty(Vocab.lastVisited).getLong());
        }

        if (resource.getProperty(Vocab.lastStatus) != null) {
            lastStatus = resource.getProperty(Vocab.lastStatus).getLiteral().getLexicalForm();
        }

        if (resource.getProperty(Vocab.isBlocked) != null) {
            isBlocked = resource.getProperty(Vocab.isBlocked).getBoolean();
        }

        return new HarvestSourceImpl(location, name, description, lastVisited,
                lastStatus, isBlocked);
    }

    private Database database;
    private String findSourcesAsConstruct;
    private String findPermittedSourcesAsConstruct;

}
