<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="false" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title>${event.title}</title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
    <style type="text/css" media="screen"><!-- @import url(http://www.bristol.ac.uk/portal_css/uobcms_corporate.css); --></style>
    <link rel="stylesheet" type="text/css" media="print" href="http://www.bristol.ac.uk/portal_css/uobcms_print.css" />
    <style type="text/css" media="screen"><!-- @import url(http://www.ilrt.bris.ac.uk/styles/ilrt-style.css); --></style>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <meta http-equiv="Content-Language" content="en-uk" />
    <c:if test="${not empty event}">
    	<meta name="caboto-annotation" content="${event.id}"/>
    </c:if>
    <c:if test="${not empty feedList}">
        <c:forEach var="feed" items="${feedList}">
    <link rel="alternate" href="${feed.feedUrl}" type="${feed.contentType}" title="${feed.title}"/>
        </c:forEach>
    </c:if>
    <script type="text/javascript" src="./js/prototype.js"></script>
    <script type="text/javascript" src="./js/annotations.js"></script>
</head>

<body id="bristol-ac-uk" onload="initializeAnnotations();">
    <div class="uobnav" id="topnav">

        <!--htdig_noindex-->

        <a name="top" href="#uobcms-content-nonav" id="skip"
           title="Go straight to the content of this page">skip to content</a>

        <form action="http://www.ilrt.bris.ac.uk/powersearch.html" onsubmit="return (this.words.value != '' &amp;&amp; this.words.value != 'search')">
            <input type="text" onfocus="if (this.value == 'search') {this.value=''}" name="search" id="search" value="search" size="18" title="enter search keywords" />

            <input type="submit" id="submitwords" name="submitwords" value="search" class="searchbutton" />
        </form>

        <ul>
            <li><a href="http://www.bris.ac.uk/" title="University of Bristol homepage">university home</a></li>
            <li><a href="http://www.bristol.ac.uk/study/" title="Information about undergraduate, postgraduate and lifelong learning opportunities">study</a></li>
            <li><a href="http://www.bristol.ac.uk/research/" title="Information about research">research</a></li>
            <li><a href="http://www.bris.ac.uk/contacting-people/" title="Contact details for all staff and students, useful contacts and emergency contact details">contacting people</a></li>
            <li><a href="http://www.bris.ac.uk/index/" title="Complete a-z of faculties, depts, research centres and other aspects of the University">a-z index</a></li>
            <li><a href="http://www.bris.ac.uk/news/" title="All the latest news related to the University">news</a></li>
            <li class="no-separator"><a href="http://www.bris.ac.uk/help/" title="Your most common questions and queries answered">help</a></li>
        </ul>

    <!--/htdig_noindex-->
    </div>
    <div id="header">
    <div id="uoblogo"><a accesskey="1" href="http://www.bristol.ac.uk/"
                         title="University of Bristol homepage">University of Bristol</a></div>

    <div class="maintitle" id="maintitle1">
        <span id="title1"><a href="/">Institute for Learning &amp; Research Technology</a></span>
    </div>
    </div>


    <!-- end of HEADER -->

    <!--/htdig_noindex-->
     <div id="deptnav">
        <ul>
            <li><a href="http://www.ilrt.bris.ac.uk/">ILRT home</a></li>
             <li><a href="http://www.ilrt.bris.ac.uk/aboutus/" title="">About Us</a></li>
             <li><a href="http://www.ilrt.bris.ac.uk/aboutus/staff/" title="">Staff A-Z </a></li>
             <li><a href="http://www.ilrt.bris.ac.uk/whatwedo/projectsaz/" title="">Projects A-Z</a></li>
            <li><a href="http://www.ilrt.bris.ac.uk/aboutus/contactus/" title="">Contact Us</a></li>
        </ul>
    </div>



<div id="uobcms-wrapper">
<div id="uobcms-col1">
<!--htdig_noindex-->
<h2 class="navtitle">
ILRT
</h2>
<ul class="navgroup">
<li>
<a href="http://www.ilrt.bris.ac.uk/aboutus/">About Us</a>
<ul>
<li>
<a href="http://www.ilrt.bris.ac.uk/aboutus/contactus/">Contact Us</a>
</li>
<li>
<span class="link-on">
Maps and Directions
</span>
</li>
<li>
<a href="http://www.ilrt.bris.ac.uk/aboutus/staff/">Staff A-Z</a>
</li>

</ul>
</li>
<li>
<a href="http://www.ilrt.bris.ac.uk/whatwedo/">What We Do</a>
</li>
<li>
<a href="http://www.ilrt.bris.ac.uk/news/">News</a>
</li>
<li>
<a href="http://www.ilrt.bris.ac.uk/events/">Events</a>
</li>
</ul>

<!-- IE Fix --><div></div><!-- /IE Fix -->
<!--/htdig_noindex-->
</div>



 <!--UserTrail-->
<p id="breadcrumbs">
  <!--htdig_noindex-->
<a name="skipmenu" id="skipmenu" href="#skipmenu" accesskey="S"></a>
<a href="http://www.bristol.ac.uk/" title="University home">University home</a>
     >
<a href="http://www.ilrt.bris.ac.uk">ILRT home</a>
     >
<a href="http://www.ilrt.bris.ac.uk/aboutus">About Us</a>
     >
      Maps and Directions
       <!--/UserTrail-->
</p>

 <div id="uobcms-content">
   <h1 id="pagetitle">${event.title}</h1>

    <div id="footprint-box">
        <span><spring:message code="place.footprint.message"/></span>
    </div>
    <!-- Travelfootprint panel -->
    <iframe src="http://www.travelfootprint.org/journey_emissions_apis/" scrolling="no" border="0" frameborder="0"
        style="margin: 0 0 2em 1em; width: 300px; height: 250px; display:block;border: 0; overflow:hidden; clear:right;float:right">
    <p>View the <a href="http://www.travelfootprint.org/journey_emissions_apis/">Journey Emissions Tool</a></p></iframe>

<c:choose>
<c:when test="${not empty event}">

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
        
    <%-- EVENT LOCATIONS --%>
<c:if test="${not empty event.locations}">

        <h2><spring:message code="event.details.locations"/></h2>
        <ul>
        <c:forEach var="location" items="${event.locations}" varStatus="rowNo">
            <li><c:if test="${location.name != 'Locations'}">${location.name};</c:if></li>
        </c:forEach>
        </ul>
</c:if>

    <%-- EVENT DESCRIPTION --%>
<c:if test="${not empty event.description}">
    <h2><spring:message code="event.details.description"/></h2>
    <p>${event.description}</p>
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

<%--
<div id="annotations-block">
<h2>Comments about this event</h2>
<div id="annotations-results">
    <p>Sorry, you need a JavaScript enabled browser.</p>
</div>

<div id="annotation-note"><h3>Add your own comment or question about this event ...</h3>
- use wiki style notation to add links, <br />
e.g. Go to [ILRT|http://www.ilrt.bris.ac.uk/]
or just [http://www.ilrt.bris.ac.uk/]</div>

<div id="annotation-messages"></div>
--%>
    <%-- show form if they are logged in --%>
<%--
<security:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
    <form id="annotation-comment-form"
          action="javascript:processForm('<%=request.getUserPrincipal().getName()%>')"
          method="post">
        <p>
            <div class="annotation-form-label">Title:</div>
            <input id="annotation-title" type="text" name="title" size="50"/><br/>
            <div class="annotation-form-label">Body:</div>
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
    <p>You need to be <a href="./secured/displayProfile.do">logged in</a> to add a comment.<br/>
--%>
        <%-- You can <a href="./registration.do">register</a> if you do not have an account.<br/> --%>
<%--
        <a href="./forgottenPassword.do">Forgotten</a> your password?</p>

</security:authorize>
</div> <!-- close annotations-block div -->
--%>

</c:when>
<c:otherwise>
    <p><spring:message code="event.empty"/></p>
</c:otherwise>
</c:choose>

<br/>

<%--
REMOVED FOR NOW UNLESS THIS SERVICE IS REQUIRED FOR THE TEST
    <div class="content">
        <ul>
            <li><a href="${pageContext.request.contextPath}/secured/findJourneySharers.do"><fmt:message key="qlinks.people"/></a></li>
        </ul>
    </div>
--%>



  </div><!-- close uobcms-content div -->
 </div><!-- close wrapper div -->


<div id="deptnavbottom" class="deptnavbottom-nav">
    <ul>
        <li class="">
            <a href="/" title="ILRT home page">ILRT home</a>
        </li>
        <li class="">
            <a href="https://intranet.ilrt.bris.ac.uk/">Intranet</a>
        </li>
        <li class="">
            <a href="/aboutus/map-directions/">Finding the ILRT</a>
        </li>
        <li class="">
            <a href="http://www.bristol.ac.uk/university/maps/">Finding the University</a>
        </li>
    </ul>
    <!--/htdig_noindex-->
     </div>
   <div id="footer" class="footer-nonav">
			<p>Updated 19 May 2008 by the
        <a href="mailto:webmaster-ilrt@bris.ac.uk">ILRT</a><br />

				 University of Bristol, 8-10 Berkeley Square, Bristol BS8 1HH, UK. Tel: +44 (0)117 331 4430</p>
		</div>
		<div id="uobnavbottom" class="uobnavbottom-nonav">
    <!--htdig_noindex-->
    <ul>
    <li><a title="University of Bristol homepage" href="http://www.bris.ac.uk/">university home</a></li>
    <li><a href="http://www.bris.ac.uk/index/" title="Complete a-z of faculties, depts, research centres and other aspects of the University">a-z index</a></li>
    <li><a title="Your most common questions and queries answered" href="http://www.bris.ac.uk//help/">help</a></li>
    <li><a href="http://www.bris.ac.uk/university/web/terms-conditions.html">terms and conditions</a></li>
    <li><a href="http://www.bris.ac.uk/university/web/privacy-policy.html">privacy and cookie policy</a></li>
    <li class="no-separator"><a href="http://www.bris.ac.uk/university/web/terms-conditions.html#copyright">&copy; 2002-2010 University of Bristol</a></li>
    </ul>
    <!--/htdig_noindex-->
</div>
 <script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-6546258-1");
pageTracker._trackPageview();
} catch(err) {}
</script>

</body>
</html>
