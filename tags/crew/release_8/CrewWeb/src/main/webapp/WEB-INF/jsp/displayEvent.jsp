<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <c:if test="${not empty event}">
        <meta name="caboto-annotation" content="${event.id}"/>
    </c:if>
    <title><spring:message code="proj.title"/> ${event.title}</title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
    <script type="text/javascript" src="./js/prototype.js"></script>
    <script type="text/javascript" src="./js/annotations.js"></script>
</head>
<body onload="initializeAnnotations();">
<%--@elvariable id="event" type="org.apache.wml.dom.WMLCardElementImpl"--%>
<div id="container">

<%-- banner navigation--%>
<%@ include file="includes/topNavAll.jsp" %>

<%-- the logo banner --%>
<%--<%@ include file="includes/logo.jsp" %>--%>

<%-- The main content --%>
<div id="mainBody">

<%-- The left column: navigation --%>
<%--
<div id="leftColumn">
--%>
    <%-- quick links --%>
    <%--<%@ include file="includes/quickLinks.jsp" %>--%>
<%--
</div>
--%>

<%-- The right column: RSS Feeds etc --%>
<div id="rightColumn">

    <%-- quick links --%>
    <%@ include file="includes/box-aboutCrew.jsp" %>

</div>

<!-- Middle column: main content -->
<div id="detailsColumn">

<%-- BREAD CRUMB --%>
<div id="event-bread-crumb">

    <p>
        <c:choose>
            <c:when test="${not empty resultsUrl}">
                <a href="${resultsUrl}"><spring:message code="event.crumb.events"/></a>
            </c:when>
            <c:otherwise>
                <a href="listEvents.do"><spring:message code="event.crumb.events"/></a>
            </c:otherwise>
        </c:choose>

        <strong>&gt;</strong>

        <c:if test="${not empty event.partOf}">
            <c:forEach var="item" items="${event.partOf}" varStatus="rowNo">
                <a href="displayEvent.do?eventId=<crew:uri uri='${item.id}'/>">${item.title}</a>
                <strong>&gt;</strong>
            </c:forEach>
        </c:if>

        ${event.title}
    </p>

</div>


<div id="eventDetails">

<c:choose>
<c:when test="${not empty event}">
<h3 id="event-title">${event.title}</h3>


<fieldset class="fieldSet">
<legend><strong><spring:message code="event.details"/></strong></legend>

    <%-- EVENT DATES --%>
<c:if test="${event.startDateTime != null}">
    <p id="event-dates"><strong><spring:message code="event.details.date"/></strong>
        <c:choose>
            <c:when test="${event.singleDay == true}">
                <joda:format value="${event.startDateTime}" pattern="dd MMMM yyyy, HH:mm"/> -
                <joda:format value="${event.endDateTime}" pattern=" HH:mm"/>
            </c:when>
            <c:otherwise>
                <joda:format value="${event.startDateTime}" pattern="dd MMMM yyyy, HH:mm"/> -
                <joda:format value="${event.endDateTime}" pattern="dd MMMM yyyy, HH:mm"/>
            </c:otherwise>
        </c:choose>
        <a href="./eventCalendar.do?eventId=<crew:uri uri='${event.id}'/>"><img
                src="./images/calendar.png" width="24" height="24" class="ical"
                title="<spring:message code="ical.event"/>"
                alt="<spring:message code="ical.event"/>"/></a>
    </p>
</c:if>

    <%-- EVENT DESCRIPTION --%>
<c:if test="${not empty event.description}">
    <p id="event-description">
        <strong><spring:message code="event.details.description"/></strong>
            ${event.description}</p>
</c:if>

    <%-- EVENT PLACES --%>
<c:if test="${not empty event.places}">
    <div id="event-places">
        <p><strong><spring:message code="event.details.place"/></strong></p>
        <ul>
            <c:forEach var="place" items="${event.places}">
                <li>
                    <a href="displayPlace.do?placeId=<crew:uri uri='${place.id}'/>">${place.title}</a>
                </li>
            </c:forEach>
        </ul>
    </div>
</c:if>

    <%-- EVENT SCHEDULE --%>
<c:if test="${not empty event.parts}">

    <p id="event-schedule"><strong><spring:message code="event.details.schedule"/></strong></p>

    <table>
        <c:forEach var="part" items="${event.parts}">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${event.singleDay == true}">
                            <em><joda:format value="${part.startDateTime}" pattern="HH:mm"/> -
                                <joda:format value="${part.endDateTime}" pattern="HH:mm"/></em>
                        </c:when>
                        <c:otherwise>
                            <em><joda:format value="${part.startDateTime}"
                                             pattern="dd MMMM yyyy, HH:mm"/> -
                                <joda:format value="${part.endDateTime}"
                                             pattern="dd MMMM yyyy, HH:mm"/></em>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a href="./displayEvent.do?eventId=<crew:uri uri='${part.id}'/>">${part.title}</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>

    <%-- EVENT VIDEO PRESENTATION --%>
<c:if test="${not empty event.recording}">
    <p id="event-presentation">
        <img class="videoImage" src="./images/video.png" width="22" height="22"
             alt="<spring:message code="image.alt.play"/>"/>
        <a href="./displayRecording.do?recordingId=${event.recording}&amp;eventId=<crew:uri uri='${event.id}'/>">
            <spring:message code="event.details.play"/></a>
    </p>
</c:if>

    <%-- PAPERS AND OTHER RESOURCES --%>
