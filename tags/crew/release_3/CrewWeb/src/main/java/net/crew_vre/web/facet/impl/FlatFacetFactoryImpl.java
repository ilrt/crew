package net.crew_vre.web.facet.impl;

import net.crew_vre.events.dao.RefinementDao;
import net.crew_vre.events.domain.facet.CountItem;
import net.crew_vre.events.domain.facet.Refinement;
import net.crew_vre.web.facet.Facet;
import net.crew_vre.web.facet.FacetState;
import net.crew_vre.web.facet.FlatFacetFactory;
import net.crew_vre.web.facet.SearchFilter;
import net.crew_vre.web.Utility;

import java.util.List;
import java.util.Map;

/**
 * <p>A factory for creating flat facets.</p>
 *
 * <p>NOTE: This implementation only supports resources for the object - it needs to be refactored
 * to support literals.</p>
 *
 * <p>The configuration details of a facet are held in a map. The keys that are available to this
 * facet type are:</p>
 *
 * <ul>
 *    <li><em>facetType</em> - the facet type, e.g. Flat.</li>
 *    <li><em>facetTitle</em> - the title of the facet, e.g. "Event Types"</li>
 *    <li><em>linkProperty</em> - the RDF link property,
 *       e.g. http://www.w3.org/1999/02/22-rdf-syntax-ns#type</li>
 *    <li><em>constraintType</em> - the RDF type used to constrain searches, e.g.
 *       http://www.ilrt.bristol.ac.uk/iugo#MainEvent</li>
 *    <li><em>paramName</em> - the parameter name that will represent this facet in
 *       a request URL, e.g. "eventDateTime"</li>
 * </ul>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: FlatFacetFactoryImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 *
 **/
public class FlatFacetFactoryImpl implements FlatFacetFactory {

    /**
     * <p>Constructor</p>
     *
     * @param dao           the DAO used to fine refinements for the facet.
     * @param utility      utility to help with some common functionality.
     */
    public FlatFacetFactoryImpl(final RefinementDao dao, final Utility utility) {
        this.dao = dao;
        this.utility = utility;
    }

    /**
     * <p>Creates a facet with no constraints.</p>
     *
     * @param config    a Map holding the configuration details for the facet.
     * @return a facet implementation with no constraints.
     */
    public Facet create(final Map<String, String> config) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createInitialState(config));
        return facet;
    }

    /**
     * <p>Creates a facet with no constrants but is constrained by other facets.</p>
     *
     * @param config            a Map holding the configuration details for the facet.
     * @param searchFilters     a List of filters that provide constraints.
     * @return a facet implentation with constraints.
     */
    public Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, searchFilters));
        return facet;
    }

    /**
     * <p>Creates a fully constrained facet.</p>
     *
     * @param config            a Map holding the configuration details for the facet.
     * @param selectedUri       uri that reflects the facet state.
     * @return a representation of a facet that is selected and has no
     *                          further refinements.
     */
    public Facet create(final Map<String, String> config, final String selectedUri) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, selectedUri));
        return facet;
    }

    /**
     * <p>Creates an initial state for the facet. This method is called when no facets have
     * been selected so each refinement is called without any constraints (except for those
     * specified in the configuration for the facet.</p>
     *
     * @param config    a Map holding the configuration details for the facet.
     * @return a facet state.
     */
    private FacetState createInitialState(final Map<String, String> config) {

        List<Refinement> names = dao.findProperties(config.get(Facet.LINK_PROPERTY));

        FacetState initialState = new FacetStateImpl();

        for (Refinement name : names) {

            // create the SPARQL filter fragment for this character
            SearchFilter filter = new FlatSearchFilterImpl(config.get(Facet.LINK_PROPERTY),
                    name.getId());

            List<CountItem> refs = dao.countRefinements(filter.getSparqlFragment(),
                    config.get(Facet.CONSTRAINT_TYPE));

            // create a facet state for the refinement
            FacetStateImpl fState = new FacetStateImpl();
            fState.setParamName(config.get(Facet.PARAM_NAME));
            fState.setParamValue(utility.uriToParmeterValue(name.getId()));
            fState.setName(name.getName());
            fState.setCount(refs.size());
            initialState.getRefinements().add(fState);
        }
        return initialState;
    }

    /**
     * <p>Creates a state for the facet that is subject to constraints.</p>
     *
     * @param config            a Map holding the configuration details for the facet.
     * @param searchFilters     a List of SPARQL fragments that provide the constraints.
     * @return a facet state.
     */
    private FacetState createState(final Map<String, String> config,
                                   final List<SearchFilter> searchFilters) {

        // create a string builder of sparql fragments that
        // represent the constraints
        StringBuilder coreFilterFragment = new StringBuilder();

        for (SearchFilter filter : searchFilters) {
            coreFilterFragment.append(filter.getSparqlFragment());
        }

        List<Refinement> names = dao.findProperties(config.get(Facet.LINK_PROPERTY));

        FacetState initialState = new FacetStateImpl();

        for (Refinement name : names) {

            // create the SPARQL filter fragment for this character
            SearchFilter filter = new FlatSearchFilterImpl(config.get(Facet.LINK_PROPERTY),
                    name.getId());

            List<CountItem> refs = dao.countRefinements(filter.getSparqlFragment()
                    + coreFilterFragment.toString(), config.get(Facet.CONSTRAINT_TYPE));

            // create a facet state for the refinement
            FacetStateImpl fState = new FacetStateImpl();
            fState.setParamName(config.get(Facet.PARAM_NAME));
            fState.setParamValue(utility.uriToParmeterValue(name.getId()));
            fState.setName(name.getName());
            fState.setCount(refs.size());
            initialState.getRefinements().add(fState);
        }
        return initialState;
    }

    private FacetState createState(final Map<String, String> config, final String selectedUri) {

        // create a blank node to hold the facet details
        FacetStateImpl blankRoot = new FacetStateImpl();
        blankRoot.setParamName(config.get(Facet.PARAM_NAME));
        String uri = utility.parameterValueToUri(selectedUri);
        Refinement name = dao.getName(uri);

        // create a new state based on the parameters
        FacetStateImpl state = new FacetStateImpl();
        state.setName(name.getName());
        state.setParamName(config.get(Facet.PARAM_NAME));
        state.setParent(blankRoot);

        return state;
    }

    private RefinementDao dao;
    private Utility utility;
}
