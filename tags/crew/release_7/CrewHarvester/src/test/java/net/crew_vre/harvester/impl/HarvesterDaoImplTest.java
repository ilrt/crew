package net.crew_vre.harvester.impl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.store.StoreFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import junit.framework.TestCase;
import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterDao;
import net.crew_vre.harvester.Vocab;
import org.caboto.jena.db.impl.SDBDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvesterDaoImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class HarvesterDaoImplTest extends TestCase {

    @Test
    public void testTemp() {
        assertTrue(true);
    }

    /**
    @Before
    public void setUp() throws Exception {

        System.out.println("SETUP");

        // create a store and format - this will clear previous activity
        String path = getClass().getClassLoader().getResource("sdb-test.ttl").getPath();
        store = StoreFactory.create(path);
        store.getTableFormatter().format();

        // create the dao object that we will test
        harvesterDao = new HarvesterDaoImpl(new SDBDatabase("/sdb-test.ttl"));

        // create a model for setting up tests
        model = getModel(store);

        // create a single source and store
        createTestSource(testUri, testTitle, testDescription, testBlocked, testLastStatus,
                testLastVisited);
    }

    @After
    public void tearDown() {

        model.close();
        store.close();
        System.out.println("TEARDOWN");
    }

    @Test
    public void testCreateHarvestSource() {

        // create a new source
        String uri = "http://example.org/file.rdf";
        String title = "Test Source";
        String description = "A Test Source";
        boolean blocked = false;

        HarvestSource harvestSource =
                harvesterDao.createHarvestSource(uri, title, description, blocked);

        // check that the bean was populated
        assertEquals("Unexpected Location", uri, harvestSource.getLocation());
        assertEquals("Unexpected title", title, harvestSource.getName());
        assertEquals("Unexpected description", description, harvestSource.getDescription());
        assertFalse("Should not be blocked", harvestSource.isBlocked());

        // check the resource was stored ...
        Resource resource = model.getResource(uri);

        assertEquals("Unexpected title from resource", title,
                resource.getProperty(DC.title).getLiteral().getLexicalForm());
        assertEquals("Unexpected description from resource", description,
                resource.getProperty(DC.description).getLiteral().getLexicalForm());
        assertFalse("Should not be blocked", resource.getProperty(Vocab.isBlocked).getLiteral()
                .getBoolean());
    }

    @Test
    public void testUpdateSourceThatDoesntExist() {
        try {
            harvesterDao.updateHarvestSource("http://example.org/grrr", null, null, null, null,
                    false);
        } catch (Exception ex) {
            assertNotNull("Expected an exception", ex);
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void testUpdateSource() {

        assertTrue("The source doesn't exist",
                model.containsResource(ResourceFactory.createResource(testUri)));

        Resource before = model.getResource(testUri);

        // check values before change
        assertEquals("Unexpected Location", testUri, before.getURI());
        assertEquals("Unexpected title", testTitle, before.getProperty(DC.title).getLiteral()
                .getLexicalForm());
        assertEquals("Unexpected description", testDescription, before.getProperty(DC.description)
                .getLiteral().getLexicalForm());
        assertFalse("Should not be blocked", before.getProperty(Vocab.isBlocked).getLiteral()
                .getBoolean());
        assertEquals("Unexpected status", testLastStatus, before.getProperty(Vocab.lastStatus)
                .getLiteral().getLexicalForm());
        assertEquals("Unexpected time", testLastVisited.getTime(),
                before.getProperty(Vocab.lastVisited).getLiteral().getLong());


        // make changes ...

        String newTitle = "New Title";
        String newDescription = "New Description";
        Date newDate = new Date();
        String newStatus = "blah";

        harvesterDao.updateHarvestSource(testUri, newTitle, newDescription, newDate,
                newStatus, true);

        Resource after = model.getResource(testUri);

        assertEquals("Unexpected title", newTitle, after.getProperty(DC.title).getLiteral()
                .getLexicalForm());
        assertEquals("Unexpected description", newDescription, after.getProperty(DC.description)
                .getLiteral().getLexicalForm());
        assertTrue("Should be blocked", after.getProperty(Vocab.isBlocked).getBoolean());
        assertEquals("Unexpected time", newDate.getTime(), after.getProperty(Vocab.lastVisited)
                .getLiteral().getLong());
        assertEquals("Unexpected status", newStatus, after.getProperty(Vocab.lastStatus)
                .getLiteral().getLexicalForm());
    }

    @Test
    public void testFindSource() {

        assertTrue("The source doesn't exist",
                model.containsResource(ResourceFactory.createResource(testUri)));

        HarvestSource source = harvesterDao.findSource(testUri);

        assertNotNull("The source should not be null", source);
        assertEquals("Unexpected title", testTitle, source.getName());
    }

    @Test
    public void testFindFakeSource() {

        HarvestSource source = harvesterDao.findSource("http://example.org/grrr");

        assertNull("The source should not exist", source);
    }


    @Test
    public void testFindSources() {

        // create some test sources and check they are in the model

        String location = "http://example.org/yadda.rdf";

        createTestSource(location, null, null, false, null, null);

        assertTrue("The source doesn't exist",
                model.containsResource(ResourceFactory.createResource(testUri)));

        assertTrue("The source doesn't exist",
                model.containsResource(ResourceFactory.createResource(location)));

        // test that the sources can be found by the dao

        List<HarvestSource> results = harvesterDao.findAllSources();

        assertEquals("There should be 2 results", 2, results.size());

    }

    @Test
    public void testFindPermittedSources() {

        // create some test sources and check they are in the model

        String location = "http://example.org/yadda.rdf";

        createTestSource(location, null, null, true, null, null);

        assertTrue("The source doesn't exist",
                model.containsResource(ResourceFactory.createResource(testUri)));

        assertTrue("The source doesn't exist",
                model.containsResource(ResourceFactory.createResource(location)));

        // test that the sources can be found by the dao

        List<HarvestSource> results = harvesterDao.findAllPermittedSources();

        assertEquals("There should be 1 results", 1, results.size());

    }

    @Test
    public void testDeleteSource() {

        assertTrue("The source doesn't exist",
                model.containsResource(ResourceFactory.createResource(testUri)));

        harvesterDao.deleteSource(testUri);

        assertNull("The source shouldn't exist", harvesterDao.findSource(testUri));
    }

    @Test
    public void testDeleteData() {

        String location = "http://example.org/test";

        FileManager fm = FileManager.get();
        Model m = fm.loadModel(this.getClass().getClassLoader()
                .getResource("rdf/event.rdf").getPath());

        Model model = SDBFactory.connectDataset(store).getNamedModel(location);
        model.add(m);
        assertTrue("The model is empty", model.size() > 0);
        model.close();

        harvesterDao.deleteData(location);

        model = SDBFactory.connectDataset(store).getNamedModel(location);

        assertEquals("The model should be empty", 0, model.size());


    }

    private void createTestSource(String location, String title, String description,
                                  boolean isBlocked, String lastStatus, Date lastVisited) {

        Model m = getModel(store);

        Resource resource = m.createResource(location);

        if (title != null) {
            resource.addLiteral(DC.title, title);
        }

        if (description != null) {
            resource.addLiteral(DC.description, description);
        }

        resource.addLiteral(Vocab.isBlocked, isBlocked);

        if (lastStatus != null) {
            resource.addLiteral(Vocab.lastStatus, lastStatus);
        }

        if (lastVisited != null) {
            resource.addLiteral(Vocab.lastVisited, lastVisited.getTime());
        }

        m.add(m.createStatement(resource, RDF.type, Vocab.Source));

        m.close();
    }

    private Model getModel(Store store) {
        Model m = SDBFactory.connectDataset(store).getDefaultModel();
        m.setNsPrefix("harvester", Vocab.NS);
        return m;
    }

    **/
    private HarvesterDao harvesterDao;
    private Model model;
    private Store store;

    private String testUri = "http://example.org/testable.rdf";
    private String testTitle = "Title";
    private String testDescription = "Description";
    private String testLastStatus = "OK";
    private Date testLastVisited = new Date();
    private boolean testBlocked = false;
}
