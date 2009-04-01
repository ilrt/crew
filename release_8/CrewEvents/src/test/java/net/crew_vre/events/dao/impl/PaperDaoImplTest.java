package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.PaperDao;
import net.crew_vre.events.domain.Paper;

import java.io.IOException;
import java.util.List;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PaperDaoImplTest.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class PaperDaoImplTest extends TestCase {

    private PaperDao paperDao;

    @Override
    public void setUp() {
        try {
            Database database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);

            paperDao = new PaperDaoImpl(database);
        } catch (IOException ex) {
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
