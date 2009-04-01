package net.crew_vre.events.domain;

/**
 * <p>Represents a screen, i.e. a presentation slide - used to select a point in
 * the recording.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: Screen.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class Screen {

    /**
     * Unique indentifier (URI) for the screen.
     */
    private String id;

    /**
     * The start time of the screen.
     */
    private long startTime;

    /**
     * The end time of the screen.
     */
    private long endTime;

    /**
     * The name of the jpeg image that represents the screen.
     */
    private String imageName;

    public Screen() { }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(final String imageName) {
        this.imageName = imageName;
    }

}
