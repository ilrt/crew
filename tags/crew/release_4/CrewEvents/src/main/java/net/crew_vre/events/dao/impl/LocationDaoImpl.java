package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import net.crew_vre.events.dao.LocationDao;
import net.crew_vre.events.domain.Location;

import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import java.util.ArrayList;
import java.util.List;

public class LocationDaoImpl implements LocationDao {


    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param database the database to query
     */
    public LocationDaoImpl(final Database database) {
        this.database = database;

        sparqlSkosLocation = Utils.loadSparql("/sparql/skos-locations.rq");
    }


    /**
     * <p>An event can be associated with a number of locations that are found in a SKOS
     * taxonomy. This method provides a <code>List</code> of locations that are associated
     * a specific event.</p>
     *
     * @param id the identifier of an event
     * @return a <code>List</code> of <code>Location</code> objects that are related to the event
     *         identified by the <code>id</code>.
     */
    public List<Location> findLocationByEvent(final String id) {

        List<Location> locations = new ArrayList<Location>();

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(id));


        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find locations for an event:");
            logger.debug(sparqlSkosLocation);
        }

        Results res = database.executeSelectQuery(sparqlSkosLocation,
                initialBindings);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {
            locations.add(getLocationDetails(rs.nextSolution()));
        }

        res.close();

        return locations;
    }


    /**
     * <p>Method that gets the details for a location.</p>
     *
     * @param qs the query solution that holds the results for a row
     * @return location details
     */
    private Location getLocationDetails(final QuerySolution qs) {

        Location location = new Location();
        Resource resource = qs.getResource("skosLocationId");
        location.setId(resource.getURI());

        if(qs.getResource("graph") != null) {
            location.setGraph(qs.getResource("graph").getURI());
        }

        if (qs.getLiteral("skosLocationName") != null) {
            location.setName(qs.getLiteral("skosLocationName").getLexicalForm());
        }

        return location;
    }


    // sparql to find location details
    private String sparqlSkosLocation;

    // Logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * The dataset that is queried
     */
    private final Database database;
}
