/**
 * Copyright (c) 2010, University of Bristol
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


package org.ilrt.green_repository.domain;

import org.ilrt.green_repository.web.RepositoryEventForm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
@Entity
@Table(name = "REPOSITORY_EVENT")
public class RepositoryEvent implements Serializable {

    public RepositoryEvent() {
    }

    public RepositoryEvent(RepositoryEventForm form) {
        this.eventId = form.getEventId();
        this.title = form.getTitle();
        this.startDate = form.getStartDate();
        this.endDate = form.getEndDate();
        this.description = form.getDescription();
        this.location = form.getLocation();
        this.eventUrl = form.getEventUrl();
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepositoryEvent)) return false;

        RepositoryEvent event = (RepositoryEvent) o;
        return eventId.equals(event.getEventId());
    }

    @Override
    public int hashCode() {
        int result;
        result = eventId.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }

    @Id
    @Column(name = "EVENTID")
    private String eventId;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "STARTDATE", nullable = true)
    private String startDate;

    @Column(name = "ENDDATE", nullable = true)
    private String endDate;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "LOCATION", nullable = true)
    private String location;

    @Column(name = "EVENTURL", nullable = true)
    private String eventUrl;


}
