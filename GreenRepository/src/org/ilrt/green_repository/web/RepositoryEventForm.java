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
import java.util.Iterator;
import java.util.Set;
import org.ilrt.green_repository.domain.RepositoryEventKml;
import org.ilrt.green_repository.domain.RepositoryEventStartPoint;
import org.ilrt.green_repository.domain.RepositoryEventWaypoint;

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
    private String latitude = null;
    private String longitude = null;
    private String locationDescription = null;
    private String locationUrl = null;
    private String locationThumbUrl = null;
    private String locationImagesUrl = null;

    private String startPoint1 = null;
    private String startPointId1 = null;
    private String startPointLat1 = null;
    private String startPointLong1 = null;

    private String startPoint2 = null;
    private String startPointId2 = null;
    private String startPointLat2 = null;
    private String startPointLong2 = null;

    private String startPoint3 = null;
    private String startPointId3 = null;
    private String startPointLat3 = null;
    private String startPointLong3 = null;

    private String waypointId1_1 = null;
    private String waypointLat1_1 = null;
    private String waypointLong1_1 = null;
    private String waypointId1_2 = null;
    private String waypointLat1_2 = null;
    private String waypointLong1_2 = null;

    private String waypointId2_1 = null;
    private String waypointLat2_1 = null;
    private String waypointLong2_1 = null;
    private String waypointId2_2 = null;
    private String waypointLat2_2 = null;
    private String waypointLong2_2 = null;

    private String waypointId3_1 = null;
    private String waypointLat3_1 = null;
    private String waypointLong3_1 = null;
    private String waypointId3_2 = null;
    private String waypointLat3_2 = null;
    private String waypointLong3_2 = null;

    private String kmlId1 = null;
    private String kmlTitle1 = null;
    private String kmlType1 = null;
    private String kmlStartLat1 = null;
    private String kmlStartLong1 = null;
    private String kmlXml1 = null;

    private String kmlId2 = null;
    private String kmlTitle2 = null;
    private String kmlType2 = null;
    private String kmlStartLat2 = null;
    private String kmlStartLong2 = null;
    private String kmlXml2 = null;

    private String kmlId3 = null;
    private String kmlTitle3 = null;
    private String kmlType3 = null;
    private String kmlStartLat3 = null;
    private String kmlStartLong3 = null;
    private String kmlXml3 = null;

    private String eventUrl = null;
    private String addButton = null;
    private String updateButton = null;
    private String cancelButton = null;
    private Set<RepositoryEventStartPoint> startPoints = null;
    private Set<RepositoryEventKml> kmlObjects = null;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public RepositoryEventForm() {}

    public RepositoryEventForm(RepositoryEvent event) {
        this.eventId = event.getEventId();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.description = event.getDescription();
        this.eventUrl = event.getEventUrl();
        this.location = event.getLocation();
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        this.locationDescription = event.getLocationDescription();
        this.locationUrl = event.getLocationUrl();
        this.locationThumbUrl = event.getLocationThumbUrl();
        this.locationImagesUrl = event.getLocationImagesUrl();
        startPoints = (Set)event.getStartPoints();
        if (startPoints != null) {
            Iterator iter = startPoints.iterator();
            RepositoryEventStartPoint startPoint;
            int count = 0;
            while (iter.hasNext()) {
                count++;
                startPoint = (RepositoryEventStartPoint)iter.next();
                if (count == 1) {
                    this.setStartPoint1(startPoint.getTitle());
                    this.setStartPointId1(startPoint.getStartPointId());
                    this.setStartPointLat1(startPoint.getLatitude());
                    this.setStartPointLong1(startPoint.getLongitude());
                    Set<RepositoryEventWaypoint>waypoints = startPoint.getWaypoints();
                    if (waypoints != null) {
                        Iterator witer = waypoints.iterator();
                        RepositoryEventWaypoint waypoint;
                        int waycount = 0;
                        while (witer.hasNext()) {
                            waycount++;
                            waypoint = (RepositoryEventWaypoint)witer.next();
                            if (waycount == 1) {
                                this.setWaypointId1_1(waypoint.getWaypointId());
                                this.setWaypointLat1_1(waypoint.getLatitude());
                                this.setWaypointLong1_1(waypoint.getLongitude());
                            } else if (waycount == 2) {
                                this.setWaypointId1_2(waypoint.getWaypointId());
                                this.setWaypointLat1_2(waypoint.getLatitude());
                                this.setWaypointLong1_2(waypoint.getLongitude());
                            }
                        }
                    }
                } else if (count == 2) {
                    this.setStartPoint2(startPoint.getTitle());
                    this.setStartPointId2(startPoint.getStartPointId());
                    this.setStartPointLat2(startPoint.getLatitude());
                    this.setStartPointLong2(startPoint.getLongitude());
                    Set<RepositoryEventWaypoint>waypoints = startPoint.getWaypoints();
                    if (waypoints != null) {
                        Iterator witer = waypoints.iterator();
                        RepositoryEventWaypoint waypoint;
                        int waycount = 0;
                        while (witer.hasNext()) {
                            waycount++;
                            waypoint = (RepositoryEventWaypoint)witer.next();
                            if (waycount == 1) {
                                this.setWaypointId2_1(waypoint.getWaypointId());
                                this.setWaypointLat2_1(waypoint.getLatitude());
                                this.setWaypointLong2_1(waypoint.getLongitude());
                            } else if (waycount == 2) {
                                this.setWaypointId2_2(waypoint.getWaypointId());
                                this.setWaypointLat2_2(waypoint.getLatitude());
                                this.setWaypointLong2_2(waypoint.getLongitude());
                            }
                        }
                    }
                } else if (count == 3) {
                    this.setStartPoint3(startPoint.getTitle());
                    this.setStartPointId3(startPoint.getStartPointId());
                    this.setStartPointLat3(startPoint.getLatitude());
                    this.setStartPointLong3(startPoint.getLongitude());
                    Set<RepositoryEventWaypoint>waypoints = startPoint.getWaypoints();
                    if (waypoints != null) {
                        Iterator witer = waypoints.iterator();
                        RepositoryEventWaypoint waypoint;
                        int waycount = 0;
                        while (witer.hasNext()) {
                            waycount++;
                            waypoint = (RepositoryEventWaypoint)witer.next();
                            if (waycount == 1) {
                                this.setWaypointId3_1(waypoint.getWaypointId());
                                this.setWaypointLat3_1(waypoint.getLatitude());
                                this.setWaypointLong3_1(waypoint.getLongitude());
                            } else if (waycount == 2) {
                                this.setWaypointId3_2(waypoint.getWaypointId());
                                this.setWaypointLat3_2(waypoint.getLatitude());
                                this.setWaypointLong3_2(waypoint.getLongitude());
                            }
                        }
                    }
                } else if (count > 3) {
                    break;
                }
            }
        }
        kmlObjects = (Set)event.getKmlObjects();
        if (kmlObjects != null) {
            Iterator iter = kmlObjects.iterator();
            RepositoryEventKml kmlObject;
            int count = 0;
            while (iter.hasNext()) {
                count++;
                kmlObject = (RepositoryEventKml)iter.next();
                if (count == 1) {
                    this.setKmlId1(kmlObject.getKmlId());
                    this.setKmlTitle1(kmlObject.getTitle());
                    this.setKmlType1(kmlObject.getType());
                    this.setKmlStartLat1(kmlObject.getStartLat());
                    this.setKmlStartLong1(kmlObject.getStartLong());
                    this.setKmlXml1(kmlObject.getXml());
                } else if (count == 2) {
                    this.setKmlId2(kmlObject.getKmlId());
                    this.setKmlTitle2(kmlObject.getTitle());
                    this.setKmlType2(kmlObject.getType());
                    this.setKmlStartLat2(kmlObject.getStartLat());
                    this.setKmlStartLong2(kmlObject.getStartLong());
                    this.setKmlXml2(kmlObject.getXml());
                } else if (count == 3) {
                    this.setKmlId3(kmlObject.getKmlId());
                    this.setKmlTitle3(kmlObject.getTitle());
                    this.setKmlType3(kmlObject.getType());
                    this.setKmlStartLat3(kmlObject.getStartLat());
                    this.setKmlStartLong3(kmlObject.getStartLong());
                    this.setKmlXml3(kmlObject.getXml());
                } else if (count > 3) {
                    break;
                }
            }
        }
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

    // Location data
    public String getLocation() {
        return location;
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
    
    
    // Up to 3 startPoints: each with id, title, lat and long

    public String getStartPoint1() {
        return startPoint1;
    }

    public void setStartPoint1(String startPoint1) {
        this.startPoint1 = startPoint1;
    }

    public String getStartPointId1() {
        return startPointId1;
    }

    public void setStartPointId1(String startPointId1) {
        this.startPointId1 = startPointId1;
    }

    public String getStartPointLat1() {
        return startPointLat1;
    }

    public void setStartPointLat1(String startPointLat1) {
        this.startPointLat1 = startPointLat1;
    }

    public String getStartPoint2() {
        return startPoint2;
    }

    public void setStartPoint2(String startPoint2) {
        this.startPoint2 = startPoint2;
    }

    public String getStartPointId2() {
        return startPointId2;
    }

    public void setStartPointId2(String startPointId2) {
        this.startPointId2 = startPointId2;
    }

    public String getStartPointLat2() {
        return startPointLat2;
    }

    public void setStartPointLat2(String startPointLat2) {
        this.startPointLat2 = startPointLat2;
    }

    public String getStartPointLong2() {
        return startPointLong2;
    }

    public void setStartPointLong2(String startPointLong2) {
        this.startPointLong2 = startPointLong2;
    }

    public String getStartPointLong1() {
        return startPointLong1;
    }

    public void setStartPointLong1(String startPointLong1) {
        this.startPointLong1 = startPointLong1;
    }

    public String getStartPoint3() {
        return startPoint3;
    }

    public void setStartPoint3(String startPoint3) {
        this.startPoint3 = startPoint3;
    }

    public String getStartPointId3() {
        return startPointId3;
    }

    public void setStartPointId3(String startPointId3) {
        this.startPointId3 = startPointId3;
    }

    public String getStartPointLat3() {
        return startPointLat3;
    }

    public void setStartPointLat3(String startPointLat3) {
        this.startPointLat3 = startPointLat3;
    }

    public String getStartPointLong3() {
        return startPointLong3;
    }

    public void setStartPointLong3(String startPointLong3) {
        this.startPointLong3 = startPointLong3;
    }


    // Waypoints: 2 for each of 3 StartPoints for now: each with id, lat and long (not title)

    public String getWaypointId1_1() {
        return waypointId1_1;
    }

    public void setWaypointId1_1(String waypointId1_1) {
        this.waypointId1_1 = waypointId1_1;
    }

    public String getWaypointLat1_1() {
        return waypointLat1_1;
    }

    public void setWaypointLat1_1(String waypointLat1_1) {
        this.waypointLat1_1 = waypointLat1_1;
    }

    public String getWaypointLong1_1() {
        return waypointLong1_1;
    }

    public void setWaypointLong1_1(String waypointLong1_1) {
        this.waypointLong1_1 = waypointLong1_1;
    }


    public String getWaypointId1_2() {
        return waypointId1_2;
    }

    public void setWaypointId1_2(String waypointId1_2) {
        this.waypointId1_2 = waypointId1_2;
    }

    public String getWaypointLat1_2() {
        return waypointLat1_2;
    }

    public void setWaypointLat1_2(String waypointLat1_2) {
        this.waypointLat1_2 = waypointLat1_2;
    }

    public String getWaypointLong1_2() {
        return waypointLong1_2;
    }

    public void setWaypointLong1_2(String waypointLong1_2) {
        this.waypointLong1_2 = waypointLong1_2;
    }

    public String getWaypointId2_1() {
        return waypointId2_1;
    }

    public void setWaypointId2_1(String waypointId2_1) {
        this.waypointId2_1 = waypointId2_1;
    }

    public String getWaypointLat2_1() {
        return waypointLat2_1;
    }

    public void setWaypointLat2_1(String waypointLat2_1) {
        this.waypointLat2_1 = waypointLat2_1;
    }

    public String getWaypointLong2_1() {
        return waypointLong2_1;
    }

    public void setWaypointLong2_1(String waypointLong2_1) {
        this.waypointLong2_1 = waypointLong2_1;
    }

    public String getWaypointId2_2() {
        return waypointId2_2;
    }

    public void setWaypointId2_2(String waypointId2_2) {
        this.waypointId2_2 = waypointId2_2;
    }

    public String getWaypointLat2_2() {
        return waypointLat2_2;
    }

    public void setWaypointLat2_2(String waypointLat2_2) {
        this.waypointLat2_2 = waypointLat2_2;
    }

    public String getWaypointLong2_2() {
        return waypointLong2_2;
    }

    public void setWaypointLong2_2(String waypointLong2_2) {
        this.waypointLong2_2 = waypointLong2_2;
    }

    public String getWaypointId3_1() {
        return waypointId3_1;
    }

    public void setWaypointId3_1(String waypointId3_1) {
        this.waypointId3_1 = waypointId3_1;
    }

    public String getWaypointLat3_1() {
        return waypointLat3_1;
    }

    public void setWaypointLat3_1(String waypointLat3_1) {
        this.waypointLat3_1 = waypointLat3_1;
    }

    public String getWaypointLong3_1() {
        return waypointLong3_1;
    }

    public void setWaypointLong3_1(String waypointLong3_1) {
        this.waypointLong3_1 = waypointLong3_1;
    }


    public String getWaypointId3_2() {
        return waypointId3_2;
    }

    public void setWaypointId3_2(String waypointId3_2) {
        this.waypointId3_2 = waypointId3_2;
    }

    public String getWaypointLat3_2() {
        return waypointLat3_2;
    }

    public void setWaypointLat3_2(String waypointLat3_2) {
        this.waypointLat3_2 = waypointLat3_2;
    }

    public String getWaypointLong3_2() {
        return waypointLong3_2;
    }

    public void setWaypointLong3_2(String waypointLong3_2) {
        this.waypointLong3_2 = waypointLong3_2;
    }

    // Up to 3 kml objects
    public String getKmlId1() {
        return kmlId1;
    }

    public void setKmlId1(String kmlId1) {
        this.kmlId1 = kmlId1;
    }

    public String getKmlTitle1() {
        return kmlTitle1;
    }

    public void setKmlTitle1(String kmlTitle1) {
        this.kmlTitle1 = kmlTitle1;
    }

    public String getKmlType1() {
        return kmlType1;
    }

    public void setKmlType1(String kmlType1) {
        this.kmlType1 = kmlType1;
    }

    public String getKmlStartLat1() {
        return kmlStartLat1;
    }

    public void setKmlStartLat1(String kmlStartLat1) {
        this.kmlStartLat1 = kmlStartLat1;
    }

    public String getKmlStartLong1() {
        return kmlStartLong1;
    }

    public void setKmlStartLong1(String kmlStartLong1) {
        this.kmlStartLong1 = kmlStartLong1;
    }

    public String getKmlXml1() {
        return kmlXml1;
    }

    public void setKmlXml1(String kmlXml1) {
        this.kmlXml1 = kmlXml1;
    }

    public String getKmlId2() {
        return kmlId2;
    }

    public void setKmlId2(String kmlId2) {
        this.kmlId2 = kmlId2;
    }

    public String getKmlTitle2() {
        return kmlTitle2;
    }

    public void setKmlTitle2(String kmlTitle2) {
        this.kmlTitle2 = kmlTitle2;
    }

    public String getKmlType2() {
        return kmlType2;
    }

    public void setKmlType2(String kmlType2) {
        this.kmlType2 = kmlType2;
    }

    public String getKmlStartLat2() {
        return kmlStartLat2;
    }

    public void setKmlStartLat2(String kmlStartLat2) {
        this.kmlStartLat2 = kmlStartLat2;
    }

    public String getKmlStartLong2() {
        return kmlStartLong2;
    }

    public void setKmlStartLong2(String kmlStartLong2) {
        this.kmlStartLong2 = kmlStartLong2;
    }

    public String getKmlXml2() {
        return kmlXml2;
    }

    public void setKmlXml2(String kmlXml2) {
        this.kmlXml2 = kmlXml2;
    }

    public String getKmlId3() {
        return kmlId3;
    }

    public void setKmlId3(String kmlId3) {
        this.kmlId3 = kmlId3;
    }

    public String getKmlTitle3() {
        return kmlTitle3;
    }

    public void setKmlTitle3(String kmlTitle3) {
        this.kmlTitle3 = kmlTitle3;
    }

    public String getKmlType3() {
        return kmlType3;
    }

    public void setKmlType3(String kmlType3) {
        this.kmlType3 = kmlType3;
    }

    public String getKmlStartLat3() {
        return kmlStartLat3;
    }

    public void setKmlStartLat3(String kmlStartLat3) {
        this.kmlStartLat3 = kmlStartLat3;
    }

    public String getKmlStartLong3() {
        return kmlStartLong3;
    }

    public void setKmlStartLong3(String kmlStartLong3) {
        this.kmlStartLong3 = kmlStartLong3;
    }

    public String getKmlXml3() {
        return kmlXml3;
    }

    public void setKmlXml3(String kmlXml3) {
        this.kmlXml3 = kmlXml3;
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
