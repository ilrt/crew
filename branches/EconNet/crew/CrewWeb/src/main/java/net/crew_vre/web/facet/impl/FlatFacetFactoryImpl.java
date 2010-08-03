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

import org.caboto.jena.db.Data;

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
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: FlatFacetFactoryImpl.java 1191 2009-03-31 13:38:51Z cmmaj $
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
    public Facet create(final Map<String, String> config, Data data) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createInitialState(config, data));
        return facet;
    }

    /**
     * <p>Creates a facet with no constrants but is constrained by other facets.</p>
     *
     * @param config            a Map holding the configuration details for the facet.
     * @param searchFilters     a List of filters that provide constraints.
     * @return a facet implentation with constraints.
     */
    public Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters,
                        Data data) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, searchFilters, data));
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
    public Facet create(final Map<String, String> config, final String selectedUri, Data data) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, selectedUri, data));
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
    private FacetState createInitialState(final Map<String, String> config, Data data) {

        List<Refinement> names = dao.findProperties(config.get(Facet.LINK_PROPERTY), data);

        FacetState initialState = new FacetStateImpl();

        for (Refinement name : names) {

            // create the SPARQL filter fragment for this character
            SearchFilter filter = new FlatSearchFilterImpl(config.get(Facet.LINK_PROPERTY),
                    name.getId());

            List<CountItem> refs = dao.countRefinements(filter.getSparqlFragment(),
                    config.get(Facet.CONSTRAINT_TYPE), null, data);

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
                                   final List<SearchFilter> searchFilters, Data data) {

        // create a string builder of sparql fragments that
        // represent the constraints
        StringBuilder coreFilterFragment = new StringBuilder();

        for (SearchFilter filter : searchFilters) {
            coreFilterFragment.append(filter.getSparqlFragment());
        }

        List<Refinement> names = dao.findProperties(config.get(Facet.LINK_PROPERTY), data);

        FacetState initialState = new FacetStateImpl();

        for (Refinement name : names) {

            // create the SPARQL filter fragment for this character
            SearchFilter filter = new FlatSearchFilterImpl(config.get(Facet.LINK_PROPERTY),
                    name.getId());

            List<CountItem> refs = dao.countRefinements(filter.getSparqlFragment()
                    + coreFilterFragment.toString(), config.get(Facet.CONSTRAINT_TYPE), null, data);

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

    private FacetState createState(final Map<String, String> config, final String selectedUri,
                                   Data data) {

        // create a blank node to hold the facet details
        FacetStateImpl blankRoot = new FacetStateImpl();
        blankRoot.setParamName(config.get(Facet.PARAM_NAME));
        String uri = utility.parameterValueToUri(selectedUri);
        Refinement name = dao.getName(uri, data);

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
