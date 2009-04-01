<%@ page session="true" contentType="text/plain;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//Universities of Bristol and Manchester//NONSGML CREW//EN
BEGIN:VEVENT
UID:${event.id}
<c:if test="${not empty event}">
<fmt:parseDate var="sdate" pattern="yyyy-MM-dd'T'HH:mm:ss" value="${event.startDate}"/>
<fmt:parseDate var="edate" pattern="yyyy-MM-dd'T'HH:mm:ss" value="${event.endDate}"/>
<c:if test="${not empty event.locations}">
LOCATION:<c:forEach items="${event.places}" var="place" varStatus="status">${place.title}<c:if test="${not status.last}">,</c:if></c:forEach>
</c:if>
DTSTART:<fmt:formatDate pattern="yyyyMMdd'T'HHmmss" value="${sdate}"/>
DTEND:<fmt:formatDate pattern="yyyyMMdd'T'HHmmss" value="${edate}"/>
SUMMARY:${event.title}
DESCRIPTION:${event.description}
</c:if>
END:VEVENT
END:VCALENDAR