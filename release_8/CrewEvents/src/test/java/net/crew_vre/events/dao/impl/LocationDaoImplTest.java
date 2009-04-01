package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.LocationDao;
import net.crew_vre.events.domain.Location;

import java.io.IOException;
import java.util.List;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: LocationDaoImplTest.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class LocationDaoImplTest extends TestCase {

    private LocationDao locationDao;

    @Override
    public void setUp() {
        try {
            Database database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);
            locationDao = new LocationDaoImpl(database);
        } catch (IOException e) {
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
