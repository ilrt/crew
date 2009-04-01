package net.crew_vre.events.service;

import net.crew_vre.events.domain.Recording;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RecordingService.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface RecordingService {

    Recording getRecordingById(final String recordingId);

}
