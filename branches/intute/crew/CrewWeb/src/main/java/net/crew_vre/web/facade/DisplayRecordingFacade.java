package net.crew_vre.web.facade;

import net.crew_vre.recordings.domain.Recording;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayRecordingFacade.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public interface DisplayRecordingFacade {

    Recording displayRecording(String recordingId);

}
