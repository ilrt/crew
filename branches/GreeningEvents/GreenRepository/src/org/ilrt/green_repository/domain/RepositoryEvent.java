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

import javax.persistence.OneToMany;
import org.ilrt.green_repository.web.RepositoryEventForm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.security.*;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;


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
        this.latitude = form.getLatitude();
        this.longitude = form.getLongitude();
        this.locationDescription = form.getLocationDescription();
        this.locationUrl = form.getLocationUrl();
        this.locationThumbUrl = form.getLocationThumbUrl();
        this.locationImagesUrl = form.getLocationImagesUrl();

        RepositoryEventStartPoint startPoint = null;
        if (form.getStartPoint1() != null && !form.getStartPoint1().equals("")
                && form.getStartPointLat1() != null && !form.getStartPointLat1().equals("")
                && form.getStartPointLong1() != null && !form.getStartPointLong1().equals("")){
            if (form.getStartPointId1() != null && !form.getStartPointId1().equals("")) {
                // Existing startPoint
                startPoint = new RepositoryEventStartPoint(
                        form.getStartPointId1(),form.getStartPoint1(),form.getStartPointLat1(),form.getStartPointLong1()
                        );
            } else {
                // New startPoint
                startPoint = new RepositoryEventStartPoint(
                        form.getStartPoint1(),form.getStartPointLat1(),form.getStartPointLong1()
                        );
            }
            if (form.getWaypointLat1_1() != null && !form.getWaypointLat1_1().equals("")
                && form.getWaypointLong1_1() != null && !form.getWaypointLong1_1().equals("")) {
                if (form.getWaypointId1_1() != null && !form.getWaypointId1_1().equals("")) {
                    // Existing waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointId1_1(),form.getWaypointLat1_1(),form.getWaypointLong1_1()
                            ));
                } else {
                    // New waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointLat1_1(),form.getWaypointLong1_1()
                            ));
                }
            }
            if (form.getWaypointLat1_2() != null && !form.getWaypointLat1_2().equals("")
                && form.getWaypointLong1_2() != null && !form.getWaypointLong1_2().equals("")) {
                if (form.getWaypointId1_2() != null && !form.getWaypointId1_2().equals("")) {
                    // Existing waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointId1_2(),form.getWaypointLat1_2(),form.getWaypointLong1_2()
                            ));
                } else {
                    // New waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointLat1_2(),form.getWaypointLong1_2()
                            ));
                }
            }
            startPoints.add(startPoint);
        }
        if (form.getStartPoint2() != null && !form.getStartPoint2().equals("")
                && form.getStartPointLat2() != null && !form.getStartPointLat2().equals("")
                && form.getStartPointLong2() != null && !form.getStartPointLong2().equals("")){
            if (form.getStartPointId2() != null && !form.getStartPointId2().equals("")) {
                // Existing startPoint
                startPoint = new RepositoryEventStartPoint(
                        form.getStartPointId2(),form.getStartPoint2(),form.getStartPointLat2(),form.getStartPointLong2()
                        );
            } else {
                // New startPoint
                startPoint = new RepositoryEventStartPoint(
                        form.getStartPoint2(),form.getStartPointLat2(),form.getStartPointLong2()
                        );
            }
            if (form.getWaypointLat2_1() != null && !form.getWaypointLat2_1().equals("")
                && form.getWaypointLong2_1() != null && !form.getWaypointLong2_1().equals("")) {
                if (form.getWaypointId2_1() != null && !form.getWaypointId2_1().equals("")) {
                    // Existing waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointId2_1(),form.getWaypointLat2_1(),form.getWaypointLong2_1()
                            ));
                } else {
                    // New waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointLat2_1(),form.getWaypointLong2_1()
                            ));
                }
            }
            if (form.getWaypointLat2_2() != null && !form.getWaypointLat2_2().equals("")
                && form.getWaypointLong2_2() != null && !form.getWaypointLong2_2().equals("")) {
                if (form.getWaypointId2_2() != null && !form.getWaypointId2_2().equals("")) {
                    // Existing waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointId2_2(),form.getWaypointLat2_2(),form.getWaypointLong2_2()
                            ));
                } else {
                    // New waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointLat2_2(),form.getWaypointLong2_2()
                            ));
                }
            }
            startPoints.add(startPoint);
        }
        if (form.getStartPoint3() != null && !form.getStartPoint3().equals("")
                && form.getStartPointLat3() != null && !form.getStartPointLat3().equals("")
                && form.getStartPointLong3() != null && !form.getStartPointLong3().equals("")){
            if (form.getStartPointId3() != null && !form.getStartPointId3().equals("")) {
                // Existing startPoint
                startPoint = new RepositoryEventStartPoint(
                        form.getStartPointId3(),form.getStartPoint3(),form.getStartPointLat3(),form.getStartPointLong3()
                        );
            } else {
                // New startPoint
                startPoint = new RepositoryEventStartPoint(
                        form.getStartPoint3(),form.getStartPointLat3(),form.getStartPointLong3()
                        );
            }
            if (form.getWaypointLat3_1() != null && !form.getWaypointLat3_1().equals("")
                && form.getWaypointLong3_1() != null && !form.getWaypointLong3_1().equals("")) {
                if (form.getWaypointId3_1() != null && !form.getWaypointId3_1().equals("")) {
                    // Existing waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointId3_1(),form.getWaypointLat3_1(),form.getWaypointLong3_1()
                            ));
                } else {
                    // New waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointLat3_1(),form.getWaypointLong3_1()
                            ));
                }
            }
            if (form.getWaypointLat3_2() != null && !form.getWaypointLat3_2().equals("")
                && form.getWaypointLong3_2() != null && !form.getWaypointLong3_2().equals("")) {
                if (form.getWaypointId3_2() != null && !form.getWaypointId3_2().equals("")) {
                    // Existing waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointId3_2(),form.getWaypointLat3_2(),form.getWaypointLong3_2()
                            ));
                } else {
                    // New waypoint
                    startPoint.setWaypoint(new RepositoryEventWaypoint(
                            form.getWaypointLat3_2(),form.getWaypointLong3_2()
                            ));
                }
            }
            startPoints.add(startPoint);
        }
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

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }
    
    // Location data
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationThumbUrl(String locationThumbUrl) {
        this.locationThumbUrl = locationThumbUrl;
    }

    public String getLocationThumbUrl() {
        return locationThumbUrl;
    }

    public void setLocationImagesUrl(String locationImagesUrl) {
        this.locationImagesUrl = locationImagesUrl;
    }

    public String getLocationImagesUrl() {
        return locationImagesUrl;
    }

    // Startpoint data for Google map directions
    public Set<RepositoryEventStartPoint> getStartPoints(){
        return startPoints;
    }
    
    public void setStartPoints(Set<RepositoryEventStartPoint> startPoints){
        this.startPoints = startPoints;
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
        result = 31 * result + latitude.hashCode();
        result = 31 * result + longitude.hashCode();
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

    // Optional collection of route startPoints connecting to the location associated with the event
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private Set<RepositoryEventStartPoint> startPoints = new HashSet<RepositoryEventStartPoint>();

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

    @Column(name = "EVENTURL", nullable = true)
    private String eventUrl;

    // Location details
    // Location name
    @Column(name = "LOCATION", nullable = true)
    private String location;

    @Column(name = "LATITUDE", nullable = true)
    private String latitude;

    @Column(name = "LONGITUDE", nullable = true)
    private String longitude;

    // Information below goes into Google map InfoWindow
    @Column(name = "LOCATIONDESCRIPTION", columnDefinition="LONG VARCHAR", nullable = true)
    private String locationDescription;

    @Column(name = "LOCATIONURL", nullable = true)
    private String locationUrl;

    @Column(name = "LOCATIONTHUMBURL", nullable = true)
    private String locationThumbUrl;

    @Column(name = "LOCATIONIMAGESURL", nullable = true)
    private String locationImagesUrl;

}
