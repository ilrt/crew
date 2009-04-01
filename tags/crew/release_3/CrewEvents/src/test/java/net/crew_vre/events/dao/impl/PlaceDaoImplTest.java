package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.domain.Place;
import net.crew_vre.jena.exception.DatasetFactoryException;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PlaceDaoImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class PlaceDaoImplTest extends TestCase {

    private PlaceDaoImpl placeDao;

    @Override
    public void setUp() {
        try {
            DatasetFactory datasetFactory =
                    new DatasetFactoryClasspathFileImpl(Constants.DEFAULT_MODEL,
                            Constants.NAMED_GRAPHS);
            placeDao = new PlaceDaoImpl(datasetFactory, new JenaQueryUtilityImpl());
        } catch (DatasetFactoryException e) {
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
