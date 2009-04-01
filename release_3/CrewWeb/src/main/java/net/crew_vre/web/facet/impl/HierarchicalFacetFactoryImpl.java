package net.crew_vre.web.facet.impl;

import net.crew_vre.events.dao.RefinementDao;
import net.crew_vre.events.domain.facet.CountItem;
import net.crew_vre.events.domain.facet.Refinement;
import net.crew_vre.web.facet.Facet;
import net.crew_vre.web.facet.FacetState;
import net.crew_vre.web.facet.HierarchicalFacetFactory;
import net.crew_vre.web.facet.SearchFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>A factory for creating hierachical facets with a state and possible refinements.</p>
 * <p/>
 * <p>The configuration details of a facet are held in a map. The keys that are available to this
 * facet type are:</p>
 * <p/>
 * <ul>
 * <li><em>facetType</em> - the facet type, e.g. Hierarchical.</li>
 * <li><em>facetTitle</em> - the title of the facet, e.g. "Subjects"</li>
 * <li><em>linkProperty</em> - the RDF link property,
 * e.g. http://www.ilrt.bristol.ac.uk/iugo#hasSubject</li>
 * <li><em>widerProperty</em> - the property used to find wider concepts,
 * e.g. http://www.w3.org/2004/02/skos/core#broader</li>
 * <li><em>facetBase</em> - the URI of the base of start of the hierarchy,
 * e.g. http://www.ilrt.bristol.ac.uk/iugo/subjects/#disciplines</li>
 * <li><em>constraintType</em> - the RDF type used to constrain searches, e.g.
 * http://www.ilrt.bristol.ac.uk/iugo#MainEvent</li>
 * <li><em>paramName</em> - the parameter name that will represent this facet in
 * a request URL, e.g. "subjects"</li>
 * <li><em>prefix</em> - the prefix used by this facet, e.g. "iugosubs"</li>
 * </ul>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: HierarchicalFacetFactoryImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class HierarchicalFacetFactoryImpl implements HierarchicalFacetFactory {

    /**
     * <p>Constructor.</p>
     *
     * @param dao        the DAO used to find refinements and parents of facet states.
     * @param nsPrefixes a Map of prefixes and the URIs they represent.
     */
    public HierarchicalFacetFactoryImpl(final RefinementDao dao,
                                        final Map<String, String> nsPrefixes) {
        this.dao = dao;
        this.nsPrefixes = nsPrefixes;
    }

    /**
     * @param config a Map holding the configuration details for the facet.
     * @return a facet implementation with no constraints.
     */
    public Facet create(final Map<String, String> config) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createInitialState(config));
        return facet;
    }

    /**
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of filters that provide constraints.
     * @return a facet implentation with constraints.
     */
    public Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, searchFilters));
        return facet;
    }

    /**
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of filters that provide constraints.
     * @param selectedUri   a URI reprsenting the facet state.
     * @return a facet state reprsented by the URI.
     */
    public Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters,
                        final String selectedUri) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, searchFilters, selectedUri));
        return facet;
    }

    /**
     * <p>Creates an initial state for the facet. This method is called when no facets have
     * been selected so each refinement is called without any constraints (except for those
     * specified in the configuration for the facet.</p>
     *
     * @param config a Map holding the configuration details for the facet.
     * @return a facet state.
     */
    private FacetState createInitialState(Map<String, String> config) {

        // get the base of the URI from the prefix map
        String base = nsPrefixes.get(config.get(Facet.PREFIX));

        // get the names of the possible refinements
        List<Refinement> refinementNames = dao.findNames(config.get(Facet.WIDER_PROPERTY),
                config.get(Facet.FACET_BASE));

        // create the initial facet (hidden in the UI)
        FacetStateImpl initialState = new FacetStateImpl();
        initialState.setName(config.get(Facet.FACET_BASE));

        // go through the refinement names and count hits
        for (Refinement concept : refinementNames) {

            // create a facet state for the refinement
            FacetStateImpl fState = new FacetStateImpl();
            fState.setParamName(config.get(Facet.PARAM_NAME));

            // create a more readable parameter value than the full URI
            String uri = uriToParmeterValue(config.get(Facet.PREFIX), base, concept.getId());

            fState.setParamValue(uri);
            fState.setName(concept.getName());

            // get the filter for this refinement
            SearchFilter filter = new HierarchicalSearchFilterImpl(config.get(Facet.LINK_PROPERTY),
                    concept.getId());

            // count the hits for this refinement
            List<CountItem> refs = dao.countRefinements(filter.getSparqlFragment(),
                    config.get(Facet.CONSTRAINT_TYPE));

            fState.setCount(refs.size());

            initialState.getRefinements().add(fState);
        }

        return initialState;
    }

    /**
     * <p>Creates a state for the facet that is subject to constraints.</p>
     *
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of SPARQL fragments that provide the constraints.
     * @return a facet state.
     */
    private FacetState createState(Map<String, String> config, List<SearchFilter> searchFilters) {

        // get the base of the URI from the prefix map
        String base = nsPrefixes.get(config.get(Facet.PREFIX));

        // create a string builder of sparql fragments that represent the constraints
        StringBuilder coreFilterFragment = new StringBuilder();

        for (SearchFilter filter : searchFilters) {
            coreFilterFragment.append(filter.getSparqlFragment());
        }

        // get the names of the possible refinements
        List<Refinement> refinementNames = dao.findNames(config.get(Facet.WIDER_PROPERTY),
                config.get(Facet.FACET_BASE));

        // create an initial state
        FacetStateImpl initialState = new FacetStateImpl();
        initialState.setName(config.get(Facet.FACET_BASE));

        // go through the refinement names and count hits
        for (Refinement concept : refinementNames) {

            // create a facet state for the refinement
            FacetStateImpl fState = new FacetStateImpl();
            fState.setParamName(config.get(Facet.PARAM_NAME));

            // create a more readable parameter value than the full URI
            String uri = uriToParmeterValue(config.get(Facet.PREFIX), base, concept.getId());

            fState.setParamValue(uri);
            fState.setName(concept.getName());

            // get the filter for this refinement
            SearchFilter filter = new HierarchicalSearchFilterImpl(config.get(Facet.LINK_PROPERTY),
                    concept.getId());

            // count the hits for this refinement
            List<CountItem> refs = dao.countRefinements(coreFilterFragment
                    + filter.getSparqlFragment(), config.get(Facet.CONSTRAINT_TYPE));

            fState.setCount(refs.size());

            initialState.getRefinements().add(fState);
        }

        return initialState;
    }

    /**
     * <p>Creates a facet state - the facet is in state other than its base state.</p>
     *
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of SPARQL fragments that provide the constraints.
     * @param selectedUri   the URI that represents a state for this facet.
     * @return a facet state.
     */
    private FacetState createState(final Map<String, String> config,
                                   List<SearchFilter> searchFilters, final String selectedUri) {

        // get the base of the URI from the prefix map
        String base = nsPrefixes.get(config.get(Facet.PREFIX));

        // convert the parameter value to a URI
        String sUri = parameterValueToUri(selectedUri);

        // create a string builder of sparql fragments that represent the constraints
        StringBuilder coreFilterFragment = new StringBuilder();

        for (SearchFilter filter : searchFilters) {
            coreFilterFragment.append(filter.getSparqlFragment());
        }

        // get the names of the possible refinements
        List<Refinement> refinementNames = dao.findNames(config.get(Facet.WIDER_PROPERTY),
                sUri);

        // get the name of the currently selected node
        Refinement refinement = dao.getName(sUri);

        // create an initial state
        FacetStateImpl initialState = new FacetStateImpl();
        initialState.setName(refinement.getName());

        // go through the refinement names and count hits
        for (Refinement concept : refinementNames) {

            // create a facet state for the refinement
            FacetStateImpl fState = new FacetStateImpl();
            fState.setParamName(config.get(Facet.PARAM_NAME));

            // create a more readable parameter value than the full URI
            String uri = uriToParmeterValue(config.get(Facet.PREFIX), base, concept.getId());

            fState.setParamValue(uri);
            fState.setName(concept.getName());

            // get the filter for this refinement
            SearchFilter filter = new HierarchicalSearchFilterImpl(config.get(Facet.LINK_PROPERTY),
                    concept.getId());

            // count the hits for this refinement
            List<CountItem> refs = dao.countRefinements(coreFilterFragment
                    + filter.getSparqlFragment(), config.get(Facet.CONSTRAINT_TYPE));

            fState.setCount(refs.size());

            initialState.getRefinements().add(fState);

        }

        // a list to hold the parents a list of parents
        List<FacetStateImpl> parents = new ArrayList<FacetStateImpl>();

        // find the parent for facet
        Refinement parent = dao.findParents(config.get(Facet.WIDER_PROPERTY), sUri);

        parents.add(refinentmentToState(parent, config.get(Facet.PARAM_NAME),
                config.get(Facet.PREFIX), base));

        // find other parents
        while (parent != null) {
            parent = dao.findParents(config.get(Facet.WIDER_PROPERTY), parent.getId());

            if (parent != null) {
                parents.add(refinentmentToState(parent, config.get(Facet.PARAM_NAME),
                        config.get(Facet.PREFIX), base));
            }
        }

        // ensure that a parent is aware of its parent!
        for (int i = 0; i < (parents.size() - 1); i++) {
            parents.get(i).setParent(parents.get(i + 1));
        }

        // remove the param value fot the last parent - in the tag
        // library this will signal that if this parent is selected then all
        // refinements for this facet are to be removed
        parents.get(parents.size() - 1).setParamValue(null);

        initialState.setParent(parents.get(0));

        return initialState;
    }

    /**
     * <p>We use refinements to find the parent states of the current facet state. This method
     * convernts them to a FacetState so they can be represented correctly in the Facet class.</p>
     *
     * @param refinement a parent of the current facet state.
     * @param paramName  the parameter name used by this facet type.
     * @param prefix     the prefix used by this facet type.
     * @param base       the base uri used by this facet type.
     * @return a facet state representation of the refinement.
     */
    private FacetStateImpl refinentmentToState(Refinement refinement, String paramName,
                                               String prefix, String base) {

        FacetStateImpl state = new FacetStateImpl();
        state.setName(refinement.getName());
        state.setParamName(paramName);

        if (refinement.getId() != null) {
            state.setParamValue(uriToParmeterValue(prefix, base, refinement.getId()));
        }

        return state;
    }

    /**
     * <p>URIs are used as parameter values to indicate the state of a facet. URIs make the request
     * URL too long. The prefixes are used to replace part of the URI to make the parameter
     * value shorter.</p>
     *
     * @param prefix the prefix used by this facet.
     * @param base   the URI represented by the prefix.
     * @param uri    the URI representing a facet state.
     * @return a value that represents the URI of the facet state.
     */
    private String uriToParmeterValue(String prefix, String base, String uri) {
        return prefix + ":" + uri.substring(base.length());
    }

    /**
     * <p>Converts the value that represents a URI back to the original URI value.</p>
     *
     * @param paramValue the parameter value used to represent a URI.
     * @return the URI that reprsents a facet state.
     */
    private String parameterValueToUri(String paramValue) {
        String[] splitVal = paramValue.split(":");
        return nsPrefixes.get(splitVal[0]) + splitVal[1];
    }

    // DAO used to count refinements
    private RefinementDao dao;

    private Map<String, String> nsPrefixes;
}
