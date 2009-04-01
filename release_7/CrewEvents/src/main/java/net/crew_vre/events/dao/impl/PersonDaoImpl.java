package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.dao.PersonDao;
import net.crew_vre.events.domain.Person;
import net.crew_vre.events.domain.Role;

import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is an implementation of <code>PersonDao</code> that uses the Jena
 * Sematic Web Framework. SPARQL is used for querying the underlying RDF model.</p>
 * <p/>
 * <p>All of the methods find the basic details for a person except for the
 * <code>findPersonById</code> which finds much more detailed information.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PersonDaoImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class PersonDaoImpl implements PersonDao {


    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param database       The database to query
     * @param eventDao       DAO that provides access to event information
     */
    public PersonDaoImpl(final Database database,
                         final EventDao eventDao) {
        this.database = database;
        this.eventDao = eventDao;

        this.sparqlFindPeople = Utils.loadSparql("/sparql/person-basic-details.rq");
        this.sparqlFindPeopleFragment =
            Utils.loadSparql("/sparql/person-basic-details-fragment.rq");
        this.sparqlFindPersonFull = Utils.loadSparql("/sparql/person-full-details.rq");
        this.sparqlFindRoles = Utils.loadSparql("/sparql/person-find-roles.rq");
        this.sparqlFindPeronByEventId =
            Utils.loadSparql("/sparql/person-full-details-by-event.rq");
        this.sparqlFindAuthors = Utils.loadSparql("/sparql/person-authors.rq");
    }


    /**
     * <p>Find details for an individual.</p>
     *
     * @param id the identifier of the person
     * @return a person identified the by id
     */
    public Person findPersonById(final String id) {

        List<Person> results = findPeople(id, "id", sparqlFindPersonFull, true);

        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }


    /**
     * <p>Find all people.</p>
     *
     * @return a list of people
     */
    public List<Person> findAllPeople() {
        return findPeople(sparqlFindPeople, false);
    }


    /**
     * <p>Find all people. The use of LIMIT and OFFSET is useful for pagination.</p>
     *
     * @param limit  the number of results to return
     * @param offset the value used to determine where to start returning results
     * @return a list of people
     */
    public List<Person> findAllPeople(final int limit, final int offset) {

        String sparql = new StringBuilder().append(sparqlFindPeople)
                .append("LIMIT ").append(limit).append("\n")
                .append("OFFSET ").append(offset).append("\n").toString();

        return findPeople(sparql, false);
    }

    /**
     * <p>Helper method to find people - it is called by the public methods</p>
     *
     * @param sparql the SPARQL that should be used in the query
     * @param full   return full details on the person - true (yes) and false (no)
     * @return a list of people
     */
    private List<Person> findPeople(final String sparql, final boolean full) {

        List<Person> results = new ArrayList<Person>();

        Results res = database.executeSelectQuery(sparql, null);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {
            results.add(getPersonDetails(rs.nextSolution(), full));
        }

        res.close();

        return results;
    }

    public List<Person> findPeopleByEvent(final String id) {
        return findPeopleByEvent(id, sparqlFindPeronByEventId);
    }

    public List<Person> findPeopleByEvent(final String id, final int limit, final int offset) {

        String sparql = new StringBuilder()
                .append(sparqlFindPeronByEventId).append("LIMIT ")
                .append(limit).append("\nOFFSET ").append(offset)
                .append("\n").toString();

        return findPeopleByEvent(id, sparql);
    }

    public List<Person> findPeopleByRole(final String id) {
        return findPeople(id, "roleId", sparqlFindPeronByEventId, false);
    }

    public List<Person> findAuthors(final String id) {
        return findPeople(id, "paperId", sparqlFindAuthors, false);
    }

    public List<Person> findPeopleByConstraint(final String constraint) {
        StringBuilder sparql = new StringBuilder(sparqlFindPeopleFragment)
                .append(constraint)
                .append("}\n}\n");
        return findPeople(sparql.toString(), false);
    }

    public List<Person> findPeopleByConstraint(final String constraint, final int limit,
                                               final int offset) {
        StringBuilder sparql = new StringBuilder(sparqlFindPeopleFragment)
                .append(constraint)
                .append("}\n}\n")
                .append("ORDER BY ASC($family_name)\n")
                .append("LIMIT ")
                .append(limit)
                .append("\nOFFSET ")
                .append(offset);
        return findPeople(sparql.toString(), false);
    }

    private List<Person> findPeopleByEvent(final String id, final String sparql) {
        return findPeople(id, "eventId", sparql, false);
    }

    /**
     * <p>General method that queries the data for people.</p>
     *
     * @param id      the URI that we are searching for
     * @param binding the value we are binding the URI against
     * @param sparql  the SPARQL query that we need to execute
     * @param full    return full or partial details for a person
     * @return a list of people found by executing the query
     */
    private List<Person> findPeople(final String id, final String binding, final String sparql,
                                    final boolean full) {

        List<Person> results = new ArrayList<Person>();

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add(binding, ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find people:");
            logger.debug(sparql);
        }

        // setup and execute the query
        Results res = database.executeSelectQuery(sparql,
                initialBindings);
        ResultSet rs = res.getResults();

        // there should only be one...
        while (rs.hasNext()) {
            results.add(getPersonDetails(rs.nextSolution(), full));
        }

        res.close();

        return results;
    }


    /**
     * <p>A private utility method used by the public methods to populate a
     * Person object with data found in the QuerySolution.<p>
     *
     * @param qs   the Jena QuerySolution that provides access to the results.
     * @param full return full or partial details
     * @return a Preson object
     */
    private Person getPersonDetails(final QuerySolution qs, final boolean full) {

        Person person = new Person();

        Resource resource = qs.getResource("id");
        person.setId(resource.getURI());

        if (qs.getResource("graph") != null) {
            person.setGraph(qs.getResource("graph").getURI());
        }

        if (qs.getLiteral("title") != null) {
            person.setTitle(qs.getLiteral("title").getLexicalForm());
        }

        if (qs.getLiteral("givenName") != null) {
            person.setGivenName(qs.getLiteral("givenName").getLexicalForm());
        }

        if (qs.getLiteral("family_name") != null) {
            person.setFamilyName(qs.getLiteral("family_name").getLexicalForm());
        }

        if (qs.getLiteral("name") != null) {
            person.setName(qs.getLiteral("name").getLexicalForm());
        }

        if (full) {

            if (qs.getResource("homepage") != null) {
                person.setHomepage(qs.getResource("homepage").getURI());
            }

            if (qs.getResource("workplaceHomepage") != null) {
                person.setWorkplaceHomepage(qs.getResource("workplaceHomepage").getURI());
            }

            if (qs.getResource("flickrHomepage") != null) {
                person.setFlickrHomepage(qs.getResource("flickrHomepage").getURI());
            }

            person.setRoles(findRoles(person));

        }

        return person;
    }


    /**
     * <p>Find the roles that are associated with an individual.</p>
     * <p/>
     * <p>The <code>Person</code> object is passed to the method because a <code>Role</code> class
     * keeps a reference to the <code>Person</code> who holds the role.</p>
     *
     * @param person the person who we are interested in
     * @return a list of roles associated with the person
     */
    private List<Role> findRoles(final Person person) {

        List<Role> results = new ArrayList<Role>();

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("personId", ModelFactory.createDefaultModel()
                .createResource(person.getId()));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find roles for a person");
            logger.debug(sparqlFindRoles);
        }

        Results res = database.executeSelectQuery(sparqlFindRoles,
                initialBindings);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {

            QuerySolution qs = rs.nextSolution();

            if (qs.getResource("roleId") != null) {

                Role role = new Role();
                role.setId(qs.getResource("roleId").getURI());

                if (qs.getResource("graph") != null) {
                    role.setGraph(qs.getResource("graph").getURI());
                }

                if (qs.getLiteral("roleName") != null) {
                    role.setName(qs.getLiteral("roleName").getLexicalForm());
                }

                if (qs.getResource("eventId") != null) {
                    role.getRoleAt().add(eventDao.findEventById(qs.getResource("eventId")
                            .getURI()));
                }

                role.setHeldBy(person);
                results.add(role);
            }
        }

        res.close();

        return results;

    }


    /**
     * SPARQL USED FOR THE QUERIES
     */

    // find people - basic details
    private String sparqlFindPeople;

    // find people - fragment to allow sparql injection
    private String sparqlFindPeopleFragment;

    // find person - fuller details
    private final String sparqlFindPersonFull;

    // find roles and related events held by the person
    private final String sparqlFindRoles;

    // find authors
    private final String sparqlFindAuthors;

    // sparql query: get users associated with an event
    private final String sparqlFindPeronByEventId;

    /**
     * DAO used to access event information
     */
    private EventDao eventDao;

    // logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * The database that is queried
     */
    private final Database database;
}
