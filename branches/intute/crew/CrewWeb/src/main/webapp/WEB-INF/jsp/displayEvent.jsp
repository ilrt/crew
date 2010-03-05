<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="resource-type" content="document" />
<meta name="distribution" content="GLOBAL" />
<meta name="description" content="Intute - Conferences and events" />
<meta name="copyright" content="Intute 2010" />
<meta name="keywords" content="internet; resource; catalogue" />
<meta name="author" content="intute" />
<meta http-equiv="content-language" content="en" />

    <c:if test="${not empty event}">
        <meta name="caboto-annotation" content="${event.id}"/>
    </c:if>
<title><spring:message code="proj.title"/> ${event.title}</title>
<style type="text/css" media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
<link rel="stylesheet" type="text/css" media="screen" href="http://www.intute.ac.uk/reset.css" />
<link rel="stylesheet" type="text/css" media="screen" href="http://www.intute.ac.uk/intute.css" />
<link rel="stylesheet" type="text/css" media="print" href="http://www.intute.ac.uk/intute-print.css" />

   <%--[if IE]>
        <link rel="stylesheet" href="http://www.intute.ac.uk/intute-ie.css" type="text/css">
        <![endif]--%>

<%--myintute code ############################################################## --%>
<script language="javascript" src="/myintute/scripts/button.js"
type="text/javascript"></script>
<script language="javascript" src="/myintute/scripts/functions.js"
type="text/javascript"></script>
<script language="javascript" src="/myintute/scripts/init.js"
type="text/javascript"></script>
<script language="javascript" src="/myintute/scripts/dosearch.js"
type="text/javascript"></script>
<script language="javascript" src="/myintute/scripts/shiv.js"
type="text/javascript"></script>
<script src="/myintute/scripts/prototype.js" 
type="text/javascript"></script>
<script src="/myintute/scripts/scriptaculous.js"
type="text/javascript"></script>
<%-- ########################################################################### --%>

<script src="http://www.intute.ac.uk/scripts/jquery-1.3.2.min.js" type="text/javascript" charset="utf-8"></script>
<script src="http://www.intute.ac.uk/scripts/jquery.hoverIntent.minified.js" type="text/javascript" charset="utf-8"></script>
<script src="http://www.intute.ac.uk/scripts/mega-dropdown.js" type="text/javascript" charset="utf-8"></script>
</head>
<%-- <body onload="initializeAnnotations();setCookie();"> --%>
<body onload="setCookie();">

<%@ include file="includes/menu-services.jsp" %>

<%--CONTENT CONTAINER--%>
<div class="content-background">
<div class="content-container center">

<%--breadcrumbs--%>
<p class="breadcrumbs smalltext">
<a href="http://www.intute.ac.uk/">Home</a>
&rsaquo; <a href="http://www.intute.ac.uk/services.html">All services</a>
&rsaquo;
   <c:choose>
       <c:when test="${not empty resultsUrl}">
           <a href="${resultsUrl}"><spring:message code="event.crumb.events"/></a>
       </c:when>
       <c:otherwise>
           <a href="listEvents.do"><spring:message code="event.crumb.events"/></a>
       </c:otherwise>
   </c:choose>
&rsaquo;
   <c:if test="${not empty event.partOf}">
       <c:forEach var="item" items="${event.partOf}" varStatus="rowNo">
           <a href="displayEvent.do?eventId=<crew:uri uri='${item.id}'/>">${item.title}</a>
           <strong>&gt;</strong>
       </c:forEach>
   </c:if>
${event.title}
</p>

<h1><spring:message code="event.details"/></h1>
<div class="content" id="content-full-width">

<c:choose>
<c:when test="${not empty event}">

<%-- <legend><strong><spring:message code="event.details"/></strong></legend> --%>
<table cellpadding="0" cellspacing="0" border="0" class="tablestyle">

    <%-- EVENT TITLE --%>
	<tr valign="top">
	<th><spring:message code="event.details.title"/></th>
	<td>${event.title}</td>
	</tr>

    <%-- EVENT DATES --%>
<c:if test="${event.startDate != null}">
        <tr valign="top">
        <th><spring:message code="event.details.date"/></th>
	<td>
        <c:choose>
            <c:when test="${event.singleDay == true}">
	        <joda:format value="${event.startDate}" pattern="dd MMMM yyyy"/>
            </c:when>
            <c:otherwise>
                <joda:format value="${event.startDate}" pattern="dd MMMM yyyy"/> -
                <joda:format value="${event.endDate}" pattern="dd MMMM yyyy"/>
            </c:otherwise>
        </c:choose>
        <a href="./eventCalendar.do?eventId=<crew:uri uri='${event.id}'/>"><img
                src="./images/calendar.png" width="24" height="24" class="ical"
                title="<spring:message code="ical.event"/>"
                alt="<spring:message code="ical.event"/>"/></a>
	</td>
	</tr>
