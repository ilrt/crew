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

package org.ilrt.green_repository.web;

import org.ilrt.green_repository.domain.RepositoryEvent;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class RepositoryEventForm {

    private String eventId = null;
    private String title = null;
    private String startDate = null;
    private String endDate = null;
    private String description = null;
    private String location = null;
    private String startPoint1 = null;
    private String startPoint2 = null;
    private String startPoint3 = null;
    private String latitude = null;
    private String startPointLat1 = null;
    private String startPointLat2 = null;
    private String startPointLat3 = null;
    private String longitude = null;
    private String startPointLong1 = null;
    private String startPointLong2 = null;
    private String startPointLong3 = null;
    private String eventUrl = null;
    private String addButton = null;
    private String updateButton = null;
    private String cancelButton = null;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public RepositoryEventForm() {}

    public RepositoryEventForm(RepositoryEvent event) {
        this.eventId = event.getEventId();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.startPoint1 = event.getStartPoint1();
        this.startPoint2 = event.getStartPoint2();
        this.startPoint3 = event.getStartPoint3();
        this.latitude = event.getLatitude();
        this.startPointLat1 = event.getStartPointLat1();
        this.startPointLat2 = event.getStartPointLat2();
        this.startPointLat3 = event.getStartPointLat3();
        this.longitude = event.getLongitude();
        this.startPointLong1 = event.getStartPointLong1();
        this.startPointLong2 = event.getStartPointLong2();
        this.startPointLong3 = event.getStartPointLong3();
        this.eventUrl = event.getEventUrl();
    }

    // Will only exist for existing events
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    // Return startDate as Date obj
    public Date getStartDateObj() {
        Date startDateObj = null;
        if (startDate != null) {
            try {
                startDateObj = dateFormat.parse(startDate);
            } catch(ParseException pe) {
                startDateObj = null;
            }
        }
        return startDateObj;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    // Return endDate as Date obj
    public Date getEndDateObj() {
        Date endDateObj = null;
        if (endDate != null) {
            try {
                endDateObj = dateFormat.parse(endDate);
            } catch(ParseException pe) {
                endDateObj = null;
            }
        }
        return endDateObj;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public String getStartPoint1() {
        return startPoint1;
    }

    public void setStartPoint1(String startPoint1) {
        this.startPoint1 = startPoint1;
    }

    public String getStartPoint2() {
        return startPoint2;
    }

    public void setStartPoint2(String startPoint2) {
        this.startPoint2 = startPoint2;
    }

    public String getStartPoint3() {
        return startPoint3;
    }

    public void setStartPoint3(String startPoint3) {
        this.startPoint3 = startPoint3;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getStartPointLat1() {
        return startPointLat1;
    }

    public void setStartPointLat1(String startPointLat1) {
        this.startPointLat1 = startPointLat1;
    }

    public String getStartPointLat2() {
        return startPointLat2;
    }

    public void setStartPointLat2(String startPointLat2) {
        this.startPointLat2 = startPointLat2;
    }

    public String getStartPointLat3() {
        return startPointLat3;
    }

    public void setStartPointLat3(String startPointLat3) {
        this.startPointLat3 = startPointLat3;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStartPointLong1() {
        return startPointLong1;
    }

    public void setStartPointLong1(String startPointLong1) {
        this.startPointLong1 = startPointLong1;
    }

    public String getStartPointLong2() {
        return startPointLong2;
    }

    public void setStartPointLong2(String startPointLong2) {
        this.startPointLong2 = startPointLong2;
    }

    public String getStartPointLong3() {
        return startPointLong3;
    }

    public void setStartPointLong3(String startPointLong3) {
        this.startPointLong3 = startPointLong3;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getAddButton() {
        return addButton;
    }

    public void setAddButton(String addButton) {
        this.addButton = addButton;
    }

    public String getUpdateButton() {
        return updateButton;
    }

    public void setUpdateButton(String updateButton) {
        this.updateButton = updateButton;
    }

    public String getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(String cancelButton) {
        this.cancelButton = cancelButton;
    }
}
