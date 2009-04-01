package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.RefinementDao;
import net.crew_vre.events.domain.facet.Refinement;
import net.crew_vre.jena.exception.DatasetFactoryException;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@gmail.com)
 * @version: $Id$
 */
public class RefinementCountDaoImplTest extends TestCase {

    private RefinementDao dao;

    @Override
    public void setUp() {
        try {
            DatasetFactory datasetFactory = new DatasetFactoryClasspathFileImpl(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);
            JenaQueryUtility jqUtility = new JenaQueryUtilityImpl();
            dao = new RefinementDaoImpl(datasetFactory, jqUtility);
        } catch (DatasetFactoryException ex) {
            ex.printStackTrace();
        }
    }

    public void testCountRefinementAlphaNumericWithEventType() {

        String type = "http://www.ilrt.bristol.ac.uk/iugo#MainEvent";
        String fragment = new StringBuilder()
                .append("?id <http://purl.org/dc/elements/1.1/title> ?name .\n")
                .append("FILTER (regex(str(?name), \"^t\", \"i\"))").toString();

        int count = dao.countRefinements(fragment, type).size();

        assertEquals("There should be 1 event refinement", 1, count);
    }

    public void testCountRefinementAlphaNumericWithPersonType() {

        String type = "http://xmlns.com/foaf/0.1/Person";
        String fragment = new StringBuilder()
                .append("?id <http://xmlns.com/foaf/0.1/family_name> ?name .\n")
                .append("FILTER (regex(str(?name), \"^b\", \"i\"))").toString();

        int count = dao.countRefinements(fragment, type).size();

        assertEquals("There should be 1 person refinement", 1, count);
    }

    public void testLocationNames() {

        String widerProperty = "http://www.w3.org/2004/02/skos/core#broader";
        String rootConcept = "http://www.ilrt.bristol.ac.uk/iugo/location/#locations";

        List<Refinement> refinements = dao.findNames(widerProperty, rootConcept);

        //for (Refinement refinement : refinements) {
        //    System.out.println("> " + refinement.getName());
        //}

        assertNotNull("The list of refinements should not be null", refinements);
        assertEquals("There should be 7 continents", 7, refinements.size());

    }

}
