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
package net.crew_vre.events.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>Represents an Event. This might be the "main" event such as a conference
 * or workshop, but can also be a lecture, tutorial, tea break etc.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Event.java 955 2008-12-04 17:25:47Z arowley $
 */
public class EventPart extends EventParent implements Comparable<EventPart> {

    /**
     * The description of the event
     */
    private String description = null;

    /**
     * Start date and time of the event
     */
    private DateTime startDateTime = null;

    /**
     * End date and time of the event
     */
    private DateTime endDateTime = null;

    /**
     * Start date
     */
    private LocalDate startDate = null;

    /**
     * End date
     */
    private LocalDate endDate = null;

    /**
     * Location - from a location taxonomy
     */
    private List<Location> locations = new ArrayList<Location>();

    public EventPart() { }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public DateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(final DateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public DateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(final DateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDate getStartDate() {
        if (startDate == null) {
            if (startDateTime != null) {
                return startDateTime.toLocalDate();
            }
        }
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        if (endDate == null) {
            if (endDateTime != null) {
                return endDateTime.toLocalDate();
            }
        }
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(final List<Location> locations) {
        this.locations = locations;
    }

    /**
     * @return true or false whether or not the event occurs on a single day
     */
    public boolean isSingleDay() {

        Period period = new Period(startDate, endDate);

        /**
         * I need to relook at this - zero also indicates "not supported"
         * in the Joda Time API for Period.
         */
        return period.getDays() == 0;

    }

    /**
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(EventPart part) {
        if (startDateTime != null) {
            if (part.startDateTime != null) {
                return startDateTime.compareTo(part.startDateTime);
            } else if (part.startDate != null) {
                return startDateTime.compareTo(part.startDate);
            }
            return -1;
        }
        if (part.startDateTime != null) {
            if (startDate != null) {
                return startDate.compareTo(part.startDateTime);
            }
            return 1;
        }
        return 0;
    }

}
