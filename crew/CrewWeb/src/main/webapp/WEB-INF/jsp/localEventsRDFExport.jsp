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
                ${event.description}
            </dc:description>
            <eswc:hasLocation>
             <rdf:Description rdf:about="${event.locationHash}">
                <dc:title>${event.location}</dc:title>
                <pos:lat>${event.latitude}</pos:lat>
                <pos:long>${event.longitude}</pos:long>
             </rdf:Description>
            </eswc:hasLocation>
        <c:if test="${not empty event.startPointLat1}">
             <ge:startPoint>
                 <rdf:Description rdf:about="${event.startPointHash1}">
                    <dc:title>${event.startPoint1}</dc:title>
                    <pos:lat>${event.startPointLat1}</pos:lat>
                    <pos:long>${event.startPointLong1}</pos:long>
                 </rdf:Description>
             </ge:startPoint>
        </c:if>
        <c:if test="${not empty event.startPointLat2}">
             <ge:startPoint>
                 <rdf:Description rdf:about="${event.startPointHash2}">
                    <dc:title>${event.startPoint2}</dc:title>
                    <pos:lat>${event.startPointLat2}</pos:lat>
                    <pos:long>${event.startPointLong2}</pos:long>
                 </rdf:Description>
             </ge:startPoint>
        </c:if>
        <c:if test="${not empty event.startPointLat3}">
             <ge:startPoint>
                 <rdf:Description rdf:about="${event.startPointHash3}">
                    <dc:title>${event.startPoint3}</dc:title>
                    <pos:lat>${event.startPointLat3}</pos:lat>
                    <pos:long>${event.startPointLong3}</pos:long>
                 </rdf:Description>
             </ge:startPoint>
        </c:if>
            <eswc:hasProgramme rdf:resource="${event.eventUrl}"/>
        </rdf:Description>
    </c:forEach>
</rdf:RDF>

