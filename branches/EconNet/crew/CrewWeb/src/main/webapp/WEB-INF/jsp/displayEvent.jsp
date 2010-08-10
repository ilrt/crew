<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title>${event.title}</title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
    <style type="text/css" media="screen">@import "./style.css";</style>
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/drupal_style_main.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/style_print.css" type="text/css" media="print" />
<c:if test="${not empty event}">
    <meta name="caboto-annotation" content="${event.id}"/>
</c:if>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <meta http-equiv="Content-Language" content="en-uk" />
    <link rel="Help" href="http://economicsnetwork.ac.uk/tenways" />
    <c:if test="${not empty feedList}">
        <c:forEach var="feed" items="${feedList}">
    <link rel="alternate" href="${feed.feedUrl}" type="${feed.contentType}" title="${feed.title}"/>
        </c:forEach>
    </c:if>
    <script type="text/javascript" src="./js/prototype.js"></script>
    <script type="text/javascript" src="./js/annotations.js"></script>
</head>

<body onload="initializeAnnotations();">
    <div id="doc2" class="yui-t2">
        <div id="hd">
            <div id="header">
                <a href="http://economicsnetwork.ac.uk/" name="top" id="top" accesskey="1">
                        <img src="http://economicsnetwork.ac.uk/nav/acadlogo.gif" id="logo" alt="Economics Network of the Higher Education Academy" title="Home Page of the Economics Network" style="width:292px;height:151px;border:0" />
                </a>
                <ul id="navlist">
                    <li class="toabout">
                            <a href="http://economicsnetwork.ac.uk/about" accesskey="2">About Us</a>
                    </li>
                    <li class="topubs">
                            <a href="http://economicsnetwork.ac.uk/journals" accesskey="3">Lecturer Resources</a>
                    </li>
                    <li class="tores">
                            <a href="http://economicsnetwork.ac.uk/resources" accesskey="4">Learning Materials</a>
                    </li>
                    <li class="tofunds">
                            <a href="http://economicsnetwork.ac.uk/projects" accesskey="5">Projects&nbsp;&amp; Funding</a>
                    </li>
                    <li class="tonews">
                            <a href="http://economicsnetwork.ac.uk/news" accesskey="6">News&nbsp;&amp; Events</a>
                    </li>
                    <li class="tothemes">
                            <a href="http://economicsnetwork.ac.uk/subjects/" accesskey="7">Browse by Topic</a>
                    </li>
                    <li id="help">
                            <a href="http://economicsnetwork.ac.uk/tenways" style="color:#036" accesskey="?">Help</a>
                    </li>
                </ul>
            </div>
            <div id="homelink">
                    <a href="http://economicsnetwork.ac.uk/">Home</a>
            </div>
        </div>
        <div id="bd">
            <div id="yui-main">
                <div class="yui-b">
                    <div class="yui-gc">
                        <div class="yui-u first" id="content"> <!-- width: 66%; float: left -->
                            <div id="content-header">
                                <h1 class="title">${event.title}</h1> <%-- Mid column main page title --%>
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
                            </div>
    <!-- /#content-header -->
    <div id="content-area">
    <%-- START OF CONTENT --%>
        <div id="node-89" class=" node-inner">
            <div class="content">
                <div class="compact">

<c:choose>
<c:when test="${not empty event}">

    <%-- EVENT DATES --%>
<c:if test="${not empty event.startDate}">
    <h4><spring:message code="event.details.date"/></h4>
    <p>
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
    </p>
</c:if>

    <%-- EVENT DESCRIPTION --%>
<c:if test="${not empty event.description}">
    <h4><spring:message code="event.details.description"/></h4>
    <p>${event.description}</p>
</c:if>

    <%-- EVENT PLACES --%>
<c:if test="${not empty event.places}">
        <h4><spring:message code="event.details.place"/></h4>
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

    <h4><spring:message code="event.details.schedule"/></h4>

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

        <h4><spring:message code="event.details.locations"/></h4>
        <ul>
        <c:forEach var="location" items="${event.locations}" varStatus="rowNo">
            <li><c:if test="${location.name != 'Locations'}">${location.name};</c:if></li>
        </c:forEach>
        </ul>

</c:if>

    <%-- EVENT SUBJECTS --%>
<c:if test="${not empty event.subjects}">
        <h4><spring:message code="event.details.subjects"/></h4>
        <ul>
        <c:forEach var="subject" items="${event.subjects}" varStatus="rowNo">
            <li><c:if test="${subject.name != 'Disciplines'}">${subject.name};</c:if></li>
        </c:forEach>
        </ul>
</c:if>

    <%-- EVENT TAGS--%>
<c:if test="${not empty event.tags}">
        <h4><spring:message code="event.details.tags"/></h4>
        <ul>
        <c:forEach var="tag" items="${event.tags}" varStatus="rowNo">
            <li>${tag}<c:if test="${not rowNo.last}">;</c:if></li>
        </c:forEach>
        </ul>
