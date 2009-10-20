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
import net.crew_vre.web.facet.DateTimeFacetFactory;
import net.crew_vre.web.facet.Facet;
import net.crew_vre.web.facet.FacetState;
import net.crew_vre.web.facet.SearchFilter;
import org.caboto.jena.db.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>A factory for creating DateTime facets with a state and possible refinements.</p>
 * <p>The SPARQL filters the dates as strings rather than anything sophisticted with xsd:date</p>
 * <p>The configuration details of a facet are held in a map. The keys that are available to this
 * facet type are:</p>
 * <p/>
 * <ul>
 * <li><em>facetType</em> - the facet type, e.g. DateTime.</li>
 * <li><em>facetTitle</em> - the title of the facet, e.g. "Event Date"</li>
 * <li><em>linkProperty</em> - the RDF link property,
 * e.g. http://www.eswc2006.org/technologies/ontology#hasStartDateTime</li>
 * <li><em>constraintType</em> - the RDF type used to constrain searches, e.g.
 * http://www.ilrt.bristol.ac.uk/iugo#MainEvent</li>
 * <li><em>paramName</em> - the parameter name that will represent this facet in
 * a request URL, e.g. "eventDateTime"</li>
 * <li><em>startYear</em> - the start year for the facets, e.g. "2007"</li>
 * <li><em>endYear</em> - the end year for the facets, e.g. "2010"</li>
 * </ul>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: DateTimeFacetFactoryImpl.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class DateTimeFacetFactoryImpl implements DateTimeFacetFactory {

    // ---------- CONSTRUCTOR

    /**
     * <p>Constructor</p>
     *
     * @param dao      the DAO used to fine refinements for the facet.
     * @param monthMap map holding monnth labels.
     */
    public DateTimeFacetFactoryImpl(final RefinementDao dao,
                                    final Map<String, String> monthMap) {
        this.dao = dao;
        this.monthMap = monthMap;
    }


    // ---------- PUBLIC METHODS

    /**
     * <p>Create a facet with no constraints and is not constrained by other facets.</p>
     *
     * @param config a Map holding the configuration details for the facet.
     * @return a facet implementation with no constraints.
     */
    public Facet create(final Map<String, String> config, Data data) {
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
                        Data data) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, searchFilters, data));
        return facet;
    }

    /**
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of filters that provide constraints.
     * @param dateString    a string reprsenting the facet state.
     * @return a facet state reprsented by the URI.
     */
    public Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters,
                        final String dateString, Data data) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, searchFilters, dateString, data));
        return facet;
    }

    // ---------- PRIVATE METHODS

    /**
     * <p>Creates an initial state for the facet. This method is called when no facets have
     * been selected so each refinement is called without any constraints (except for those
     * specified in the configuration for the facet.</p>
     *
     * @param config a Map holding the configuration details for the facet.
     * @return a facet state.
     */
    private FacetState createInitialState(final Map<String, String> config, Data data) {

        // create the initial facet (hidden in the UI)
        FacetStateImpl initalFacetState = (FacetStateImpl) createState(config, null, data);
        initalFacetState.setName(config.get(Facet.FACET_BASE));
        return initalFacetState;
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

        // create the initial state
        FacetState state = new FacetStateImpl();

        // create the SPARQL filter fragment
        SearchFilter filter = new DateTimeSearchFilterImpl(config.get(Facet.PARAM_NAME),
                config.get(Facet.LINK_PROPERTY));

        String sparql = filter.getSparqlFragment();

        // create a string builder of sparql fragments that represent the constraints
        if (searchFilters != null) {

            StringBuilder coreFilterFragment = new StringBuilder();

            for (SearchFilter f : searchFilters) {
                coreFilterFragment.append(f.getSparqlFragment());
            }

            sparql = sparql + coreFilterFragment.toString();
        }

        //System.out.println("MMMMMM " + sparql);

        // get the refinements
        List<FacetState> refinements = getYearRefinements(sparql, config.get(Facet.CONSTRAINT_TYPE),
                config.get(Facet.PARAM_NAME), data);

        // add refinements to the state
        state.getRefinements().addAll(refinements);

        return state;
    }


    private List<FacetState> getYearRefinements(String sparqlFrament, String constraintType,
                                                String paramName, Data data) {

        // hold the refinements
        List<FacetState> refinements = new ArrayList<FacetState>();

        // get a list of all the start dates
        List<CountItem> refs = dao.countRefinements(sparqlFrament, constraintType, paramName, data);

        // hash to hold refinement counts
        Map<String, Integer> countMap = count(refs, 0, 4);

        // get the keys  in order ... I suspect this is horribly inefficient
        List<String> keys = new ArrayList<String>();
        keys.addAll(countMap.keySet());
        Collections.sort(keys, Collections.reverseOrder());

        // calculate the refinements and add it to the state
        for (String val : keys) {

            FacetState state = new FacetStateImpl(val, countMap.get(val),
                    paramName, val);

            refinements.add(state);
        }

        return refinements;
    }


    private List<FacetState> getYearMonthRefinement(String sparqlFragment, String constraintType,
                                                    String paramName, String dateString,
                                                    Data data) {
        // hold the refinements
        List<FacetState> refinements = new ArrayList<FacetState>();

        // get a list of all the start dates
        List<CountItem> refs = dao.countRefinements(sparqlFragment, constraintType, paramName,
                data);

        // hash to hold refinement counts
        Map<String, Integer> countMap = count(refs, 5, 7);

        // get the keys  in order ... I suspect this is horribly inefficient
        List<String> keys = new ArrayList<String>();
        keys.addAll(countMap.keySet());
        Collections.sort(keys);

        // calculate the refinements and add it to the state
        for (String val : keys) {

            // get the label for the month
            String label = monthMap.get(val);

            // we need a string that can be used in the filter (YYYY-MM)
            String yearMonthLabel = dateString + "-" + val;

            FacetStateImpl state = new FacetStateImpl();
            state.setName(label);
            state.setParamName(paramName);
            state.setParamValue(yearMonthLabel);
            state.setCount(countMap.get(val));
            refinements.add(state);
        }

        return refinements;

    }

    private List<FacetState> getYearMonthDayRefinement(String sparqlFrament, String constraintType,
                                                       String paramName, String dateString,
                                                       Data data) {
        // hold the refinements
        List<FacetState> refinements = new ArrayList<FacetState>();

        // get a list of all the start dates
        List<CountItem> refs = dao.countRefinements(sparqlFrament, constraintType, paramName, data);

        // hash to hold refinement counts
        Map<String, Integer> countMap = count(refs, 8, 10);

        // get the keys  in order ... I suspect this is horribly inefficient
        List<String> keys = new ArrayList<String>();
        keys.addAll(countMap.keySet());
        Collections.sort(keys);

        // calculate the refinements and add it to the state
        for (String val : keys) {

            String yearMonthDay = dateString + "-" + val;

            FacetStateImpl state = new FacetStateImpl();
            state.setName(val);
            state.setParamName(paramName);
            state.setParamValue(yearMonthDay);
            state.setCount(countMap.get(val));

            refinements.add(state);
        }

        return refinements;

    }


    private Map<String, Integer> count(List<CountItem> items, int start, int end) {

        Map<String, Integer> countMap = new HashMap<String, Integer>();

        for (CountItem item : items) {

            String key = item.getValue().substring(start, end);

            if (countMap.containsKey(key)) {
                Integer count = countMap.get(key);
                count++;
                countMap.put(key, count);
            } else {
                countMap.put(key, 1);
            }
        }

        return countMap;
    }

    /**
     * <p>Creates a facet state - the facet is in state other than its base state.</p>
     *
     * @param config        a Map holding the configuration details for the facet.
     * @param searchFilters a List of SPARQL fragments that provide the constraints.
     * @param dateString    the URI that represents a state for this facet.
     * @return a facet state.
     */
    private FacetState createState(final Map<String, String> config,
                                   final List<SearchFilter> searchFilters,
                                   final String dateString, Data data) {;

        // create a string builder of sparql fragments that represent the constraints
        StringBuilder coreFilterFragment = new StringBuilder();

        for (SearchFilter f : searchFilters) {
            coreFilterFragment.append(f.getSparqlFragment());
        }

        // create enlarged sparql fragment
        String sparql = coreFilterFragment.toString();

        // are we just dealing with a year (YYYY)?
        if (yearPattern.matcher(dateString).matches()) {

            return handleYearPattern(sparql, config.get(Facet.CONSTRAINT_TYPE),
                    config.get(Facet.PARAM_NAME), dateString, data);

        } else if (yearMonthPattern.matcher(dateString).matches()) { // YYYY-MM?

            return handleYearMonthPattern(sparql, config.get(Facet.CONSTRAINT_TYPE),
                    config.get(Facet.PARAM_NAME), dateString, data);

        } else if (yearMonthDayPattern.matcher(dateString).matches()) { // YYYY-MM-DD???

            return handleYearMonthDayPattern(dateString, config.get(Facet.PARAM_NAME));

        } else {
            throw new RuntimeException("The values for the parameter are unexected. "
                    + "They should be numeric in the format YYYY, YYYY-MM or YYYY-MM-DD."
                    + "Received: " + dateString);
        }
    }


    private FacetStateImpl handleYearPattern(String sparql, String constraintType,
                                             String paramName, String dateString, Data data) {
        // get a list of states
        List<FacetState> states = getYearMonthRefinement(sparql,
                constraintType, paramName,
                dateString, data);


        FacetStateImpl initialState = new FacetStateImpl();
        initialState.setName(dateString);
        initialState.getRefinements().addAll(states);

        FacetStateImpl parent = new FacetStateImpl();
        parent.setName(dateString);
        parent.setParamName(paramName);

        initialState.setParent(parent);

        return initialState;
    }

    private FacetState handleYearMonthPattern(String sparql, String constraintType,
                                              String paramName, String dateString, Data data) {

        // get a list of states
        List<FacetState> states = getYearMonthDayRefinement(sparql, constraintType, paramName,
                dateString, data);

        // get the month label ...
        String[] val = dateString.split("-");
        String monthLabel = monthMap.get(val[1]);

        // ... if null (dodgy input, throw an error)
        if (monthLabel == null) {
            throw new RuntimeException("The parameter value that represents a month - "
                    + val[1] + " - cannot be resolved to a label.");
        }

        // create the initial state (month)
        FacetStateImpl initialState = new FacetStateImpl();
        initialState.getRefinements().addAll(states);
        initialState.setName(monthLabel);

        // create the root state
        FacetStateImpl root = new FacetStateImpl();
        root.setParamName(paramName);

        // create year state
        FacetStateImpl year = new FacetStateImpl();
        year.setName(val[0]);
        year.setParamName(paramName);
        year.setParamValue(val[0]);
        year.setParent(root);

        initialState.setParent(year);

        return initialState;
    }


    private FacetState handleYearMonthDayPattern(String dateString, String paramName) {

        // split the request parameter to get year, month, day
        String[] vals = dateString.split("-");
        String year = vals[0];
        String month = vals[1];
        String day = vals[2];

        // create an empty root facet
        FacetStateImpl rootFacet = new FacetStateImpl();
        rootFacet.setParamName(paramName);

        // create a facet to represent the year
        FacetStateImpl yearFacet = new FacetStateImpl();
        yearFacet.setName(year);
        yearFacet.setParamName(paramName);
        yearFacet.setParamValue(year);

        // create a facet for the month
        FacetStateImpl monthFacet = new FacetStateImpl();
        monthFacet.setName(monthMap.get(month));
        monthFacet.setParamName(paramName);
        monthFacet.setParamValue(year + "-" + month);

        // set the day details
        FacetStateImpl initialState = new FacetStateImpl();
        initialState.setName(day);
        initialState.setParamName(paramName);
        initialState.setParamValue(year + "-" + month + "day");

        // add the parent relationships
        initialState.setParent(monthFacet);
        monthFacet.setParent(yearFacet);
        yearFacet.setParent(rootFacet);

        // return the initial state ... day in this case
        return initialState;
    }


    // DAO used to count refinements
    private RefinementDao dao;

    // map of months
    private Map<String, String> monthMap;

    // regex patterns to check the parameter vales
    private Pattern yearPattern = Pattern.compile("^\\d{4}$");
    private Pattern yearMonthPattern = Pattern.compile("^\\d{4}\\-\\d{2}$");
    private Pattern yearMonthDayPattern = Pattern.compile("^\\d{4}\\-\\d{2}\\-\\d{2}$");
}