<c:if test="${not empty event.papers}">
    <div id="event-papers"><strong><spring:message code="event.papers"/></strong>
        <ul>
            <c:forEach var="paper" items="${event.papers}">
                <li><em>${paper.title}</em>
                    <c:if test="${not empty paper.authors}">
                        <spring:message code="event.papers.by"/>
                        <c:forEach var="author" items="${paper.authors}" varStatus="count">
                            ${author.name}<c:choose><c:when
                                test="${not count.last}">;</c:when><c:otherwise>.</c:otherwise></c:choose>
                        </c:forEach>
                    </c:if>
                    <c:if test="${paper.retrievable}">[ <a href="${paper.id}">Download</a> ]
                        <img class="externalLink" src="./images/web.png" alt="External Link"
                             width="16" height="16"/></c:if>
                </li>
            </c:forEach>
        </ul>
    </div>
</c:if>


    <%-- EVENT LOCATIONS --%>
<c:if test="${not empty event.locations}">
    <p id="event-locations">
        <strong><spring:message code="event.details.locations"/></strong>
        <c:forEach var="location" items="${event.locations}" varStatus="rowNo">
            <c:if test="${location.name != 'Locations'}">${location.name};</c:if>
        </c:forEach>
    </p>
</c:if>

    <%-- EVENT SUBJECTS --%>
<c:if test="${not empty event.subjects}">
    <p id="event-subjects">
        <strong><spring:message code="event.details.subjects"/></strong>
        <c:forEach var="subject" items="${event.subjects}" varStatus="rowNo">
            <c:if test="${subject.name != 'Disciplines'}">${subject.name};</c:if>
        </c:forEach>
    </p>
</c:if>

    <%-- EVENT TAGS--%>
<c:if test="${not empty event.tags}">
    <p id="event-tags">
        <strong><spring:message code="event.details.tags"/></strong>
        <c:forEach var="tag" items="${event.tags}" varStatus="rowNo">
            ${tag}<c:if test="${not rowNo.last}">;</c:if>
        </c:forEach>
    </p>
</c:if>

    <%-- EVENT EXTERNAL LINKS --%>
<c:if test="${not empty event.programme || not empty event.proceedings}">

    <p id="event-external-links"><strong><spring:message code="event.details.external"/></strong>

        <c:if test="${not empty event.programme}">
            <a href="${event.programme}"><spring:message code="event.details.programme"/></a>
            <img class="externalLink" src="./images/web.png" alt="External Link" width="16"
                 height="16"/>
        </c:if>

        <c:if test="${not empty event.proceedings}">
            <a href="${event.proceedings}"><spring:message code="event.details.proceedings"/></a>
            <img class="externalLink" src="./images/web.png" alt="External Link" width="16"
                 height="16"/>
        </c:if>

    </p>

</c:if>


    <%-- ROLES --%>
<c:if test="${not empty event.roles}">

    <div id="event-roles"><strong><spring:message code="event.roles"/></strong>
        <ul>
            <c:forEach var="role" items="${event.roles}">
                <li>${role.name}:&nbsp;<a
                        href="displayPerson.do?personId=<crew:uri uri='${role.heldBy.id}'/>">${role.heldBy.name}</a>
                </li>
            </c:forEach>
        </ul>
    </div>

</c:if>


</fieldset>

<%
    Cookie uid = new Cookie("uid", null);
    Cookie admin = new Cookie("admin", null);

    if (request.getUserPrincipal() != null) {
        uid.setValue(request.getUserPrincipal().getName());
        if (request.isUserInRole("ADMIN")) {
            admin.setValue("true");
        }
    }

    response.addCookie(uid);
    response.addCookie(admin);
%>
<div class="annotations">

    <fieldset class="fieldSet">
        <legend>Annotations</legend>
        <div id="annotations-results"><p>Sorry, you need a JavaScript enabled browser.</p></div>


        <p>Add your own annotation...</p>

        <div id="annotation-messages"></div>

            <%-- show form if they are logged in --%>
        <security:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
            <form id="annotation-comment-form"
                  action="javascript:processForm('<%=request.getUserPrincipal().getName()%>')"
                  method="post">
                <p>
                    <label><strong>Title:</strong></label><br/>
                    <input id="annotation-title" type="text" name="title" size="50"/><br/>
                    <label><strong>Body:</strong></label><br/>
                    <textarea id="annotation-body" rows="5" cols="50"
                              name="description"></textarea><br/>
                    <input type="radio" name="privacy" value="public" checked="checked"> Public
                    <input type="radio" name="privacy" value="private"> Private
                    <input type="hidden" name="type" value="SimpleComment"/>
                    <input type="hidden" name="annotates" value="${event.id}"/><br/>
                    <input id="annotation-submit" type="submit" name="submit" value="Submit"
                           disabled="disabled"/>
                </p>
            </form>
        </security:authorize>

            <%-- message if not logged in --%>
        <security:authorize ifNotGranted="ROLE_USER,ROLE_ADMIN">
            <p>You need to be <a href="./secured/displayProfile.do">logged in</a> to add an annotation.
                You can <a href="./registration.do">register</a> if you do not have an account.<br/>
                <a href="./forgottenPassword.do">Forgotten</a> your password?</p>

        </security:authorize>

    </fieldset>
</div>


</c:when>
<c:otherwise>
    <p><spring:message code="event.empty"/></p>
</c:otherwise>
</c:choose>


</div>


</div>
</div>


<%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>