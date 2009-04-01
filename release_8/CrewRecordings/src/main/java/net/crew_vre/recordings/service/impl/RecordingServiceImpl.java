package net.crew_vre.recordings.service.impl;

import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.service.RecordingService;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RecordingServiceImpl.java 929 2008-11-18 15:28:41Z cmmaj $
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
