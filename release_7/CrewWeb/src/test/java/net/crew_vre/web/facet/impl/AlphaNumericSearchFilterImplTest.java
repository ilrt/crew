package net.crew_vre.web.facet.impl;

import junit.framework.TestCase;
import net.crew_vre.web.facet.SearchFilter;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: AlphaNumericSearchFilterImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class AlphaNumericSearchFilterImplTest extends TestCase {

    public void testGetSearchFilter() {

        String property = "dc:title";
        String constraint = "C*";
        String paramName = "name";
        String match = "?id <dc:title> ?name .\nFILTER(regex(str(?name), \"^C\", \"i\")) .\n";

        SearchFilter filter = new AlphaNumericSearchFilterImpl(paramName, property, constraint);

        assertNotNull("The sparql fragment shouldn't be null", filter);
        assertEquals("Unexpected filter", match, filter.getSparqlFragment());
    }

}
