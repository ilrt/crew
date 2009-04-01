package net.crew_vre.events.service.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.dao.PaperDao;
import net.crew_vre.events.dao.PersonDao;
import net.crew_vre.events.dao.impl.*;
import net.crew_vre.events.domain.Paper;
import net.crew_vre.events.service.PaperService;

import java.io.IOException;
import java.util.List;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PaperServiceImplTest.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class PaperServiceImplTest extends TestCase {

    private PaperService paperService;

    @Override
    public void setUp() {
        try {
            Database database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);
            EventDao eventDao = new EventDaoImpl(database);
            PaperDao paperDao = new PaperDaoImpl(database);
            PersonDao personDao = new PersonDaoImpl(database, eventDao);
            paperService = new PaperServiceImpl(paperDao, personDao);
        } catch (IOException e) {
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
