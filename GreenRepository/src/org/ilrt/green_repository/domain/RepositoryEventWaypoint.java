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
@Table(name = "WAYPOINT")
public class RepositoryEventWaypoint implements Serializable {

    public RepositoryEventWaypoint(){}

    // Create new waypoint with new id
    public RepositoryEventWaypoint(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        waypointId = getWaypointHash();
    }

    // Re-create existing waypoint object with existing id
    public RepositoryEventWaypoint(String waypointId, String latitude, String longitude) {
        this.waypointId = waypointId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getWaypointId() {
        return waypointId;
    }

    public String getWaypointHash() {
        String waypointHash = "";
        if (this.getLatitude() != null && this.getLongitude() != null) {
            waypointHash = md5Hash(this.getLatitude() + this.getLongitude());
            waypointHash = "WPT_" + waypointHash;
        }
        return waypointHash;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepositoryEventWaypoint)) return false;

        RepositoryEventWaypoint event = (RepositoryEventWaypoint) o;
        return waypointId.equals(event.getWaypointId());
    }

    @Override
    public int hashCode() {
        int result;
        result = waypointId.hashCode();
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

    @Id
    @Column(name = "WAYPOINTID")
    private String waypointId;

    @Column(name = "LATITUDE", nullable = true)
    private String latitude;

    @Column(name = "LONGITUDE", nullable = true)
    private String longitude;


}
