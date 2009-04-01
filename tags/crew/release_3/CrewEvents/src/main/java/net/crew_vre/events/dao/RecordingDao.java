package net.crew_vre.events.dao;

import net.crew_vre.events.domain.Recording;

/**
 * <p>A Data Access Object that provides access to recordings.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RecordingDao.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface RecordingDao {

    Recording findRecordingById(final String id);

}
