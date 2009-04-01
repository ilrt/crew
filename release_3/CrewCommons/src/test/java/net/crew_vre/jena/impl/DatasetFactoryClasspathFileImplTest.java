package net.crew_vre.jena.impl;

import com.hp.hpl.jena.query.Dataset;
import junit.framework.TestCase;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;
import net.crew_vre.jena.exception.DatasetFactoryException;

import java.util.Iterator;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DatasetFactoryClasspathFileImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DatasetFactoryClasspathFileImplTest extends TestCase {


    public void setUp() {

        try {
            datasetFactory = new DatasetFactoryClasspathFileImpl(defaultModelFile, namedGraphsDir);
        } catch (DatasetFactoryException e) {
            e.printStackTrace();
        }
    }

    public void testNamedGraphs() {

        Dataset dataset = datasetFactory.create();

        assertNotNull("The dataset should not be null", dataset);

        int count = 0;

        for (Iterator i = dataset.listNames(); i.hasNext(); i.next()) {
            count++;
        }
        assertEquals("There should be 5 named graphs", GRAPH_TOTAL, count);
    }

    private DatasetFactory datasetFactory;
    private String defaultModelFile = "/graphs/default.rdf";
    private String namedGraphsDir = "/graphs/named/";
    private static final int GRAPH_TOTAL = 5;
}
