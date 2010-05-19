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
    <link rel='stylesheet' type='text/css' media='screen'
          href='http://www.jiscdigitalmedia.ac.uk/?css=jdm/master.v.1267190280' />
</head>
<body class="bodybg2" onload="initializeAnnotations();">
<div id="topbg"></div>

<div>
<!--start of wrapper-->
    <div class="wrapper">

        <!--start of top nav-->
        <div class="mainNav">
                        <a href="http://www.jiscdigitalmedia.ac.uk/"><img src="http://www.jiscdigitalmedia.ac.uk/images/site/logo.gif" border="0" width="279" height="55" alt="JISC Digital Media" id="logo" /></a>
                        <ul>
                        <li class="diamond" id="about"><a href="http://www.jiscdigitalmedia.ac.uk/about/">About</a></li>
                        <li class="diamond" id="helpdesk"><a href="http://www.jiscdigitalmedia.ac.uk/helpdesk/">Helpdesk</a></li>

                        <li class="diamond" id="news"><a href="http://www.jiscdigitalmedia.ac.uk/news/">News</a></li>
                        <li class="diamond" id="case"><a href="http://www.jiscdigitalmedia.ac.uk/tags/category/case-studies/">Case Studies</a></li>
                        <li><a href="http://www.jiscdigitalmedia.ac.uk/contact/" id="contact">Contact</a></li>
                        </ul>
                        <!--start of search form-->
<form id='searchForm' method="post" action="http://www.jiscdigitalmedia.ac.uk/"  >
<div class='hiddenFields'>
<input type="hidden" name="ACT" value="19" />
<input type="hidden" name="XID" value="" />

<input type="hidden" name="RP" value="search/results" />
<input type="hidden" name="NRP" value="search&amp;#47;no-results" />
<input type="hidden" name="RES" value="90" />
<input type="hidden" name="status" value="open" />
<input type="hidden" name="weblog" value="not archived|default_site|external|seminar-test|tips" />
<input type="hidden" name="search_in" value="entries" />
<input type="hidden" name="where" value="all" />
<input type="hidden" name="site_id" value="1" />
</div>


<div>
<input type="text" name="keywords" id="search" value=""  /> <input type="submit" name="searchBtn" id="searchBtn" value="Search" title="Search" />
</div>
</form>
			<!--end of search form-->

		</div>
			<!--end of top nav-->
		<div class="clearDiv"></div>
<!--start of main content-->
                <div class="contentWrap contentWrap2">
                        <div class="intro intro2">
                            <img src="http://www.jiscdigitalmedia.ac.uk/images/site/hometopleft2.gif" alt="" width="525" height="169" id="hometop2" />
                            <div class="introBox introBox2">
                                <p>Free help and advice to the UK Further and Higher Education community</p>

                                <a href="http://www.jiscdigitalmedia.ac.uk/helpdesk/">Helpdesk</a>
                            </div>
                            <img src="http://www.jiscdigitalmedia.ac.uk/images/site/hometopbottom.gif" alt="" width="445" height="23" id="hometopbtm" />
                        </div>
                </div>

                <div class="clearDiv"></div>

<div class="content content2">
        <!--start of Left content-->

        <div id="leftCol">


    <%-- quick links --%>
    <%-- <%@ include file="includes/quickLinks.jsp" %> --%>
    <%-- the browser links --%>
    <%@ include file="includes/headerBrowse.jsp" %>

    <%-- the facets ---%>
    <%--
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
    --%>


</div>
<!--end of Left content-->

<!--start of Middle content-->
<div id="midCol" class="genericContent traininglist">
<%-- BREAD CRUMB --%>
<div id="breadCrumb">
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
</div>

        <div><h1>${event.title}</h1></div>

<div class="contentBlock">

<c:choose>
<c:when test="${not empty event}">

    <%-- EVENT DATES --%>
<c:if test="${event.startDate != null}">
    <h2><spring:message code="event.details.date"/></h2>
    <p>
        <c:choose>
            <c:when test="${event.singleDay == true}">
                <joda:format value="${event.startDate}" pattern="dd MMMM yyyy, HH:mm"/> -
                <joda:format value="${event.endDate}" pattern=" HH:mm"/>
            </c:when>
            <c:otherwise>
                <joda:format value="${event.startDate}" pattern="dd MMMM yyyy, HH:mm"/> -
                <joda:format value="${event.endDate}" pattern="dd MMMM yyyy, HH:mm"/>
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
    <h2><spring:message code="event.details.description"/></h2>
    <p>${event.description}</p>
</c:if>

    <%-- EVENT PLACES --%>
