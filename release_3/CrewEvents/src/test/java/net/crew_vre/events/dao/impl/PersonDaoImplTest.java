package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.dao.PersonDao;
import net.crew_vre.events.domain.Person;
import net.crew_vre.jena.exception.DatasetFactoryException;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PersonDaoImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class PersonDaoImplTest extends TestCase {

    private PersonDao personDao;

    @Override
    public void setUp() {
        try {
            DatasetFactory datasetFactory =
                    new DatasetFactoryClasspathFileImpl(Constants.DEFAULT_MODEL,
                            Constants.NAMED_GRAPHS);
            JenaQueryUtility jqUtility = new JenaQueryUtilityImpl();
            EventDao eventDao = new EventDaoImpl(datasetFactory, jqUtility);
            personDao = new PersonDaoImpl(datasetFactory, jqUtility, eventDao);
        } catch (DatasetFactoryException e) {
            e.printStackTrace();
        }


    }

    public void testFindPersonById() {
        Person person = personDao.findPersonById(Constants.PERSON_ONE_ID);
        assertNotNull("The graph should not be null", person.getGraph());
        assertNotNull("The given name should not be null", person.getGivenName());
        assertNotNull("The family name should not be null", person.getFamilyName());
        assertNotNull("The homepage should not be null", person.getHomepage());
        assertNotNull("The workplace homepage should not be null", person.getWorkplaceHomepage());
        assertNotNull("The flickr homepage should not be null", person.getFlickrHomepage());
        assertNotNull("The roles should not be null", person.getRoles());
        assertTrue("The roles should not be empty", person.getRoles().size() > 0);
    }

    public void testFindAllPeople() {
        List<Person> results = personDao.findAllPeople();

        for (Person p : results) {
            assertNotNull("The graph should not be null", p.getGraph());
        }

        assertNotNull("The results should not be null", results);
        assertTrue("There should be more than 0 people", results.size() > 0);
    }


    public void testFindAllPeopleWithOffset() {
        List<Person> results = personDao.findAllPeople(10, 0);

        for (Person p : results) {
            assertNotNull("The graph should not be null", p.getGraph());
        }

        assertNotNull("The results should not be null", results);
        assertTrue("There should be more than 0 people", results.size() > 0);
    }

    public void testFindPeopleByEvent() {
        List<Person> results = personDao.findPeopleByEvent(Constants.EVENT_ONE_ID);

        for (Person p : results) {
            assertNotNull("The graph should not be null", p.getGraph());
        }

        assertNotNull("The results should not be null", results);
        assertTrue("There should be more than 0 people", results.size() > 0);
    }

    public void testFindPeopleByEventWithOffset() {
        List<Person> results = personDao.findPeopleByEvent(Constants.EVENT_ONE_ID, 10, 0);

        for (Person p : results) {
            assertNotNull("The graph should not be null", p.getGraph());
        }

        assertNotNull("The results should not be null", results);
        assertTrue("There should be more than 0 people", results.size() > 0);
    }

    public void testFindAuthors() {
        List<Person> results = personDao.findAuthors(Constants.PAPER_ONE_ID);

        for (Person p : results) {
            assertNotNull("The graph should not be null", p.getGraph());
        }

        assertNotNull("The results should not be null", results);
    }

}
