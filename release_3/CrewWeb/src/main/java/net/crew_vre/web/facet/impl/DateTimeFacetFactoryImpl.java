package net.crew_vre.web.facet.impl;

import net.crew_vre.events.dao.RefinementDao;
import net.crew_vre.events.domain.facet.CountItem;
import net.crew_vre.web.facet.DateTimeFacetFactory;
import net.crew_vre.web.facet.Facet;
import net.crew_vre.web.facet.FacetState;
import net.crew_vre.web.facet.SearchFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>HERE BE DRAGONS!</p>
 * <p/>
 * <p>A factory for creating DateTime facets with a state and possible refinements.</p>
 * <p/>
 * <p>Some icky things:</p>
 * <p/>
 * <ul>
 * <li>The SPARQL filters the dates as strings rather than anything sophisticted with
 * xsd:dateTime</li>
 * <li>The year range is determimed by the facets configuration rather than the data
 * itself.</li>
 * <li>The months are created by a for loop of iterating over 1 to 12.</li>
 * <li>The days are looped from 1 to 31 and this doesn't take into account that there might
 * not be 31 days in a month. A filter with ^2008-02-31 will just not return any results.
 * This needs sorting though.</li>
 * </ul>
 * <p/>
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
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DateTimeFacetFactoryImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DateTimeFacetFactoryImpl implements DateTimeFacetFactory {

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

    /**
     * <p>Create a facet with no constraints and is not constrained by other facets.</p>
     *
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
     * @param dateString    a string reprsenting the facet state.
     * @return a facet state reprsented by the URI.
     */
    public Facet create(final Map<String, String> config, final List<SearchFilter> searchFilters,
                        final String dateString) {
        FacetImpl facet = new FacetImpl(config.get(Facet.FACET_TITLE));
        facet.setState(createState(config, searchFilters, dateString));
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
    private FacetState createInitialState(final Map<String, String> config) {

        // create the initial facet (hidden in the UI)
        FacetStateImpl initialState = new FacetStateImpl();
        initialState.setName(config.get(Facet.FACET_BASE));

        List<FacetState> refinements = new ArrayList<FacetState>();

        for (Integer year = new Integer(config.get(Facet.START_YEAR));
             year <= new Integer(config.get(Facet.END_YEAR)); year++) {

            // create the label
            String label = year.toString();

            // create the SPARQL filter fragment for this character
            SearchFilter filter = new DateTimeSearchFilterImpl(config.get(Facet.PARAM_NAME),
                    config.get(Facet.LINK_PROPERTY), label);

            // count the number of possible refinements for this character
            List<CountItem> refs = dao.countRefinements(filter.getSparqlFragment(),
                    config.get(Facet.CONSTRAINT_TYPE));

            // create a new facet state
            FacetState state = new FacetStateImpl(label, refs.size(), config.get(Facet.PARAM_NAME),
                    label);

            refinements.add(state);

        }

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
                                   final List<SearchFilter> searchFilters) {

        // create a string builder of sparql fragments that
        // represent the constraints
        StringBuilder coreFilterFragment = new StringBuilder();

        for (SearchFilter filter : searchFilters) {
            coreFilterFragment.append(filter.getSparqlFragment());
        }

        // create the initial state
        FacetState initialState = new FacetStateImpl();
        List<FacetState> refinements = new ArrayList<FacetState>();

        for (Integer year = new Integer(config.get(Facet.START_YEAR));
             year <= new Integer(config.get(Facet.END_YEAR)); year++) {

            // create the label
            String label = year.toString();

            // create the SPARQL filter fragment for this string (YYYY)
            SearchFilter filter = new DateTimeSearchFilterImpl(config.get(Facet.PARAM_NAME),
                    config.get(Facet.LINK_PROPERTY), label);

            // count the number of possible refinements for this character
            List<CountItem> refs = dao.countRefinements(filter.getSparqlFragment()
                    + coreFilterFragment.toString(), config.get(Facet.CONSTRAINT_TYPE));

            // create a new facet state
            FacetState state = new FacetStateImpl(label, refs.size(), config.get(Facet.PARAM_NAME),
                    label);

            refinements.add(state);

        }

        // add refinements to the state
        initialState.getRefinements().addAll(refinements);

        return initialState;
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
                                   final String dateString) {

        FacetStateImpl initialState = new FacetStateImpl();
        List<FacetState> refinements = new ArrayList<FacetState>();

        // create a string builder of sparql fragments that
        // represent the constraints
        StringBuilder coreFilterFragment = new StringBuilder();

        for (SearchFilter filter : searchFilters) {
            coreFilterFragment.append(filter.getSparqlFragment());
        }

        // are we just dealing with a year (YYYY)?
        if (yearPattern.matcher(dateString).matches()) {

            // check the validity of the year
            checkYear(dateString, config.get(Facet.START_YEAR), config.get(Facet.END_YEAR));

            // go through the months...
            for (int month = 1; month <= MAX_MONTHS; month++) {

                String monthLabel;
                String label;

                // we might need a leading 0
                if (month < TEN_VALUE) {
                    monthLabel = "0" + Integer.toString(month);
                } else {
                    monthLabel = Integer.toString(month);
                }

                // get the label for the month
                label = monthMap.get(monthLabel);

                // we need a string that can be used in the filter (YYYY-MM)
                String yearMonthLabel = dateString + "-" + monthLabel;

                SearchFilter filter = new DateTimeSearchFilterImpl(config.get(Facet.PARAM_NAME),
                        config.get(Facet.LINK_PROPERTY), yearMonthLabel);

                // count the number of possible refinements for this string
                List<CountItem> refs = dao.countRefinements(filter.getSparqlFragment()
                        + coreFilterFragment.toString(), config.get(Facet.CONSTRAINT_TYPE));

                // create a facet state for each month
                FacetStateImpl state = new FacetStateImpl();
                state.setName(label);
                state.setParamName(config.get(Facet.PARAM_NAME));
                state.setParamValue(yearMonthLabel);
                state.setCount(refs.size());

                refinements.add(state);

            }

            initialState.setName(dateString);
            initialState.getRefinements().addAll(refinements);

            FacetStateImpl parent = new FacetStateImpl();
            parent.setName(dateString);
            parent.setParamName(config.get(Facet.PARAM_NAME));

            initialState.setParent(parent);


        } else if (yearMonthPattern.matcher(dateString).matches()) { // YYYY-MM?

            String[] val = dateString.split("-");

            // check the validity of the year
            checkYear(val[0], config.get(Facet.START_YEAR), config.get(Facet.END_YEAR));

            // get the month label ...
            String monthLabel = monthMap.get(val[1]);

            // ... if null (dodgy input, throw an error)
            if (monthLabel == null) {
                throw new RuntimeException("The parameter value that represents a month - "
                        + val[1] + " - cannot be resolved to a label.");
            }

            // get the days of the month ... yes, 1 to 31 is a bit lazy
            for (int day = 1; day <= MAX_DAYS; day++) {

                String dayLabel;

                if (day < TEN_VALUE) {
                    dayLabel = "0" + Integer.toString(day);
                } else {
                    dayLabel = Integer.toString(day);
                }

                String yearMonthDay = dateString + "-" + dayLabel;

                SearchFilter filter = new DateTimeSearchFilterImpl(config.get(Facet.PARAM_NAME),
                        config.get(Facet.LINK_PROPERTY), yearMonthDay);

                // count the number of possible refinements for this character
                List<CountItem> refs = dao.countRefinements(filter.getSparqlFragment()
                        + coreFilterFragment.toString(), config.get(Facet.CONSTRAINT_TYPE));

                FacetStateImpl state = new FacetStateImpl();
                state.setName(Integer.toString(day));
                state.setParamName(config.get(Facet.PARAM_NAME));
                state.setParamValue(yearMonthDay);
                state.setCount(refs.size());

                refinements.add(state);
            }

            initialState.getRefinements().addAll(refinements);
            initialState.setName(monthLabel);

            FacetStateImpl root = new FacetStateImpl();
            root.setParamName(config.get(Facet.PARAM_NAME));

            FacetStateImpl year = new FacetStateImpl();
            year.setName(val[0]);
            year.setParamName(config.get(Facet.PARAM_NAME));
            year.setParamValue(val[0]);

            year.setParent(root);

            initialState.setParent(year);

        } else if (yearMonthDayPattern.matcher(dateString).matches()) { // YYYY-MM-DD???

            // split the request parameter to get year, month, day
            String[] vals = dateString.split("-");

            String year = vals[0];
            String month = vals[1];
            String day = vals[2];

            // a an empty root facet
            FacetStateImpl rootFacet = new FacetStateImpl();
            rootFacet.setParamName(config.get(Facet.PARAM_NAME));

            // a facet to represent the year
            FacetStateImpl yearFacet = new FacetStateImpl();
            yearFacet.setName(year);
            yearFacet.setParamName(config.get(Facet.PARAM_NAME));
            yearFacet.setParamValue(year);

            // a facet for the month
            FacetStateImpl monthFacet = new FacetStateImpl();
            monthFacet.setName(monthMap.get(month));
            monthFacet.setParamName(config.get(Facet.PARAM_NAME));
            monthFacet.setParamValue(year + "-" + month);

            // set the day details
            initialState.setName(day);
            initialState.setParamName(config.get(Facet.PARAM_NAME));
            initialState.setParamValue(year + "-" + month + "day");

            // add the parent relationships
            initialState.setParent(monthFacet);
            monthFacet.setParent(yearFacet);
            yearFacet.setParent(rootFacet);
        } else {
            throw new RuntimeException("The values for the parameter are unexected. "
                    + "They should be numeric in the format YYYY, YYYY-MM or YYYY-MM-DD");
        }

        return initialState;
    }


    /**
     * <p>Checks that the year specified in the parameter is actually withing the bounds set
     * for the facet - will only return false if people have been changing the values in the
     * request.</p>
     *
     * @param yearValue the year specified in the request parameter.
     * @param startYear the start year specified by the system.
     * @param endYear   the end year specified by the system.
     */
    void checkYear(final String yearValue, final String startYear, final String endYear) {
        if (Integer.valueOf(yearValue) < Integer.valueOf(startYear)
                || Integer.valueOf(yearValue) > Integer.valueOf(endYear)) {
            throw new RuntimeException("The year specified in the parameter is out of "
                    + "bounds: " + yearValue + ". The range is " + startYear
                    + " to " + endYear + ".");
        }
    }


    // DAO used to count refinements
    private RefinementDao dao;

    // map of months
    private Map<String, String> monthMap;

    // regex patterns to check the parameter vales
    private Pattern yearPattern = Pattern.compile("^\\d{4}$");
    private Pattern yearMonthPattern = Pattern.compile("^\\d{4}\\-\\d{2}$");
    private Pattern yearMonthDayPattern = Pattern.compile("^\\d{4}\\-\\d{2}\\-\\d{2}$");

    private static final int MAX_MONTHS = 12;
    private static final int TEN_VALUE = 10;
    private static final int MAX_DAYS = 31;
}
