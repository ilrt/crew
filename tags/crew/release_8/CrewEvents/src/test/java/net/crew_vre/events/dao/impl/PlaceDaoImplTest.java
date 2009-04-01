package net.crew_vre.events.dao.impl;

import java.io.IOException;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.domain.Place;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PlaceDaoImplTest.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class PlaceDaoImplTest extends TestCase {

    private PlaceDaoImpl placeDao;

    @Override
    public void setUp() {
        try {
            Database database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);
            placeDao = new PlaceDaoImpl(database);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void testFindplaceById() {

        Place place = placeDao.findPlaceById(Constants.PLACE_ONE_ID);
        assertNotNull(place);
        assertNotNull("The graph should not be null", place.getGraph());
        assertEquals("The id should be " + Constants.PLACE_ONE_ID, Constants.PLACE_ONE_ID,
                place.getId());
        assertNotNull("Latitude should not be null", place.getLatitude());
        assertNotNull("Longitude should not be null", place.getLongitude());
        assertTrue("The place should have another place as a part",
                place.getLocations().size() > 0);

        for (Place p : place.getLocations()) {
            assertNotNull("The graph should not be null", place.getGraph());
        }

    }
}
