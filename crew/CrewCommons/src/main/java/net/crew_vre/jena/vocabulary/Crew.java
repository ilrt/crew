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
package net.crew_vre.jena.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The CREW vocabulary
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Crew {

    private static final Model MODEL = ModelFactory.createDefaultModel();

    /**
     * The CREW namespace
     */
    public static final String NS = "http://www.crew-vre.net/ontology#";

    /**
     * The type of a bridge registry
     */
    public static final Resource TYPE_BRIDGE_REGISTRY =
        MODEL.createResource(NS + "BridgeRegistry");

    /**
     * The type of a venue server
     */
    public static final Resource TYPE_VENUE_SERVER =
        MODEL.createResource(NS + "VenueServer");

    /**
     * The type of a Crew Server
     */
    public static final Resource TYPE_CREW_SERVER =
        MODEL.createResource(NS + "CrewServer");

    /**
     * Link to a bridge
     */
    public static final Property HAS_BRIDGE =
        MODEL.createProperty(NS, "has-bridge");

    /**
     * Link to a venue
     */
    public static final Property HAS_VENUE =
        MODEL.createProperty(NS, "has-venue");

    /**
     * The type of a bridge
     */
    public static final Resource TYPE_BRIDGE =
        MODEL.createResource(NS + "Bridge");

    /**
     * The type of a device
     */
    public static final Resource TYPE_DEVICE =
        MODEL.createResource(NS + "Device");

    /**
     * The type of a venue
     */
    public static final Resource TYPE_VENUE =
        MODEL.createResource(NS + "Venue");

    /**
     * The type of a CREW recording
     */
    public static final Resource TYPE_RECORDING =
        MODEL.createResource(NS + "Recording");

    /**
     * The type of a CREW layout
     */
    public static final Resource TYPE_LAYOUT =
        MODEL.createResource(NS + "Layout");

    /**
     * The type of a CREW layout position
     */
    public static final Resource TYPE_LAYOUT_POSITION =
        MODEL.createResource(NS + "LayoutPosition");

    /**
     * Link to an eventUri
     */
    public static final Property IS_RECORDING_OF =
        MODEL.createProperty(NS, "is-recording-of");


    /**
     * Link to a CREW id
     */
    public static final Property HAS_ID =
        MODEL.createProperty(NS, "has-id");

    /**
     * Link to a start-time
     */
    public static final Property HAS_START_TIME =
        MODEL.createProperty(NS, "has-start-time");

    /**
     * Link to a end-time
     */
    public static final Property HAS_END_TIME =
        MODEL.createProperty(NS, "has-end-time");


    /**
     * Link to a start-date
     */
    public static final Property HAS_START_DATE =
        MODEL.createProperty(NS, "hasStartDate");

    /**
     * Link to a end-date
     */
    public static final Property HAS_END_DATE =
        MODEL.createProperty(NS, "hasEndDate");


    /**
     * Link to a selected-video-stream
     */
    public static final Property HAS_SELECTED_VIDEO_STREAM =
        MODEL.createProperty(NS, "has-selected-video-stream");

    /**
     * Link to a selected-screen-stream
     */
    public static final Property HAS_SELECTED_SCREEN_STREAM =
        MODEL.createProperty(NS, "has-selected-screen-stream");

    /**
     * Link to a stream
     */
    public static final Property HAS_STREAM =
        MODEL.createProperty(NS, "has-stream");

    public static final Property HAS_SSRC =
        MODEL.createProperty(NS, "has-ssrc");

    public static final Property HAS_FIRST_TIMESTAMP=
        MODEL.createProperty(NS, "has-first-timestamp");

    public static final Property HAS_PACKETS_SEEN=
        MODEL.createProperty(NS, "has-packets-seen");

    public static final Property HAS_PACKETS_MISSED=
        MODEL.createProperty(NS, "has-packets-missed");

    public static final Property HAS_BYTES=
        MODEL.createProperty(NS, "has-bytes");

    public static final Property HAS_RTP_TYPE=
        MODEL.createProperty(NS, "has-rtp-type");

    public static final Property RTP_MEDIA_TYPE=
        MODEL.createProperty(NS,"rtp-media-type");

    public static final Property RTP_ENCODING=
        MODEL.createProperty(NS,"rtp-encoding");

    public static final Property RTP_CLOCK_RATE=
        MODEL.createProperty(NS,"rtp-clock-rate");

    public static final Property HAS_CNAME=
        MODEL.createProperty(NS, "has-cname");
    public static final Property HAS_NAME=
        MODEL.createProperty(NS, "has-name");
    public static final Property HAS_EMAIL=
        MODEL.createProperty(NS, "has-email");
    public static final Property HAS_PHONE_NUMBER=
        MODEL.createProperty(NS, "has-phone-number");
    public static final Property HAS_LOCATION=
        MODEL.createProperty(NS, "has-location");
    public static final Property HAS_TOOL=
        MODEL.createProperty(NS, "has-tool");
    public static final Property HAS_NOTE=
        MODEL.createProperty(NS, "has-note");

    public static final Property HAS_LAYOUT=
        MODEL.createProperty(NS, "has-layout");
    public static final Property HAS_TIMESTAMP=
        MODEL.createProperty(NS, "has-timestamp");
    public static final Property HAS_LAYOUT_POSITION=
        MODEL.createProperty(NS, "has-layout-position");

    public static final Property HAS_GRAPH=
        MODEL.createProperty(NS, "has-graph");

}
