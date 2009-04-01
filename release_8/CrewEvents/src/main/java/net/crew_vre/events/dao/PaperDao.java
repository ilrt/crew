package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Paper;

import java.util.List;

/**
 * <p>An event might have references to papers and documents - these might be a PDF with regard to
 * a given presentation or references to further reading. This DAO provides a means to access
 * this data.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: PaperDao.java 776 2008-06-26 14:55:36Z cmmaj $
 */
public interface PaperDao {

    Paper findPaperById(String id);

    List<Paper> findPapersRelatedToEvent(String eventId);

}
