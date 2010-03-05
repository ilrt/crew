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
package net.crew_vre.events.rest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.crew_vre.events.Utility;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventParent;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.events.domain.PlacePart;
import net.crew_vre.events.domain.Subject;
import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.jena.vocabulary.ESWC2006;
import net.crew_vre.jena.vocabulary.IUGO;
import net.crew_vre.jena.vocabulary.SKOS;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * Utilities for Event REST resources
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Utils {

    private Utils() {
        // Does Nothing
    }

    /**
     * Fills in an event parent from a resource
     * @param event The event to fill in
     * @param resource The resource to extract from
     */
    public static void fillInParent(EventParent event, Resource resource) {
        event.setUri(resource.getURI());
        if (resource.hasProperty(DC.title)) {
            event.setTitle(resource.getProperty(
                    DC.title).getLiteral().getLexicalForm());
        }
        if (resource.hasProperty(Crew.HAS_GRAPH)) {
            event.setGraph(resource.getProperty(
                    Crew.HAS_GRAPH).getResource().getURI());
        }
    }

    /**
     * Fills in an event part from a resource
     * @param event The event to fill in
     * @param resource The resource to extract from
     */
    public static void fillInPart(EventPart event, Resource resource) {
        fillInParent(event, resource);
        if (resource.hasProperty(DC.description)) {
            event.setTitle(resource.getProperty(
                    DC.title).getLiteral().getLexicalForm());
        }
        try {
            if (resource.hasProperty(ESWC2006.hasStartDateTime)) {
                event.setStartDateTime(Utility.parseStringToDateTime(
                    resource.getProperty(
                    ESWC2006.hasStartDateTime).getLiteral().getLexicalForm()));
            }
            if (resource.hasProperty(ESWC2006.hasEndDateTime)) {
                event.setEndDateTime(Utility.parseStringToDateTime(
                    resource.getProperty(
                    ESWC2006.hasEndDateTime).getLiteral().getLexicalForm()));
            }
            if (resource.hasProperty(Crew.HAS_START_DATE)) {
                event.setStartDate(Utility.parseStringToLocalDate(
                    resource.getProperty(
                    Crew.HAS_START_DATE).getLiteral().getLexicalForm()));
            }
            if (resource.hasProperty(Crew.HAS_END_DATE)) {
                event.setStartDate(Utility.parseStringToLocalDate(
                    resource.getProperty(
                    Crew.HAS_END_DATE).getLiteral().getLexicalForm()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fills in an event from a resource
     * @param event The event to fill in
     * @param resource The resource to extract from
     */
    public static void fillInEvent(Event event, Resource resource) {
        fillInPart(event, resource);

        if (resource.hasProperty(ESWC2006.hasLocation)) {
            List<PlacePart> places = new ArrayList<PlacePart>();
            StmtIterator iter = resource.listProperties(
                    ESWC2006.hasLocation);
            while (iter.hasNext()) {
                Statement statement = iter.nextStatement();
                Resource placeResource = statement.getResource();
                PlacePart place = new PlacePart();
                place.setUri(placeResource.getURI());
                if (placeResource.hasProperty(DC.title)) {
                    place.setTitle(placeResource.getProperty(
                            DC.title).getLiteral().getLexicalForm());
                }
                places.add(place);
            }
            event.setPlaces(places);
        }

        if (resource.hasProperty(ESWC2006.hasProgramme)) {
            event.setProceedings(resource.getProperty(
                    ESWC2006.hasProgramme).getResource().getURI());
        }

        Property hasProceedings = resource.getModel().createProperty(
                ESWC2006.NS, "hasProceedings");
        if (resource.hasProperty(hasProceedings)) {
            event.setProceedings(resource.getProperty(
                    hasProceedings).getResource().getURI());
        }

        List<EventPart> parts = new ArrayList<EventPart>();
        StmtIterator partIter = resource.listProperties(ESWC2006.hasPart);
        while (partIter.hasNext()) {
            EventPart part = new EventPart();
            Resource partResource = partIter.nextStatement().getResource();
            fillInPart(part, partResource);
            parts.add(part);
        }
        Collections.sort(parts);
        event.setParts(parts);

        List<EventParent> parents = new ArrayList<EventParent>();
        StmtIterator partOfIter = resource.listProperties(ESWC2006.isPartOf);
        while (partOfIter.hasNext()) {
            EventParent parent = new EventParent();
            Resource parentResource =
                partOfIter.nextStatement().getResource();
            fillInParent(parent, parentResource);
            parents.add(parent);
        }
        event.setPartOf(parents);

        List<Subject> subjects = new ArrayList<Subject>();
        StmtIterator subjectIter = resource.listProperties(IUGO.hasSubject);
        while (subjectIter.hasNext()) {
            Subject subject = new Subject();
            Resource subjectResource =
                subjectIter.nextStatement().getResource();
            subject.setUri(subjectResource.getURI());
            if (subjectResource.hasProperty(DC.title)) {
                subject.setName(subjectResource.getProperty(
                        SKOS.prefLabel).getLiteral().getLexicalForm());
            }
            subjects.add(subject);
        }
        event.setSubjects(subjects);

        List<String> tags = new ArrayList<String>();
        StmtIterator tagIter = resource.listProperties(IUGO.hasTag);
        while (tagIter.hasNext()) {
            tags.add(tagIter.nextStatement().getLiteral().getLexicalForm());
        }
        event.setTags(tags);
    }


}