</c:if>

    <%-- EVENT EXTERNAL LINKS --%>
<c:if test="${not empty event.programme || not empty event.proceedings}">

    <h4><spring:message code="event.details.external"/></h4>
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

    <h4><spring:message code="event.roles"/></h4>
        <ul>
            <c:forEach var="role" items="${event.roles}">
                <li>${role.name}:&nbsp;<a
                        href="displayPerson.do?personId=<crew:uri uri='${role.heldBy.id}'/>">${role.heldBy.name}</a>
                </li>
            </c:forEach>
        </ul>

</c:if>

<!-- end compact -->
    </div>
<!-- end content -->
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


<div class="content">
    <div class="compact">
        <div class="seealso">
        <h3>Comments about this event</h3>
        <p>Sorry, you need a JavaScript enabled browser.</p>

        <p><strong>Add your own comment or question about this event ...</strong><br />
        - use wiki style notation to add links, <br />
        e.g. [Go to JISC Digital Media|http://www.jiscdigitalmedia.ac.uk]
        or [http://www.jiscdigitalmedia.ac.uk]</p>

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
            <p>You need to be <a href="./secured/displayProfile.do">logged in</a> to add an annotation.<br/>
                <%-- You can <a href="./registration.do">register</a> if you do not have an account.<br/> --%>
                <a href="./forgottenPassword.do">Forgotten</a> your password?</p>

        </security:authorize>


</c:when>
<c:otherwise>
    <p><spring:message code="event.empty"/></p>
</c:otherwise>
</c:choose>

    <%-- end seealso --%>
        </div>
<br/>
    <%-- End class="compact" --%>
    </div>

    <%-- End class="content" --%>
    </div>

        <%-- End id="node-89" class=" node-inner" --%>
    </div>

    <%-- END id="content-area" --%>
    </div>

    <%-- end yui-u first --%>
    </div>


    <%-- RIGHT COLUMN --%>
                        <div class="yui-u"><%-- width: 32%; float: right --%>
                            <div id="rightcol">
                                <div id="sidebar-right-inner" class="region region-right">
                                    <div id="block-block-12" class="">
                                    <h3 class="title">What's your travel footprint?</h3>
                                        <div class="content">
                                            <div class="compact">

                                            <%-- RIGHT COLUMN CONTENT --%>

    <div class="trainingBlock travelfootprint">
        <span><spring:message code="place.footprint.message"/></span>
    </div>
    <div class="travelfootprint">
        <!-- Travelfootprint panel -->
        <iframe src="http://www.travelfootprint.org/journey_emissions_apis/" scrolling="no" border="0" frameborder="0"
            style="margin-top:0; width: 300px; height: 250px; display:block;border: 0; overflow:hidden">
        <p>View the <a href="http://www.travelfootprint.org/journey_emissions_apis/">Journey Emissions Tool</a></p></iframe>
    </div>

                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
    <%-- END RIGHT COLUMN --%>


                    </div>
                </div>
            </div>

<%-- LEFT NAV --%>
            <div class="yui-b">		<!--googleoff: all-->
                <div id="snav">
                    <div class="snavtop"></div>
                    <form method="get" action="http://search2.openobjects.com/kbroker/hea/economics/search.lsim" class="sform">
                        <fieldset>
                            <input type="text" name="qt" size="15" maxlength="1000" value="" class="sbox" style="width:100px" />
                            <input id="submit" type="submit" value="Search" class="gobutton" style="width:4em" />
                            <input type="hidden" name="sr" value="0" />
                            <input type="hidden" name="nh" value="10" />
                            <input type="hidden" name="cs" value="iso-8859-1" />
                            <input type="hidden" name="sc" value="hea" />
                            <input type="hidden" name="sm" value="0" />
                            <input type="hidden" name="mt" value="1" />
                            <input type="hidden" name="ha" value="1022" />
                        </fieldset>
                    </form>
                    <%-- Left nav links --%>
                    <div class="content">
                        <ul>
                            <%@ include file="includes/headerLinks.jsp" %>
                            <%@ include file="includes/headerBrowse.jsp" %>
                        </ul>
                    </div>
                </div>
                <div class="snavbtm"></div>
                <%@include file="includes/econNetLeftNavBottom.jsp" %>
                </div>
            </div>

 <%@ include file="includes/footer.jsp" %>

    </div>
    <script src="http://www.economicsnetwork.ac.uk/gatag.js" type="text/javascript"></script>
    <script type="text/javascript">var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));</script>
    <script type="text/javascript">try{var pageTracker = _gat._getTracker("UA-1171701-1");pageTracker._trackPageview();} catch(err) {}</script>



</body>
</html>



