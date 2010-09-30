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
import java.util.Date;


/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
@Entity
@Table(name = "KML")
public class RepositoryEventKml implements Serializable {

    public RepositoryEventKml(){}

    // Create new startPoint with new id
    public RepositoryEventKml(String title, String type, String startLat,
            String startLong, String kxml) {
        this.title = title;
        this.type = type;
        this.startLat = startLat;
        this.startLong = startLong;
        this.kxml = kxml;
        kmlId = getKmlHash();
    }

    // Re-create existing startPoint object with existing id
    public RepositoryEventKml(String kmlId, String title, String type, String startLat,
            String startLong, String kxml) {
        this.kmlId = kmlId;
        this.title = title;
        this.type = type;
        this.startLat = startLat;
        this.startLong = startLong;
        this.kxml = kxml;
    }

    public String getKmlId() {
        return kmlId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLong(String startLong) {
        this.startLong = startLong;
    }

    public String getStartLong() {
        return startLong;
    }

    public String getKmlHash() {
        String kmlHash = "";
        if (this.getTitle() != null) {
            String time = new Date().toString();
            kmlHash = md5Hash(this.getTitle() + time);
            kmlHash = "KML_" + kmlHash;
        }
        return kmlHash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXml() {
        return kxml;
    }

    public void setXml(String kxml) {
        this.kxml = kxml;
    }

 //   public Set<RepositoryEvent> getAssociatedEvents() {
 //       return associatedEvents;
 //   }

 //   public void setAssociatedEvents(Set<RepositoryEvent> associatedEvents) {
 //       this.associatedEvents = associatedEvents;
 //   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepositoryEventKml)) return false;

        RepositoryEventKml kml = (RepositoryEventKml) o;
        return kmlId.equals(kml.getKmlId());
    }

    @Override
    public int hashCode() {
        int result;
        result = kmlId.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + type.hashCode();
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
    @Column(name = "KMLID")
    private String kmlId;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "STARTLAT")
    private String startLat;

    @Column(name = "STARTLONG")
    private String startLong;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "KXML", columnDefinition="LONG VARCHAR", nullable = false)
    private String kxml;

    //@ManyToMany(mappedBy = "kmlObjects", fetch = FetchType.EAGER)
   // private Set<RepositoryEvent> associatedEvents = new HashSet<RepositoryEvent>();


}
