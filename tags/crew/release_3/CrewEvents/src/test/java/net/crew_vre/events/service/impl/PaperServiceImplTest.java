package net.crew_vre.events.service.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.dao.PaperDao;
import net.crew_vre.events.dao.PersonDao;
import net.crew_vre.events.dao.impl.*;
import net.crew_vre.events.domain.Paper;
import net.crew_vre.events.service.PaperService;
import net.crew_vre.jena.exception.DatasetFactoryException;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.query.impl.DatasetFactoryClasspathFileImpl;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PaperServiceImplTest.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class PaperServiceImplTest extends TestCase {

    private PaperService paperService;

    @Override
    public void setUp() {
        try {
            DatasetFactory datasetFactory =
                    new DatasetFactoryClasspathFileImpl(Constants.DEFAULT_MODEL,
                            Constants.NAMED_GRAPHS);
            JenaQueryUtility jqUtility = new JenaQueryUtilityImpl();
            EventDao eventDao = new EventDaoImpl(datasetFactory, jqUtility);
            PaperDao paperDao = new PaperDaoImpl(datasetFactory, jqUtility);
            PersonDao personDao = new PersonDaoImpl(datasetFactory, jqUtility, eventDao);
            paperService = new PaperServiceImpl(paperDao, personDao);
        } catch (DatasetFactoryException e) {
            e.printStackTrace();
        }


    }

    public void testGetPaperById() {
        Paper paper = paperService.getPaperById(Constants.PAPER_ONE_ID);
        assertNotNull("The paper should not be null", paper);
        assertEquals("The paper should have two authors", 2, paper.getAuthors().size());
    }

    public void testGetPapersRelatedToEvent() {
        List<Paper> results = paperService.findPapersRelatedToEvent(Constants.EVENT_TWO_ID);
        assertNotNull("The results should not be null", results);
        assertEquals("The results should have two paper", 2, results.size());

    }
}
