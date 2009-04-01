package net.crew_vre.events.service.impl;

import net.crew_vre.events.dao.PaperDao;
import net.crew_vre.events.dao.PersonDao;
import net.crew_vre.events.domain.Paper;
import net.crew_vre.events.service.PaperService;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class PaperServiceImpl implements PaperService {

    public PaperServiceImpl(PaperDao paperDao, PersonDao personDao) {
        this.paperDao = paperDao;
        this.personDao = personDao;
    }

    public Paper getPaperById(String id) {
        Paper paper = paperDao.findPaperById(id);

        if (paper != null) {
            paper.setAuthors(personDao.findAuthors(id));
        }
        return paper;
    }

    public List<Paper> findPapersRelatedToEvent(String eventId) {

        List<Paper> results = paperDao.findPapersRelatedToEvent(eventId);

        for (Paper p: results) {
            p.setAuthors(personDao.findAuthors(p.getId()));
        }
        return results;
    }

    // used to get the paper object
    private PaperDao paperDao;

    // used to get the authors of a paper
    private PersonDao personDao;

}
