/**
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
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

package net.crew_vre.recordings;

import java.util.Iterator;
import java.util.List;

import org.caboto.CabotoUtility;

import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.ReplayLayout;
import net.crew_vre.recordings.domain.ReplayLayoutPosition;
import net.crew_vre.recordings.domain.Stream;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Utility functions for the recording DAO
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Utility {

    private Utility() {
        // Does Nothing
    }

    /**
     * Generates a model for a recording
     * @param recording The recording
     * @return The model
     */
    public static Model getModelForRecording(Recording recording) {
        Model model = ModelFactory.createDefaultModel();

        String uri = recording.getUri();
        Resource recordingResource = model.createResource(uri);

        recordingResource.addProperty(RDF.type, Crew.TYPE_RECORDING);

        recordingResource.addProperty(Crew.HAS_ID, recording.getId());

        if (recording.getEventUri() != null) {
            recordingResource.addProperty(Crew.IS_RECORDING_OF,
                model.createResource(recording.getEventUri()));
        }

        recordingResource.addProperty(Crew.HAS_START_TIME,
                model.createTypedLiteral(CabotoUtility.parseDate(
                        recording.getStartTime()),
                        XSDDatatype.XSDdateTime));

        recordingResource.addProperty(Crew.HAS_END_TIME,
                model.createTypedLiteral(CabotoUtility.parseDate(
                        recording.getEndTime()),
                        XSDDatatype.XSDdateTime));

        return model;
    }

    /**
     * Creates a model for a stream
     * @param stream The stream
     * @return The model
     */
    public static Model createModelForStream(Stream stream) {
        Model model = ModelFactory.createDefaultModel();

     // generate uri and resource for the annotation
        Resource streamResource = model.createResource(stream.getUri());

        streamResource.addProperty(Crew.HAS_SSRC, stream.getSsrc());

        streamResource.addProperty(Crew.HAS_START_TIME,
                model.createTypedLiteral(CabotoUtility.parseDate(
                        stream.getStartTime()),
                        XSDDatatype.XSDdateTime));

        streamResource.addProperty(Crew.HAS_END_TIME,
                model.createTypedLiteral(CabotoUtility.parseDate(
                        stream.getEndTime()),
                        XSDDatatype.XSDdateTime));

        streamResource.addProperty(Crew.HAS_FIRST_TIMESTAMP,
                model.createTypedLiteral(stream.getFirstTimestamp(),
                        XSDDatatype.XSDlong));

        streamResource.addProperty(Crew.HAS_PACKETS_SEEN,
                model.createTypedLiteral(stream.getPacketsSeen(),
                        XSDDatatype.XSDlong));

        streamResource.addProperty(Crew.HAS_PACKETS_MISSED,
                model.createTypedLiteral(stream.getPacketsMissed(),
                        XSDDatatype.XSDlong));

        streamResource.addProperty(Crew.HAS_BYTES,
                model.createTypedLiteral(stream.getBytes(),
                        XSDDatatype.XSDlong));

        streamResource.addProperty(Crew.HAS_RTP_TYPE,
                model.createTypedLiteral(stream.getRtpType().getId(),
                        XSDDatatype.XSDinteger));

        if (stream.getCname() != null) {
            streamResource.addProperty(Crew.HAS_CNAME, stream.getCname());
        }

        if (stream.getName() != null) {
            streamResource.addProperty(Crew.HAS_NAME, stream.getName());
        }

        if (stream.getEmail() != null) {
            streamResource.addProperty(Crew.HAS_EMAIL, stream.getEmail());
        }

        if (stream.getPhone() != null) {
            streamResource.addProperty(Crew.HAS_PHONE_NUMBER,
                    stream.getPhone());
        }

        if (stream.getLocation() != null) {
            streamResource.addProperty(Crew.HAS_LOCATION, stream.getLocation());
        }

        if (stream.getTool() != null) {
            streamResource.addProperty(Crew.HAS_TOOL, stream.getTool());
        }

        if (stream.getNote() != null) {
            streamResource.addProperty(Crew.HAS_NOTE, stream.getNote());
        }

        Resource recordingResource = model.createResource(
                stream.getRecording().getUri());

        recordingResource.addProperty(Crew.HAS_STREAM, streamResource);

        return model;
    }

    /**
     * Creates a model for a replay layout
     * @param replayLayout The layout
     * @return The model
     */
    public static Model createModelForLayout(ReplayLayout replayLayout) {
        Model model = ModelFactory.createDefaultModel();

        Resource recordingResource = model.getResource(
                replayLayout.getRecording().getUri());
        Resource replayLayoutResource = model.createResource(
                replayLayout.getUri());

        replayLayoutResource.addProperty(RDF.type, Crew.TYPE_LAYOUT);
        replayLayoutResource.addProperty(Crew.HAS_TIMESTAMP,
                model.createTypedLiteral(CabotoUtility.parseDate(
                        replayLayout.getTime()),
                        XSDDatatype.XSDdateTime));
        replayLayoutResource.addProperty(Crew.HAS_NAME,
                replayLayout.getName());

        List<ReplayLayoutPosition> positions =
            replayLayout.getLayoutPositions();
        Iterator<ReplayLayoutPosition> posIter = positions.iterator();
        while (posIter.hasNext()) {
            ReplayLayoutPosition replayLayoutPosition = posIter.next();
            Resource replayLayoutPositionResource = model.createResource(
                    replayLayoutPosition.getUri());
            if (replayLayoutPosition.getStream() != null) {
                replayLayoutPositionResource.addProperty(RDF.type,
                        Crew.TYPE_LAYOUT_POSITION);
                replayLayoutPositionResource.addProperty(Crew.HAS_NAME,
                        replayLayoutPosition.getName());
                replayLayoutPositionResource.addProperty(Crew.HAS_STREAM ,
                        model.createResource(
                                replayLayoutPosition.getStream().getUri()));

                replayLayoutResource.addProperty(Crew.HAS_LAYOUT_POSITION,
                        replayLayoutPositionResource);
            }
        }
        recordingResource.addProperty(Crew.HAS_LAYOUT,
                replayLayoutResource);
        return model;
    }
}
