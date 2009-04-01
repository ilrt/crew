package net.crew_vre.events.dao;

import net.crew_vre.events.domain.facet.Refinement;
import net.crew_vre.events.domain.facet.CountItem;

import java.util.List;

import org.caboto.jena.db.Data;

/**
 *
 * @author: Mike Jones (mike.a.jones@gmail.com)
 * @version: $Id$
 *
 **/
public interface RefinementDao {
    /**
     * <p>Find the names of refinements - used by the hierarchical facet.</p>
     *
     * @param widerProperty     the property used to widen searches.
     * @param subjectUri        the resource to start the search.
     * @return a list of refinements.
     */
    List<Refinement> findNames(String widerProperty, String subjectUri, Data data);

    /**
     * <p>Find the name of a resource.</p>
     *
     * @param uri   the URI of the resource.
     * @return a Refinement with a name.
     */
    Refinement getName(String uri, Data data);

    /**
     * <p>Returns a list of refinenments based on a single linking property - it is used by the
     * Flat Facet.</p>
     *
     * @param linkProperty      the linking property.
     * @return a list of refinements.
     */
    List<Refinement> findProperties(String linkProperty, Data data);

    /**
     * <p>Find the parents of a resource - used in the hierachical facets.</p>
     *
     * @param linkProperty      the linking property.
     * @param childUri          the child URI we want to find the parents.
     * @return the parent of the childUri or null.
     */
    Refinement findParents(String linkProperty, String childUri, Data data);

    /**
     * <p>Count the items that are available for a refinement, e,g, how many events start with the
     * letter A. It returns a list of count objects - these objects include graph details which
     * allows us to filter the results if the person browsing isn't allowed to access an event.
     * This allows us to display a number in the UI of matching events that a person is allows to
     * view rather than all matching events.</p>
     *
     * @param sparqlFragment    SPARQL fragment that constraints the count.
     * @param type              the RDF type we are interested in.
     * @return return a list of count objects.
     */
    List<CountItem> countRefinements(String sparqlFragment, String type, String paramName, final Data data);
}
