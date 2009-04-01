package net.crew_vre.events.service;

import net.crew_vre.events.domain.Paper;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PaperService.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface PaperService {

    Paper getPaperById(String id);

    List<Paper> findPapersRelatedToEvent(String eventId);

}
