<?xml version="1.0" encoding="utf-8"?>
<%@ page contentType="application/rdf+xml;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<rdf:RDF xmlns:pos="http://www.w3.org/2003/01/geo/wgs84_pos#"
         xmlns:iugo="http://www.ilrt.bristol.ac.uk/iugo#"
         xmlns:digest="http://nwalsh.com/xslt/ext/com.nwalsh.xslt.Digest"
         xmlns:skos="http://www.w3.org/2004/02/skos/core#"
         xmlns:crew="http://www.crew-vre.net/ontology#"
         xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
         xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
         xmlns:dc="http://purl.org/dc/elements/1.1/"
         xmlns:eswc="http://www.eswc2006.org/technologies/ontology#"
         xmlns:ge="http://www.ilrt.bris.ac.uk/GreeningEvents/ontology#">

    <c:forEach var="event" items="${events}" varStatus="status">
        <rdf:Description rdf:about="http://www.jiscdigitalmedia.ac.uk/events/${event.eventId}">
            <rdf:type rdf:resource="http://www.ilrt.bristol.ac.uk/iugo#MainEvent"/>
            <rdf:type rdf:resource="http://www.eswc2006.org/technologies/ontology#ConferenceEvent"/>
            <dc:title>${event.title}</dc:title>
            <crew:hasStartDate rdf:datatype="http://www.w3.org/2001/XMLSchema#date">${event.startDate}</crew:hasStartDate>
            <crew:hasEndDate rdf:datatype="http://www.w3.org/2001/XMLSchema#date">${event.endDate}</crew:hasEndDate>
            <dc:description>
                <c:out value="${event.description}" escapeXml="true"/>
            </dc:description>
            <eswc:hasLocation>
             <rdf:Description rdf:about="${event.locationHash}">
                <dc:title>${event.location}</dc:title>
                <pos:lat>${event.latitude}</pos:lat>
                <pos:long>${event.longitude}</pos:long>
                <dc:description>
                    <c:out value="${event.locationDescription}" escapeXml="true"/>
                </dc:description>
                <ge:locationUrl>${event.locationUrl}</ge:locationUrl>
                <ge:locationThumbSrc>${event.locationThumbUrl}</ge:locationThumbSrc>
                <ge:locationImagesSrc>${event.locationImagesUrl}</ge:locationImagesSrc>
             </rdf:Description>
            </eswc:hasLocation>
            <c:forEach var="startPoint" items="${event.startPoints}" varStatus="status">
                 <ge:startPoint>
                     <rdf:Description rdf:about="${startPoint.startPointId}">
                        <dc:title>${startPoint.title}</dc:title>
                        <pos:lat>${startPoint.latitude}</pos:lat>
                        <pos:long>${startPoint.longitude}</pos:long>
                        <c:forEach var="waypoint" items="${startPoint.waypoints}" varStatus="status">
                            <ge:waypoint>
                                <rdf:Description rdf:about="${waypoint.waypointId}">
                                    <pos:lat>${waypoint.latitude}</pos:lat>
                                    <pos:long>${waypoint.longitude}</pos:long>
                                </rdf:Description>
                            </ge:waypoint>
                        </c:forEach>
                     </rdf:Description>
                 </ge:startPoint>
            </c:forEach>
            <c:forEach var="kml" items="${event.kmlObjects}" varStatus="status">
                 <ge:kmlObject>
                     <rdf:Description rdf:about="${kml.kmlId}">
                        <dc:title>${kml.title}</dc:title>
                        <dc:type>${kml.type}</dc:type>
                        <ge:kml><c:out value="${kml.xml}" escapeXml="true"/></ge:kml>
                     </rdf:Description>
                 </ge:kmlObject>
            </c:forEach>
            <eswc:hasProgramme rdf:resource="${event.eventUrl}"/>
        </rdf:Description>
    </c:forEach>
</rdf:RDF>

