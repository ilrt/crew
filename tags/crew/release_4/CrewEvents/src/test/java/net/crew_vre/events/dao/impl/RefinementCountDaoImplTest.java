package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.RefinementDao;
import net.crew_vre.events.domain.facet.Refinement;

import java.io.IOException;
import java.util.List;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.Data;
import org.caboto.jena.db.impl.FileDatabase;

/**
 * @author: Mike Jones (mike.a.jones@gmail.com)
 * @version: $Id$
 */
public class RefinementCountDaoImplTest extends TestCase {

    private RefinementDao dao;

    @Override
    public void setUp() {
        try {
            database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);
            dao = new RefinementDaoImpl();


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void testCountRefinementAlphaNumericWithEventType() throws Exception {

        String type = "http://www.ilrt.bristol.ac.uk/iugo#MainEvent";
        String fragment = new StringBuilder()
                .append("?id <http://purl.org/dc/elements/1.1/title> ?name .\n")
                .append("FILTER (regex(str(?name), \"^t\", \"i\"))").toString();

        int count = dao.countRefinements(fragment, type, database.getData()).size();

        assertEquals("There should be 1 event refinement", 1, count);
    }

    public void testCountRefinementAlphaNumericWithPersonType() throws Exception {

        String type = "http://xmlns.com/foaf/0.1/Person";
        String fragment = new StringBuilder()
                .append("?id <http://xmlns.com/foaf/0.1/family_name> ?name .\n")
                .append("FILTER (regex(str(?name), \"^b\", \"i\"))").toString();

        int count = dao.countRefinements(fragment, type, database.getData()).size();

        assertEquals("There should be 1 person refinement", 1, count);
    }

    public void testLocationNames() throws Exception {

        String widerProperty = "http://www.w3.org/2004/02/skos/core#broader";
        String rootConcept = "http://www.ilrt.bristol.ac.uk/iugo/location/#locations";

        List<Refinement> refinements = dao.findNames(widerProperty, rootConcept,
                database.getData());

        //for (Refinement refinement : refinements) {
        //    System.out.println("> " + refinement.getName());
        //}

        assertNotNull("The list of refinements should not be null", refinements);
        assertEquals("There should be 7 continents", 7, refinements.size());

    }

    Database database;

}
