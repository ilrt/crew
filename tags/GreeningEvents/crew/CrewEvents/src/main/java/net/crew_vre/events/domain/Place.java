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

import java.util.Set;
import java.util.HashSet;

/**
 * <p>The Place object represents the location/venue where an event is being
 * held. It holds geo information that might be useful for maps. A place can
 * also have other places - for example, a venue might have a number
 * of rooms.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Place.java 1188 2009-03-31 13:09:20Z cmmaj $
 */
public class Place extends PlacePart {

    /**
     * The latitude value (decimal).
     */
    private Float latitude;

    /**
     * The longitude value (decimal).
     */
    private Float longitude;

    /**
     * The altitude value (decimal).
     */
    private Float altitude;

    /**
     * A description of the location for inserting into a Google InfoWindow - may include html tags
     */
    private String locationDescription;

    /**
     * A URL connected with the location
     */
    private String locationUrl;

    /**
     * URL of a thumbnail images associated with the location
     */
    private String locationThumbUrl;

    /**
     * URL for further images associated with the location
     */
    private String locationImagesUrl;

    /**
     * List of other places that this place might hold.
     */
    private Set<Place> places = new HashSet<Place>();

    public Place() { }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(final Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(final Float longitude) {
        this.longitude = longitude;
    }

    public Float getAltitude() {
        return altitude;
    }

    public void setAltitude(final Float altitude) {
        this.altitude = altitude;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getLocationThumbUrl() {
        return locationThumbUrl;
    }

    public void setLocationThumbUrl(String locationThumbUrl) {
        this.locationThumbUrl = locationThumbUrl;
    }

    public String getLocationImagesUrl() {
        return locationImagesUrl;
    }

    public void setLocationImagesUrl(String locationImagesUrl) {
        this.locationImagesUrl = locationImagesUrl;
    }

    public Set<Place> getLocations() {
        return places;
    }

    public void setLocations(final Set<Place> places) {
        this.places = places;
    }

}
