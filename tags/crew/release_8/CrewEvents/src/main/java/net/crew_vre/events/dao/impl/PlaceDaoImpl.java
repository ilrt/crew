package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import net.crew_vre.events.dao.PlaceDao;
import net.crew_vre.events.domain.Place;

import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>This is an implementation of <code>PlaceDao</code> that uses the Jena
 * Sematic Web Framework. SPARQL is used for querying the underlying RDF model.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PlaceDaoImpl.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class PlaceDaoImpl implements PlaceDao {


    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param database The database to query
     */
    public PlaceDaoImpl(final Database database) {
        this.database = database;

        sparqFindPlaceById = Utils.loadSparql("/sparql/place-details.rq");
        sparqlFindParts = Utils.loadSparql("/sparql/place-details-haspart.rq");
    }


    /**
     * <p>Find the details for a specific place.</p>
     *
     * @param id the identifier of a place
     * @return a place identified by the id
     */
    public Place findPlaceById(final String id) {

        Place place = null;

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find place details:");
            logger.debug(sparqFindPlaceById);
        }

        Results res = database.executeSelectQuery(sparqFindPlaceById,
                initialBindings);
        ResultSet rs = res.getResults();

        // there should only be one...
        while (rs.hasNext()) {
            place = getPlaceDetails(rs.nextSolution());
            place.setLocations(findPartsForPlace(place.getId()));
        }

        res.close();

        return place;
    }


    /**
     * <p>A Place might be made up of may parts. This methods finds the "parts" for a
     * place.</p>
     *
     * @param id the identity of the Place
     * @return a Set of Place objects i.e the parts of a Place!
     */
    private Set<Place> findPartsForPlace(final String id) {

        // list to hold the results
        Set<Place> parts = new HashSet<Place>();

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("place", ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find place details:");
            logger.debug(sparqlFindParts);
        }

        Results res = database.executeSelectQuery(sparqlFindParts,
                initialBindings);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {
            Place place = getPlaceDetails(rs.nextSolution());
            parts.add(place);
        }

        res.close();

        return parts;
    }


    /**
     * <p>Get the details of a location and create a <code>Place</code> object.</p>
     *
     * @param qs the Jena query solution with the results of a sparql query
     * @return a Place
     */
    private Place getPlaceDetails(final QuerySolution qs) {

        Place place = new Place();
        Resource resource = qs.getResource("id");
        place.setId(resource.getURI());

        if (qs.getResource("graph") != null) {
            place.setGraph(qs.getResource("graph").getURI());
        }

        if (qs.getLiteral("name") != null) {
            place.setTitle(qs.getLiteral("name").getLexicalForm());
        }

        if (qs.getLiteral("longitude") != null) {
            place.setLongitude(Float.valueOf(qs.getLiteral("longitude").getLexicalForm()));
        }

        if (qs.getLiteral("latitude") != null) {
            place.setLatitude(Float.valueOf(qs.getLiteral("latitude").getLexicalForm()));
        }

        if (qs.getLiteral("altitude") != null) {
            place.setLatitude(Float.valueOf(qs.getLiteral("altitude").getLexicalForm()));
        }

        return place;
    }


    /**
     * SPARQL to find a place by its URI
     */
    private String sparqFindPlaceById;

    /**
     * SPARQL to find a places that have a "hasPart" relationship with another place
     */
    private String sparqlFindParts;

    // Logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * The database that is queried
     */
    private final Database database;
}
