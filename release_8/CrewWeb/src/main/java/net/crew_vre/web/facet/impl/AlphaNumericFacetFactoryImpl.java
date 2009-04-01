package net.crew_vre.web.facet.impl;

import net.crew_vre.events.dao.RefinementDao;
import net.crew_vre.events.domain.facet.CountItem;
import net.crew_vre.web.facet.AlphaNumericFacetFactory;
import net.crew_vre.web.facet.Facet;
import net.crew_vre.web.facet.FacetState;
import net.crew_vre.web.facet.SearchFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import org.caboto.jena.db.Data;

/**
 * <p>A factory for creating alpha-numeric facets with a state and possible refinements.</p>
 * <p/>
 * <p>The configuration details of a facet are held in a map. The keys that are
 * available to this facet type are:</p>
 * <p/>
 * <ul>
 * <li><em>facetType</em> - the facet type, e.g. AlphaNumeric.</li>
 * <li><em>facetTitle</em> - the title of the facet, e.g. "Event Titles"</li>
 * <li><em>linkProperty</em> - the RDF link property,
 * e.g. http://purl.org/dc/elements/1.1/title</li>
 * <li><em>constraintType</em> - the RDF type used to constrain searches, e.g.
 * http://www.ilrt.bristol.ac.uk/iugo#MainEvent</li>
 * <li><em>paramName</em> - the parameter name that will represent this facet in a request
 * URL, e.g. "eventTitles"</li>
 * </ul>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AlphaNumericFacetFactoryImpl.java 994 2009-01-09 15:42:35Z cmmaj $
 */
public class AlphaNumericFacetFactoryImpl implements AlphaNumericFacetFactory {

    /**
     * <p>Constructor.</p>
     *
     * @param dao the DAO used to find refinements for the facet.
     */
    public AlphaNumericFacetFactoryImpl(final RefinementDao dao) {
        this.dao = dao;
    }

    /**
     * @param config a Map holding the configuration details for the facet.
     * @return a facet implementation with no constraints.
     */
    public Facet create(final Map<String, String> config, final Data data) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createInitialState(config, data));
        return facet;
    }

    /**
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of filters that provide constraints.
     * @return a facet implentation with constraints.
     */
    public Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters,
                        final Data data) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, searchFilters, data));
        return facet;
    }

    /**
     * @param config         a Map holding the configuration details for the facet.
     * @param facetStateName a facet state name
     * @return a representation of a facet that is selected and has no
     *         further refinements.
     */
    public Facet create(final Map<String, String> config, final String facetStateName,
                        final Data data) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, facetStateName));
        return facet;
    }

    /**
     * <p>Creates an initial state for the facet.<p>
     * <p/>
     * <p>This method is called when no facets have been selected so each refinement is
     * called without any constraints (except for those specified in the configuration
     * for the facet.</p>
     *
     * @param config a Map holding the configuration details for the facet.
     * @return a facet state.
     */
    private FacetState createInitialState(final Map<String, String> config, final Data data) {

        // create an empty (hidden) facet state
        FacetState initialState = new FacetStateImpl();

        // create the search filter
        SearchFilter searchFilter = new AlphaNumericSearchFilterImpl(config.get(Facet.PARAM_NAME),
                config.get(Facet.LINK_PROPERTY));

        // get the all the count items
        List<CountItem> countItems = dao.countRefinements(searchFilter.getSparqlFragment(),
                config.get(Facet.CONSTRAINT_TYPE), config.get(Facet.PARAM_NAME), data);

        Map<String, Integer> countMap = createMap(countItems);

        List<FacetState> refinements = getrefinements(countMap, config.get(Facet.PARAM_NAME));

        // add the refinements to the initial state
        initialState.getRefinements().addAll(refinements);

        return initialState;
    }

    /**
     * <p>Creates a state for the facet that is subject to constraints.</p>
     *
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of SPARQL fragments that provide the constraints.
     * @return a facet state.
     */
    private FacetState createState(final Map<String, String> config,
                                   final List<SearchFilter> searchFilters, Data data) {

        // create a string builder of sparql fragments that
        // represent the constraints
        StringBuilder coreFilterFragment = new StringBuilder();

        for (SearchFilter filter : searchFilters) {
            coreFilterFragment.append(filter.getSparqlFragment());
        }

        // create the search filter
        SearchFilter searchFilter = new AlphaNumericSearchFilterImpl(config.get(Facet.PARAM_NAME),
                config.get(Facet.LINK_PROPERTY));

        // get the all the count items
        List<CountItem> countItems = dao.countRefinements(searchFilter.getSparqlFragment()
                + coreFilterFragment.toString(), config.get(Facet.CONSTRAINT_TYPE),
                config.get(Facet.PARAM_NAME), data);

        // create the initial state
        FacetState initialState = new FacetStateImpl();

        Map<String, Integer> countMap = createMap(countItems);

        List<FacetState> refinements = getrefinements(countMap, config.get(Facet.PARAM_NAME));

        // add the refinements to the initial state
        initialState.getRefinements().addAll(refinements);

        return initialState;
    }

    /**
     * <p>Creates a final possible state, i.e. one that is selected, for an alpha numeric
     * facet.</p>
     *
     * @param config a Map holding the configuration details for the facet.
     * @param name   the name (label) of the selected facet state.
     * @return a facet state.
     */
    private FacetState createState(final Map<String, String> config, final String name) {

        // create a blank node to hold the facet details
        FacetStateImpl blankRoot = new FacetStateImpl();
        blankRoot.setParamName(config.get(Facet.PARAM_NAME));

        // create a new state based on the parameters
        FacetStateImpl state = new FacetStateImpl();
        state.setName(name);
        state.setParamName(config.get(Facet.PARAM_NAME));
        state.setParent(blankRoot);

        return state;
    }

    /**
     * @return an alpha numeric character array.
     */
    private char[] createAlphaNumericArray() {
        return "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    }

    /**
     * @param c an alpha numeric character.
     * @return returns a label using the character.
     */
    private String createLabel(final char c) {
        return c + "*";
    }

    private Map<String, Integer> createMap(List<CountItem> countItems) {

        // create a map to hold the the alphanumeric index and counts
        Map<String, Integer> countMap = new HashMap<String, Integer>();

        for (CountItem item : countItems) {

            String val = item.getValue();

            if (val != null && val.trim().length() > 0) {

                // get the first item of a title and make it upper case
                val = val.substring(0, 1).toUpperCase();

                // add the item to the map / increment values
                if (countMap.containsKey(val)) {
                    Integer count = countMap.get(val);
                    count++;
                    countMap.put(val, count);
                } else {
                    countMap.put(val, 1);
                }
            }
        }


        return countMap;
    }

    private List<FacetState> getrefinements(Map<String, Integer> countMap, String paramName) {

        List<FacetState> refinements = new ArrayList<FacetState>();

        // go through 0-9A-Z and populate the facet states
        for (char c : createAlphaNumericArray()) {

            // create the label
            String label = createLabel(c);

            if (countMap.containsKey(String.valueOf(c))) {
                FacetState state = new FacetStateImpl(label, countMap.get(String.valueOf(c)),
                        paramName, label);
                refinements.add(state);
            }
        }

        return refinements;
    }


    // DAO used to count refinements
    private RefinementDao dao;
}
