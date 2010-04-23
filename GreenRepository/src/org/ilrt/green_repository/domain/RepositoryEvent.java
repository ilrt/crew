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
import java.security.*;


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
        this.startPoint1 = form.getStartPoint1();
        this.startPoint2 = form.getStartPoint2();
        this.startPoint3 = form.getStartPoint3();
        this.latitude = form.getLatitude();
        this.startPointLat1 = form.getStartPointLat1();
        this.startPointLat2 = form.getStartPointLat2();
        this.startPointLat3 = form.getStartPointLat3();
        this.longitude = form.getLongitude();
        this.startPointLong1 = form.getStartPointLong1();
        this.startPointLong2 = form.getStartPointLong2();
        this.startPointLong3 = form.getStartPointLong3();
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

    public String getLocationHash() {
        String locationHash = "";
        if (this.getLocation() != null) {
            locationHash = md5Hash(this.getLocation());
        }
        return locationHash;
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

    public String getStartPointHash1() {
        String startPointHash1 = "";
        if (this.getStartPoint1() != null) {
            startPointHash1 = md5Hash(this.getStartPoint1());
            startPointHash1 = "SPT_" + startPointHash1;
        }
        return startPointHash1;
    }

    public String getStartPoint2() {
        return startPoint2;
    }

    public void setStartPoint2(String startPoint2) {
        this.startPoint2 = startPoint2;
    }

    public String getStartPointHash2() {
        String startPointHash2 = "";
        if (this.getStartPoint2() != null) {
            startPointHash2 = md5Hash(this.getStartPoint2());
            startPointHash2 = "SPT_" + startPointHash2;        }
        return startPointHash2;
    }

    public String getStartPoint3() {
        return startPoint3;
    }

    public void setStartPoint3(String startPoint3) {
        this.startPoint3 = startPoint3;
    }

    public String getStartPointHash3() {
        String startPointHash3 = "";
        if (this.getStartPoint3() != null) {
            startPointHash3 = md5Hash(this.getStartPoint3());
            startPointHash3 = "SPT_" + startPointHash3;
        }
        return startPointHash3;
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

    private String md5Hash(String input) {
        String digestString = "";
        if (input != null) {
            byte[] bytes = input.getBytes();
            try {
                MessageDigest algorithm = MessageDigest.getInstance("md5");
                algorithm.reset();
                algorithm.update(bytes);
                byte[] messageDigest = algorithm.digest();
                StringBuffer digestBuffer = new StringBuffer();
                for (int i=0;i < messageDigest.length; i++) {
                    digestBuffer.append(Integer.toHexString((messageDigest[i] >>> 4) & 0x0F));
                    digestBuffer.append(Integer.toHexString(0x0F & messageDigest[i]));
                }
                digestString = digestBuffer.toString();
            }
            catch (NoSuchAlgorithmException nsae) {
                return digestString;
            }
        }
        return digestString;
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

    @Column(name = "DESCRIPTION", columnDefinition="LONG VARCHAR", nullable = true)
    private String description;

    @Column(name = "LOCATION", nullable = true)
    private String location;

    @Column(name = "STARTPOINT1", nullable = true)
    private String startPoint1;

    @Column(name = "STARTPOINT2", nullable = true)
    private String startPoint2;

    @Column(name = "STARTPOINT3", nullable = true)
    private String startPoint3;

    @Column(name = "LATITUDE", nullable = true)
    private String latitude;

    @Column(name = "STARTPOINTLAT1", nullable = true)
    private String startPointLat1;

    @Column(name = "STARTPOINTLAT2", nullable = true)
    private String startPointLat2;

    @Column(name = "STARTPOINTLAT3", nullable = true)
    private String startPointLat3;

    @Column(name = "LONGITUDE", nullable = true)
    private String longitude;

    @Column(name = "STARTPOINTLONG1", nullable = true)
    private String startPointLong1;

    @Column(name = "STARTPOINTLONG2", nullable = true)
    private String startPointLong2;

    @Column(name = "STARTPOINTLONG3", nullable = true)
    private String startPointLong3;

    @Column(name = "EVENTURL", nullable = true)
    private String eventUrl;


}
