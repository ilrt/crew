package net.crew_vre.events.service.impl;

import net.crew_vre.events.dao.RecordingDao;
import net.crew_vre.events.domain.Recording;
import net.crew_vre.events.service.RecordingService;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RecordingServiceImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class RecordingServiceImpl implements RecordingService {

    public RecordingServiceImpl(final RecordingDao recordingDao) {
        this.recordingDao = recordingDao;
    }

    public Recording getRecordingById(final String recordingId) {

        return recordingDao.findRecordingById(recordingId);
    }

    /**
     * DAO to find recordings.
     */
    private RecordingDao recordingDao;
}
