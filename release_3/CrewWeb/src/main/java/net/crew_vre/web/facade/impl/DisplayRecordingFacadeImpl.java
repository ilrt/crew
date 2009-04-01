package net.crew_vre.web.facade.impl;

import net.crew_vre.events.domain.Recording;
import net.crew_vre.events.service.RecordingService;
import net.crew_vre.web.facade.DisplayRecordingFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayRecordingFacadeImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
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
