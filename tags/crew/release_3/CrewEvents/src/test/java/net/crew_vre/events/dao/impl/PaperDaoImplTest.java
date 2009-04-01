package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.PaperDao;
import net.crew_vre.events.domain.Paper;
import net.crew_vre.jena.exception.DatasetFactoryException;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PaperDaoImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class PaperDaoImplTest extends TestCase {

    private PaperDao paperDao;

    @Override
    public void setUp() {
        try {
        DatasetFactory datasetFactory = new DatasetFactoryClasspathFileImpl(Constants.DEFAULT_MODEL,
                Constants.NAMED_GRAPHS);
        JenaQueryUtility jqUtility = new JenaQueryUtilityImpl();
        paperDao = new PaperDaoImpl(datasetFactory, jqUtility);
        } catch (DatasetFactoryException ex) {
            ex.printStackTrace();
        }
    }

    public void testPaperById() {
        Paper paper = paperDao.findPaperById(Constants.PAPER_ONE_ID);
        assertNotNull("The paper should not be null", paper);
        assertNotNull("The graph should not be null", paper.getGraph());
        assertNotNull("The title should not be null", paper.getTitle());
        assertNotNull("The description should not be null", paper.getTitle());
        assertTrue("The paper is retrievable", paper.isRetrievable());
    }

    public void testFindPapersRelatedToEvent() {
        List<Paper> results = paperDao.findPapersRelatedToEvent(Constants.EVENT_TWO_ID);
        assertNotNull("The results should not be null", results);
        assertEquals("There should be two paper", 2, results.size());

        for (Paper p : results) {
            assertNotNull("The graph should not be null", p.getGraph());
        }
    }

}
