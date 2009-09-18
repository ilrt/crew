package net.crew_vre.web.facade.impl;

import java.util.List;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.EventService;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.service.RecordingService;
import net.crew_vre.web.facade.DisplayEventFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayEventFacadeImpl.java 1171 2009-03-26 17:36:37Z arowley $
 */
public class DisplayEventFacadeImpl implements DisplayEventFacade {

    public DisplayEventFacadeImpl(EventService eventService,
            RecordingService recordingService) {
        this.eventService = eventService;
        this.recordingService = recordingService;
    }

    public Event displayEvent(String eventId) {

        return eventService.getEventById(eventId);
    }

    public List<Recording> displayRecordings(Event event) {
        return recordingService.getRecordingsOfEvent(event.getId());
    }

    private EventService eventService;

    private RecordingService recordingService;
}