</c:if>

    <%-- EVENT DESCRIPTION --%>
<c:if test="${not empty event.description}">
        <tr valign="top">
        <th><spring:message code="event.details.description"/></th>
        <td>${event.description}</td>
        </tr>
</c:if>

    <%-- EVENT PLACES --%>
<c:if test="${not empty event.places}">
        <tr valign="top">
        <th><spring:message code="event.details.place"/></th>
        <td>
            <c:forEach var="place" items="${event.places}">
                 ${place.title}
            </c:forEach>
<%-- <c:if test="${place.latitude != null}"> This is not possible as events.domain.PlacePart only provides title and id --%>
        <%--            <a href="displayPlace.do?placeId=<crew:uri uri='${place.id}'/>">(see map)</a> --%>
<%-- </c:if> --%>
	</td>
	</tr>
</c:if>


    <%-- EVENT LOCATIONS --%>
<c:if test="${not empty event.locations}">
        <tr valign="top">
        <th><spring:message code="event.details.locations"/></th>
        <td>
        <c:forEach var="tag" items="${event.tags}" varStatus="rowNo">
            ${tag}<c:if test="${not rowNo.last}">;</c:if>
        </c:forEach>
<%-- Using tags to show location to avoid random ordering of location hierachy - Intute exporting locations as tags
        <c:forEach var="location" items="${event.locations}" varStatus="rowNo">
            <c:if test="${location.name != 'Locations'}">${location.name};</c:if>
        </c:forEach>
--%>
	</td>
	</tr>
</c:if>

    <%-- EVENT SUBJECTS --%>
<c:if test="${not empty event.subjects}">
        <tr valign="top">
        <th><spring:message code="event.details.subjects"/></th>
        <td>
        <c:forEach var="subject" items="${event.subjects}" varStatus="rowNo">
            <c:if test="${subject.name != 'Disciplines'}">${subject.name};</c:if>
        </c:forEach>
        </td>
        </tr>
</c:if>

    <%-- EVENT TAGS--%>
<%--
<c:if test="${not empty event.tags}">
    <p id="event-tags">
        <strong><spring:message code="event.details.tags"/></strong>
        <c:forEach var="tag" items="${event.tags}" varStatus="rowNo">
            ${tag}<c:if test="${not rowNo.last}">;</c:if>
        </c:forEach>
    </p>
</c:if>
--%>

    <%-- EVENT EXTERNAL LINKS --%>
<c:if test="${not empty event.programme || not empty event.proceedings}">
        <tr valign="top">
        <th><spring:message code="event.details.external"/></th>
        <td>
        <c:if test="${not empty event.programme}">
            <a href="${event.programme}" target="_blank"><spring:message code="event.details.programme"/></a>
            <img class="externalLink" src="./images/web.png" alt="External Link" width="16"
                 height="16"/>
        </c:if>

        <c:if test="${not empty event.proceedings}">
            <a href="${event.proceedings}"><spring:message code="event.details.proceedings"/></a>
            <img class="externalLink" src="./images/web.png" alt="External Link" width="16"
                 height="16"/>
        </c:if>
        </td>
        </tr>
</c:if>


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

<%-- Disabling annotations 
<div class="annotations">

    <fieldset class="fieldSet">
        <legend>Annotations</legend>
        <div id="annotations-results"><p>Sorry, you need a JavaScript enabled browser.</p></div>


        <p>Add your own annotation...<br />
	- use wiki style notation to add links, e.g. [Go to Intute|http://www.intute.ac.uk]
        or [http://www.intute.ac.uk]</p>

        <div id="annotation-messages"></div>
--%>
            <%-- show form if they are logged in --%>
<%--
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
--%>
            <%-- message if not logged in --%>
<%--
        <security:authorize ifNotGranted="ROLE_USER,ROLE_ADMIN">
            <p>You need to be <a href="./secured/displayProfile.do">logged in</a> to add an annotation.
                You can <a href="./registration.do">register</a> if you do not have an account.<br/>
                <a href="./forgottenPassword.do">Forgotten</a> your password?</p>

        </security:authorize>

    </fieldset>
</div>
--%>
</table>

</c:when>
<c:otherwise>
    <p><spring:message code="event.empty"/></p>
</c:otherwise>
</c:choose>


<%-- end content --%>
</div>

<%--important div to prevent IE guillotine bug--%>
<div style="clear: both"></div>

<%-- end content container --%>
</div>

<%-- end content background --%>
</div>


<%-- FOOTER --%>
<%@ include file="includes/footer.jsp" %>
</body>
</html>

