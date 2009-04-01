<%@ include file="includes/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><spring:message code="proj.title"/> ${person.name}</title>
    <style type="text/css" media="screen">@import "./style.css";</style>
</head>
<body>

<div id="container">

<%-- banner navigation--%>
<%@ include file="includes/topNav.jsp" %>

<%-- the logo banner --%>
<%@ include file="includes/logo.jsp" %>

<%-- The main content --%>
<div id="mainBody">


<%-- The left column: navigation --%>
<div id="leftColumn">

    <%-- quick links --%>
    <%@ include file="includes/quickLinks.jsp" %>

</div>

<%-- The right column: RSS Feeds etc --%>
<div id="rightColumn">

    <%-- quick links --%>
    <%@ include file="includes/box-aboutCrew.jsp" %>

    <%-- upcoming events --%>
    <%--
    <%@ include file="includes/box-upcomingEvents.jsp" %>
    --%>

    <%-- recently added events --%>
    <%--
    <%@ include file="includes/box-recentlyAdded.jsp" %>
    --%>

</div>

<!-- Middle column: main content -->
<div id="middleColumn">

    <%-- Temp solution --%>
    <div id="person-nav">
        <p><a href="javascript:history.go(-1)"><spring:message code="nav.back"/></a></p>
    </div>

    <%--
    <c:if test="${browseHistory[1] != null}">
        <div id="person-nav">
            <p><a href="${browseHistory[1].path}"><spring:message code="nav.back" /></a></p>
        </div>
    </c:if>
    --%>

    <%-- Person Details --%>
    <div id="personDetails">

        <c:choose>
            <c:when test="${not empty person}">
                <h3 id="event-title">${person.title} ${person.name}</h3>

                <c:if test="${not empty person.homepage || not empty person.workplaceHomepage || not empty person.flickrHomepage}">

                    <fieldset>

                        <legend><strong><spring:message code="person.details"/></strong></legend>

                        <c:if test="${not empty person.homepage}">
                            <p><strong><spring:message code="person.details.homepage"/></strong>
                                <a href="${person.homepage}">${person.homepage}</a></p>
                        </c:if>

                        <c:if test="${not empty person.workplaceHomepage}">
                            <p><strong><spring:message code="person.details.workplaceHome"/></strong>
                                <a href="${person.workplaceHomepage}">${person.workplaceHomepage}</a>
                            </p>
                        </c:if>

                        <c:if test="${not empty person.flickrHomepage}">
                            <p><strong><spring:message code="person.details.flickrHomepage"/></strong>
                                <a href="${person.flickrHomepage}">${person.flickrHomepage}</a></p>
                        </c:if>

                    </fieldset>
                </c:if>
            </c:when>
            <c:otherwise><spring:message code="person.empty"/></c:otherwise>
        </c:choose>
    </div>

    <%-- Person roles --%>
    <c:if test="${not empty person.roles}">
        <div id="personRoles">
            <fieldset>
                <legend><strong><spring:message code="person.roles"/></strong></legend>
                <p><spring:message code="person.roles.assoc" arguments="${person.name}"/></p>

                <ul>
                    <c:forEach var="role" items="${person.roles}">
                        <li>${role.name}&nbsp;-&nbsp;
                            <c:if test="${not empty role.roleAt}">
                                <c:forEach var="event" items="${role.roleAt}">
                                    <a href="displayEvent.do?eventId=<crew:uri uri='${event.id}'/>">${event.title}</a>
                                    <c:if test="${not empty event.partOf}">
                                        <spring:message code="person.roles.eventPartOf"/>
                                        <a href="displayEvent.do?eventId=<crew:uri uri='${event.partOf[0].id}'/>">${event.partOf[0].title}</a>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </li>
                    </c:forEach>
                </ul>
            </fieldset>
        </div>
    </c:if>
</div>
</div>


<%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>