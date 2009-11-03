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
package net.crew_vre.events;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.events.domain.PlacePart;
import net.crew_vre.events.domain.Subject;
import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.jena.vocabulary.ESWC2006;
import net.crew_vre.jena.vocabulary.IUGO;
import net.crew_vre.jena.vocabulary.SKOS;

import org.caboto.CabotoUtility;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Utility.java 1188 2009-03-31 13:09:20Z cmmaj $
 */
public class Utility {

    /**
     * Private constrructor since it just has static methods.
     */
    private Utility() {
    }

    final static DateTimeFormatter dtf = ISODateTimeFormat.dateTimeParser().withOffsetParsed();
    
    /**
     * <p>Utility to convert a date string into a DateTime instance.
     *
     * @param stringDateTime the date in string format
     * @return a DateTime represented by the string
     * @throws ParseException if there is an error parsing the string
     */
    public static DateTime parseStringToDateTime(String stringDateTime) throws ParseException {
        return dtf.parseDateTime(stringDateTime);
    }

    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static LocalDate parseStringToLocalDate(String stringDate) throws ParseException {

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(stringDate);
        return new LocalDate(date.getTime());
    }

    public static Model createModelFromPlacePart(PlacePart place) {
        Model model = ModelFactory.createDefaultModel();
        Resource placeResource = model.createResource(place.getId());
        placeResource.addProperty(DC.title, place.getTitle());
        return model;
    }

    public static Model createModelFromSubject(Subject subject) {
        Model model = ModelFactory.createDefaultModel();
        Resource subjectResource = model.createResource(subject.getId());
        subjectResource.addProperty(SKOS.prefLabel, subject.getName());
        return model;
    }

    public static Model createModelFromEventParent(EventParent event) {
        Model model = ModelFactory.createDefaultModel();
        Resource eventResource = model.createResource(event.getId());
        eventResource.addProperty(DC.title, event.getTitle());
        return model;
    }

    public static Model createModelFromEventPart(EventPart event) {
        Model model = createModelFromEventParent(event);
        Resource eventResource = model.createResource(event.getId());
        if (event.getDescription() != null) {
            eventResource.addProperty(DC.description, event.getDescription());
        }
        if (event.getStartDateTime() != null) {
            eventResource.addProperty(ESWC2006.hasStartDateTime,
                    CabotoUtility.parseDate(event.getStartDateTime().toDate()),
                    XSDDatatype.XSDdateTime);
        }
        if (event.getEndDateTime() != null) {
            eventResource.addProperty(ESWC2006.hasEndDateTime,
                    CabotoUtility.parseDate(event.getEndDateTime().toDate()),
                    XSDDatatype.XSDdateTime);
        }
        return model;
    }

    public static Model createModelFromEvent(Event event) {
        Model model = createModelFromEventPart(event);
        Resource eventResource = model.createResource(event.getId());

        if ((event.getPartOf() != null) && !event.getPartOf().isEmpty()) {
            eventResource.addProperty(RDF.type, ESWC2006.Event);
        } else {
            eventResource.addProperty(RDF.type, IUGO.MainEvent);
            eventResource.addProperty(Crew.HAS_START_DATE,
                    event.getStartDate().toString(),
                    XSDDatatype.XSDdate);
            eventResource.addProperty(Crew.HAS_END_DATE,
                    event.getStartDate().toString(),
                    XSDDatatype.XSDdate);
        }

        List<PlacePart> places = event.getPlaces();
        if (places != null) {
            for (PlacePart place : places) {
                eventResource.addProperty(ESWC2006.hasLocation,
                        model.createResource(place.getId()));
            }
        }

        if (event.getProgramme() != null) {
            eventResource.addProperty(ESWC2006.hasProgramme,
                    model.createResource(event.getProgramme()));
        }

        if (event.getProceedings() != null) {
            // TODO: Check if we should be using ESWC2006 here as hasProceedings doesn't exist!
            eventResource.addProperty(
                    model.createProperty(ESWC2006.NS, "hasProceedings"),
                    model.createResource(event.getProceedings()));
        }

        if (event.getParts() != null) {
            for (EventPart part : event.getParts()) {
                Resource partResource = model.createResource(part.getId());
                eventResource.addProperty(ESWC2006.hasPart, partResource);
                partResource.addProperty(ESWC2006.isPartOf, eventResource);
            }
        }

        if ((event.getPartOf() != null) && (event.getPartOf().size() > 0)) {
            EventParent parent = event.getPartOf().get(
                    event.getPartOf().size() - 1);
            Resource parentResource = model.createResource(parent.getId());
            eventResource.addProperty(ESWC2006.isPartOf, parentResource);
            parentResource.addProperty(ESWC2006.hasPart, eventResource);
        }

        if (event.getSubjects() != null) {
            for (Subject subject : event.getSubjects()) {
                eventResource.addProperty(IUGO.hasSubject,
                        model.createResource(subject.getId()));
            }
        }

        if (event.getTags() != null) {
            for (String tag : event.getTags()) {
                eventResource.addProperty(IUGO.hasTag, tag);
            }
        }

        return model;
    }

}
