package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.LocationDao;
import net.crew_vre.events.domain.Location;
import net.crew_vre.jena.exception.DatasetFactoryException;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: LocationDaoImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class LocationDaoImplTest extends TestCase {

    private LocationDao locationDao;

    @Override
    public void setUp() {
        try {
            DatasetFactory datasetFactory =
                    new DatasetFactoryClasspathFileImpl(Constants.DEFAULT_MODEL,
                            Constants.NAMED_GRAPHS);
            locationDao = new LocationDaoImpl(datasetFactory, new JenaQueryUtilityImpl());
        } catch (DatasetFactoryException e) {
            e.printStackTrace();
        }
    }

    public void testFindLocationByEvent() {

        List<Location> results = locationDao.findLocationByEvent(Constants.EVENT_ONE_ID);
        assertNotNull("The results should not be null", results);

        for (Location location : results) {
           assertNotNull("The graph should not be null", location.getGraph());
           assertNotNull("The name of the location should not be null", location.getName());
        }

        assertEquals("There are 5 locations", 5, results.size());
    }
}
