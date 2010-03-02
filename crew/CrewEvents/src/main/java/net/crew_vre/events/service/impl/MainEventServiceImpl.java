/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.crew_vre.events.service.impl;

import net.crew_vre.events.dao.MainEventDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.events.service.MainEventService;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: MainEventServiceImpl.java 1188 2009-03-31 13:09:20Z cmmaj $
 */
public class MainEventServiceImpl implements MainEventService {

    public MainEventServiceImpl(final MainEventDao mainEventDao) {
        this.mainEventDao = mainEventDao;
    }

    public EventPart getEventById(final String eventId) {

        return mainEventDao.findEventById(eventId);
    }

    public List<EventPart> getAllEvents() {
        return mainEventDao.findAllEvents();
    }

    public List<EventPart> getAllEvents(final int limit, final int offset) {
        return mainEventDao.findAllEvents(limit, offset);
    }

    public List<EventPart> getEventsByDate(final LocalDate startDate, final LocalDate endDate) {
        return mainEventDao.findEventsByDate(startDate, endDate);
    }

    public List<EventPart> getEventsByDate(final LocalDate startDate, final LocalDate endDate,
                                       final int limit, final int offset) {
        return mainEventDao.findEventsByDate(startDate, endDate, limit, offset);
    }

    public List<EventPart> getEventsByDateAsc(final LocalDate startDate, final LocalDate endDate) {
        return mainEventDao.findEventsByDateAsc(startDate, endDate);
    }

    public List<EventPart> getEventsByDateAsc(final LocalDate startDate, final LocalDate endDate,
                                       final int limit, final int offset) {
        return mainEventDao.findEventsByDateAsc(startDate, endDate, limit, offset);
    }

    public List<EventPart> getEventsByCreationDate(final DateTime startDate, final DateTime endDate) {
        return mainEventDao.findEventsByCreationDate(startDate, endDate);
    }

    public List<EventPart> getEventsByCreationDate(DateTime startDate, DateTime endDate, int limit,
                                               int offset) {
        return mainEventDao.findEventsByCreationDate(startDate, endDate, limit, offset);
    }

    public int getTotalEvents() {
        return mainEventDao.findAllEvents().size();
    }

    public int getTotalEventsByDate(final LocalDate startDate, final LocalDate endDate) {
        return mainEventDao.findEventsByDate(startDate, endDate).size();
    }

    public List<EventPart> getEventWithConstraint(String constraint) {
        return mainEventDao.findEventsWithConstraint(constraint);
    }

    public List<EventPart> getEventWithConstraint(String constraint, int limit, int offset) {
        return mainEventDao.findEventsWithConstraint(constraint, limit, offset);
    }

    public int getTotalEventWithConstraint(String constraint) {
        return mainEventDao.findEventsWithConstraint(constraint).size();
    }

    /**
     * DAO for "main event" details
     */
    private MainEventDao mainEventDao;
}