<c:if test="${not empty event.places}">
        <h2><spring:message code="event.details.place"/></h2>
        <ul>
            <c:forEach var="place" items="${event.places}">
                <li>
                    <a href="displayPlace.do?placeId=<crew:uri uri='${place.id}'/>&amp;eventId=<crew:uri uri='${event.id}'/>&amp;eventTitle=${event.title}">${place.title}</a>
                </li>
            </c:forEach>
        </ul>
</c:if>

    <%-- EVENT SCHEDULE --%>
<c:if test="${not empty event.parts}">

    <h2><spring:message code="event.details.schedule"/></h2>

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
<c:if test="${not empty recordings}">
    <c:forEach items="${recordings}" var="recording">
        <p id="event-presentation">
            <img class="videoImage" src="./images/video.png" width="22" height="22"
                 alt="<spring:message code="image.alt.play"/>"/>
            <a href="./displayRecording.do?recordingId=${recording.id}&amp;eventId=<crew:uri uri='${event.id}'/>">
                <spring:message code="event.details.play"/></a>
        </p>
    </c:forEach>
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

        <h2><spring:message code="event.details.locations"/></h2>
        <ul>
        <c:forEach var="location" items="${event.locations}" varStatus="rowNo">
            <li><c:if test="${location.name != 'Locations'}">${location.name};</c:if></li>
        </c:forEach>
        </ul>

</c:if>

    <%-- EVENT SUBJECTS --%>
<c:if test="${not empty event.subjects}">
        <h2><spring:message code="event.details.subjects"/></h2>
        <ul>
        <c:forEach var="subject" items="${event.subjects}" varStatus="rowNo">
            <li><c:if test="${subject.name != 'Disciplines'}">${subject.name};</c:if></li>
        </c:forEach>
        </ul>
</c:if>

    <%-- EVENT TAGS--%>
<c:if test="${not empty event.tags}">
        <h2><spring:message code="event.details.tags"/></h2>
        <ul>
        <c:forEach var="tag" items="${event.tags}" varStatus="rowNo">
            <li>${tag}<c:if test="${not rowNo.last}">;</c:if></li>
        </c:forEach>
        </ul>
</c:if>

    <%-- EVENT EXTERNAL LINKS --%>
<c:if test="${not empty event.programme || not empty event.proceedings}">

    <h2><spring:message code="event.details.external"/></h2>
    <ul>

        <c:if test="${not empty event.programme}">
            <li><a href="${event.programme}"><spring:message code="event.details.programme"/></a></li>
        </c:if>

        <c:if test="${not empty event.proceedings}">
            <li><a href="${event.proceedings}"><spring:message code="event.details.proceedings"/></a></li>
        </c:if>
    </ul>
</c:if>


    <%-- ROLES --%>
<c:if test="${not empty event.roles}">

    <h2><spring:message code="event.roles"/></h2>
        <ul>
            <c:forEach var="role" items="${event.roles}">
                <li>${role.name}:&nbsp;<a
                        href="displayPerson.do?personId=<crew:uri uri='${role.heldBy.id}'/>">${role.heldBy.name}</a>
                </li>
            </c:forEach>
        </ul>
    </div>

</c:if>

<!-- end contentBlock -->
    </div>


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


<div class="contentBlock">
<%--
        <div id="annotations-title">Comments</div>
        <div id="annotations-results"><p>Sorry, you need a JavaScript enabled browser.</p></div>

        <div id="annotation-note">Add your own annotation...<br />
        - use wiki style notation to add links, <br />
        e.g. [Go to JISC Digital Media|http://www.jiscdigitalmedia.ac.uk]
        or [http://www.jiscdigitalmedia.ac.uk]</div>

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

--%>

</c:when>
<c:otherwise>
    <p><spring:message code="event.empty"/></p>
</c:otherwise>
</c:choose>


<!-- end contentBlock -->
    </div>

</div>
<!--End of Middle content-->

<!--start of Right content-->
<div id="rightCol">

    <div class="calloutBox trainingBlock accountLinksBox">
    <%-- the header links --%>
    <ul id="accountLinks">
    <%@ include file="includes/headerLinks.jsp" %>

    <%-- register message --%>
    <%-- <%@ include file="includes/headerMessage.jsp" %> --%>
    </ul>
    </div>

</div>
    <!--End of Right content-->
	<div class="clearDiv" style="height:2em;"></div>

<!-- End of content2 -->
</div>

		</div>
<!--end of wrqpper-->
</div>
<%@ include file="includes/jdm_footer.jsp" %>
	</body>
</html>
