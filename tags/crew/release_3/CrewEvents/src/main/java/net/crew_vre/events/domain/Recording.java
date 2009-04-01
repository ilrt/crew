package net.crew_vre.events.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Represents a recording of an event.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class Recording {

    /**
     * Milliseconds in a seconds
     */
    private static final int MSECONDS = 1000;

    /**
     * Unique indentifier (URI) for the recording.
     */
    private String id;

    /**
     * Unique indentifier (URI) for the associated event.
     */
    private String eventId;

    /**
     * The URL of the video stream.
     */
    private String videoUrl;

    /**
     * The URL of the screen stream.
     */
    private String screenUrl;

    /**
     * The start time of the recording.
     */
    private long startTime;

    /**
     * The end time of the recording.
     */
    private long endTime;

    /**
     * List of Screen objects - jpeg images.
     */
    private List<Screen> screens = new ArrayList<Screen>();

    public Recording() { }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(final String eventId) {
        this.eventId = eventId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(final String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getScreenUrl() {
        return screenUrl;
    }

    public void setScreenUrl(final String screenUrl) {
        this.screenUrl = screenUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public List<Screen> getScreens() {
        return screens;
    }

    public void setScreens(final List<Screen> screens) {
        this.screens = screens;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }

    public String getThumbNailStartTimes() {

        StringBuffer out = new StringBuffer();

        Iterator i = screens.iterator();

        while (i.hasNext()) {
            Screen screen = (Screen) i.next();
            out.append((screen.getStartTime() - getStartTime()) / MSECONDS);
            if (i.hasNext()) {
                out.append(",");
            }
        }

        return out.toString();
    }

    public String getThumbNailEndTimes() {

        StringBuffer out = new StringBuffer();

        Iterator i = screens.iterator();

        while (i.hasNext()) {
            Screen screen = (Screen) i.next();
            out.append((screen.getEndTime() - getStartTime()) / MSECONDS);
            if (i.hasNext()) {
                out.append(",");
            }
        }

        return out.toString();
    }

}
