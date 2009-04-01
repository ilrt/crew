package net.crew_vre.events.service;

import net.crew_vre.events.domain.Paper;

import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PaperService.java 524 2007-12-19 20:13:22Z cmmaj $
 */
public interface PaperService {

    Paper getPaperById(String id);

    List<Paper> findPapersRelatedToEvent(String eventId);

}
