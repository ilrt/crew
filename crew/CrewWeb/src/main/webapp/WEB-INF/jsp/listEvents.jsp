<%-- dtd and xml declaration --%>
<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="false" contentType="text/html;charset=UTF-8" language="java" %>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="resource-type" content="document" />
<meta name="distribution" content="GLOBAL" />
<meta name="description" content="Intute - Conferences and events" />
<meta name="copyright" content="Intute 2009" />
<meta name="keywords" content="internet; resource; catalogue" />
<meta name="author" content="intute" />
<meta http-equiv="content-language" content="en" />
<title><spring:message code="event.list.title"/></title>


    <c:if test="${not empty feedList}">
        <c:forEach var="feed" items="${feedList}">
            <link rel="alternate" href="${feed.feedUrl}" type="${feed.contentType}"
                  title="${feed.title}"/>
        </c:forEach>
    </c:if>

<style type="text/css" media="screen">@import "./style.css";</style>
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
<body onload="setCookie()">

<%@ include file="includes/menu-services.jsp" %>
   		
<%--CONTENT CONTAINER--%>
<div class="content-background">
<div class="content-container center">
           
<%--breadcrumbs--%>
<p class="breadcrumbs smalltext">
<a href="http://www.intute.ac.uk/">Home</a> 
&rsaquo; <a href="http://www.intute.ac.uk/services.html">All services</a> 
&rsaquo; <spring:message code="event.page.title"/>
&rsaquo; <a href="listEvents.do">All events</a>
</p>

<h1><spring:message code="event.page.title"/></h1>
<div class="content" id="content-full-width">
<p>
The Conferences and events database currently only covers the Social Sciences and Arts and Humanities subjects. 
If you want to find details of a conference or event in these areas, search here. If you have a conference or 
event you would like publicised please send the details through our helpdesk.</p>

<p>Log in to your MyIntute account if you have any conferences or events you wish to add to the database.</p> 

<%-- Column container --%>
<div class="col-3366percent-container">

<%--left hand column--%>
<div class="col-33percent-left">

<div class="services-box" id="services-box-conferences">
    <%-- the facets ---%>
    <div class="bl">
        <div class="br">
            <div class="tl">
                <div class="tr">
                    <div class="box" id="facetNavigation">
                        <h4 class="box-header"><spring:message code="facet.title"/></h4>
                        <c:forEach var="facet" items="${facets}">
                            <crew:facet facet="${facet}" showEmpty="false" url="${url}"
                                        parameters="${parameters}"/>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="services-box" id="services-box-forthcoming-events-rss">
    <%-- upcoming events --%>
    <%@ include file="includes/box-upcomingEvents.jsp" %>
</div>
    <%-- recently added events --%>
<%-- No last mod dates in Intute data --%>
<%--
<div class="services-box" id="services-box-recent-events-rss">
    <%@ include file="includes/box-recentlyAdded.jsp" %> 
</div>
--%>

<%-- end left hand column --%>
</div>

<%--right hand column--%>
<div class="col-66percent-right">

<div class="services-box" id="events-results">

    <%-- display info about the number of results --%>
    <div class="resultDetails">
        <p>
            <c:choose>
                <c:when test="${total == 1}">
                    <spring:message code="event.total.one"/>
                </c:when>
                <c:otherwise>
                    <spring:message code="events.total" arguments="${total}"/>
                </c:otherwise>
            </c:choose>


            <%-- show navigation stuff? --%>
            <c:choose>
            <c:when test="${total > 0}">
                <%-- navigation message --%>
            <%@ include file="includes/navMessage.jsp" %>
        </p>
            <%-- provide navigation if there is more than one page --%>
        <c:if test="${nav.totalPages > 1}">
            <crew:nav navHelper="${nav}" params="${parameters}" className="pagination"/>
        </c:if>
        </c:when>
        <c:otherwise>
            </p>
        </c:otherwise>
        </c:choose>

    </div>
    <%-- the results --%>
    <c:if test="${not empty listEvents}">
        <c:forEach var="event" items="${listEvents}" varStatus="rowNum">
            <c:choose>
                <c:when test="${rowNum.count % 2 == 0}">
                    <div class="rowEven">
                </c:when>
                <c:otherwise>
                    <div class="rowOdd">
                 </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${not empty event.id}">
                    <h5 class="resultHeader"><a
                            href="./displayEvent.do?eventId=<crew:uri uri='${event.id}'/>">${event.title}</a>
                    </h5>

                    <p class="resultDate">
                        <c:choose>
                            <c:when test="${event.singleDay == true}">
                                <joda:format value="${event.startDate}"
                                             pattern="dd MMMM yyyy"/>
                            </c:when>
                            <c:otherwise>
                                <joda:format value="${event.startDate}"
                                             pattern="dd MMMM yyyy"/> -
                                <joda:format value="${event.endDateTime}"
                                             pattern="dd MMMM yyyy"/>
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <c:if test="${not empty event.locations}">
                        <p class="resultsLocations">
                            <c:forEach var="location" items="${event.locations}" varStatus="rowNo">
                                <c:if test="${location.name != 'Locations'}">${location.name};</c:if>
                            </c:forEach>
                        </p>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <h5 class="resultHeader">Sorry, you are not authorized to view the event details.</h5>
                </c:otherwise>
            </c:choose>
        </div>
        </c:forEach>
    </c:if>

        <%-- provide navigation if there is more than one page --%>
        <c:if test="${nav.totalPages > 1}">
            <div class="pagination-bottom">
                <crew:nav navHelper="${nav}" params="${parameters}" className="pagination"/>
            </div>
        </c:if>
<%-- end services box --%>
</div>

<%--end of right col--%>
</div>

<%-- end of column container --%>
</div>

<%--end of content--%>
</div>

<%--MyIntute--%>
<span id="load"></span>
<div id="container" class="myintute-container"><noscript>
<p><img src="/myintute/mockup_files/off.png" alt="tick" /><br/><b><font
color="red">MyInute functionality requires the use of Javascript and
cookies.</font></b>
<br/>To use MyIntute please enable javascript and cookies in your
browser.</p>

</noscript>
</div>

<%--end of Myintute--%>
<%--end of content-container--%>

<%--important div to prevent IE guillotine bug--%>
<div style="clear: both"></div>

  </div>
<%--end of content background--%>
</div>
   
   <%--FOOTER--%>

<%@ include file="includes/footer.jsp" %>
</body>
</html>
