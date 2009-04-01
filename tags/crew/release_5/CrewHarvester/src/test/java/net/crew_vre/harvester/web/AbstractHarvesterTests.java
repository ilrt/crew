package net.crew_vre.harvester.web;

import junit.framework.TestCase;
import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.impl.HarvestSourceImpl;
import org.jmock.Mockery;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: AbstractHarvesterTests.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class AbstractHarvesterTests extends TestCase {

    @Before
    public void setUp() {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        context = new Mockery();

    }

    List<HarvestSource> createTestSources() {

        final List<HarvestSource> sources = new ArrayList<HarvestSource>();
        sources.add(new HarvestSourceImpl(sourceOneUri, null, null, null, null, false));
        sources.add(new HarvestSourceImpl(sourceTwoUri, null, null, null, null, false));
        return sources;

    }

    private final String sourceOneUri = "http://test.org/source1.rdf";
    private final String sourceTwoUri = "http://test.org/source1.rdf";

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    Mockery context = null;
}
