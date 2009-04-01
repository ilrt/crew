package net.crew_vre.recordings.service;

import net.crew_vre.recordings.domain.Recording;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RecordingService.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public interface RecordingService {

    Recording getRecordingById(final String recordingId);

}
