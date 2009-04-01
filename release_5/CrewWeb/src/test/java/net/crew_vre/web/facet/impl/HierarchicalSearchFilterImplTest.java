package net.crew_vre.web.facet.impl;

import junit.framework.TestCase;
import net.crew_vre.web.facet.SearchFilter;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: HierarchicalSearchFilterImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class HierarchicalSearchFilterImplTest extends TestCase {

    public void testGetSearchFilterSubject() {

        String property = "http://www.eswc2006.org/technologies/ontology#hasSubject";
        String objectUri = "http://www.ilrt.bristol.ac.uk/iugo/location/#durham";

        SearchFilter filter = new HierarchicalSearchFilterImpl(property, objectUri);

        assertNotNull("The sparql fragment shouldn't be null", filter);
        assertEquals("Unexpected filter",
                "?id <" + property + "> <" + objectUri + "> .\n", filter.getSparqlFragment());
    }

}
