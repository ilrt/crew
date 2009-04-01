package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import net.crew_vre.events.dao.RoleDao;
import net.crew_vre.events.domain.Person;
import net.crew_vre.events.domain.Role;
import net.crew_vre.jena.query.DatasetFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is an implementation of <code>RoleDao</code> that uses the Jena
 * Sematic Web Framework. SPARQL is used for querying the underlying RDF model.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RoleDaoImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class RoleDaoImpl implements RoleDao {


    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param datasetFactory factory that provides a dataset to query
     * @param jqUtility      utility that helps with Jena queries
     */
    public RoleDaoImpl(final DatasetFactory datasetFactory, final JenaQueryUtility jqUtility) {
        this.dataset = datasetFactory.create();
        this.jqUtility = jqUtility;

        sparqlByEvent = jqUtility.loadSparql("/sparql/roles-by-event.rq");
        sparqlByPerson = jqUtility.loadSparql("/sparql/roles-by-person.rq");
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

        QueryExecution qe = jqUtility.queryExecution(sparql, dataset, initialBindings);
        ResultSet rs = qe.execSelect();

        // iterate over the results
        while (rs.hasNext()) {
            results.add(getRoleDetails(rs.nextSolution()));
        }

        qe.close();

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
     * The dataset that is queried
     */
    private final Dataset dataset;

    /**
     * The utility used with Jena queries
     */
    final JenaQueryUtility jqUtility;
}
