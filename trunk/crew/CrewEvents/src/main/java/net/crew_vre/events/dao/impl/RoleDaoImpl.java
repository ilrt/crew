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
import net.crew_vre.events.dao.RoleDao;
import net.crew_vre.events.domain.Person;
import net.crew_vre.events.domain.Role;

import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is an implementation of <code>RoleDao</code> that uses the Jena
 * Sematic Web Framework. SPARQL is used for querying the underlying RDF model.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RoleDaoImpl.java 1188 2009-03-31 13:09:20Z cmmaj $
 */
public class RoleDaoImpl implements RoleDao {


    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param database The database to query
     */
    public RoleDaoImpl(final Database database) {
        this.database = database;

        sparqlByEvent = Utils.loadSparql("/sparql/roles-by-event.rq");
        sparqlByPerson = Utils.loadSparql("/sparql/roles-by-person.rq");
    }


    public List<Role> findRolesByPerson(final String personId) {
        return findRoles(personId, "personId", sparqlByPerson);
    }


    public List<Role> findRolesByEvent(final String eventId) {
        return findRoles(eventId, "eventId", sparqlByEvent);
    }


    public List<Role> findRoles(final String id, final String binding, final String sparql) {

        List<Role> results = new ArrayList<Role>();

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add(binding, ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPAQRL to find roles:");
            logger.debug(sparql);
        }

        Results res = database.executeSelectQuery(sparql,
                initialBindings);
        ResultSet rs = res.getResults();

        // iterate over the results
        while (rs.hasNext()) {
            results.add(getRoleDetails(rs.nextSolution()));
        }

        res.close();

        return results;
    }


    private Role getRoleDetails(final QuerySolution qs) {

        Role role = new Role();

        // add the id for the role
        Resource resource = qs.getResource("id");
        role.setId(resource.getURI());

        if (qs.getResource("graph") != null) {
            role.setGraph(qs.getResource("graph").getURI());
        }

        // get the role name
        if (qs.getLiteral("name") != null) {
            role.setName(qs.getLiteral("name").getLexicalForm());
        }

        // get the person stuff if it exists
        if (qs.getResource("personId") != null) {

            Person person = new Person();
            person.setId(qs.getResource("personId").getURI());

            if (qs.getLiteral("title") != null) {
                person.setTitle(qs.getLiteral("title").getLexicalForm());
            }

            if (qs.getLiteral("givenName") != null) {
                person.setGivenName(qs.getLiteral("givenName").getLexicalForm());
            }

            if (qs.getLiteral("family_name") != null) {
                person.setFamilyName(qs.getLiteral("family_name").getLexicalForm());
            }

            role.setHeldBy(person);
        }

        return role;
    }

    private String sparqlByPerson;

    private String sparqlByEvent;

    // logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * The database that is queried
     */
    private final Database database;
}
