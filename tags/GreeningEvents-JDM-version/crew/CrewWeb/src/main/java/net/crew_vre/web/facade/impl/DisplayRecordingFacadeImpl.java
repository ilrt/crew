package net.crew_vre.web.facade.impl;

import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.service.RecordingService;
import net.crew_vre.web.facade.DisplayRecordingFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayRecordingFacadeImpl.java 929 2008-11-18 15:28:41Z cmmaj $
 */
public class DisplayRecordingFacadeImpl implements DisplayRecordingFacade {

    public DisplayRecordingFacadeImpl(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    public Recording displayRecording(String recordingId) {

        return recordingService.getRecordingById(recordingId);
    }

    private RecordingService recordingService;

}
