<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="false" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title><spring:message code="event.list.title"/></title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
    <style type="text/css" media="screen"><!-- @import url(http://www.bristol.ac.uk/portal_css/uobcms_corporate.css); --></style>
    <link rel="stylesheet" type="text/css" media="print" href="http://www.bristol.ac.uk/portal_css/uobcms_print.css" />
    <style type="text/css" media="screen"><!-- @import url(http://www.ilrt.bris.ac.uk/styles/ilrt-style.css); --></style>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <meta http-equiv="Content-Language" content="en-uk" />
    <c:if test="${not empty feedList}">
        <c:forEach var="feed" items="${feedList}">
    <link rel="alternate" href="${feed.feedUrl}" type="${feed.contentType}" title="${feed.title}"/>
        </c:forEach>
    </c:if>
</head>

<body id="bristol-ac-uk">
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
        <span id="title1"><a href="http://www.ilrt.bris.ac.uk/">Institute for Learning &amp; Research Technology</a></span>
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
<h4 class="navtitle">
ILRT
</h4>
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
   <a href="http://www.bristol.ac.uk/"
         title="University home">University home</a>
  > 
  <a href="http://www.ilrt.bris.ac.uk">ILRT home</a>     
     > 
<a href="http://www.ilrt.bris.ac.uk/aboutus">About Us</a>   
     > 
      Maps and Directions
       <!--/UserTrail-->
</p>

 <div id="uobcms-content">
   <h1 id="pagetitle">Maps and Directions</h1>


    <%-- display info about the number of results --%>
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
                <c:when test="${nav.totalPages > 1}">
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

   <!-- </div> -->

    <%-- the results --%>
    <div class="compact">
    <c:if test="${not empty listEvents}">
        <c:forEach var="event" items="${listEvents}" varStatus="rowNum">

            <c:choose>
                <c:when test="${not empty event.id}">
                    <div class="row" style="width:100%; padding: 0 0.9em 0 1em">
                            <div class="newsstory"><a href="./displayEvent.do?eventId=<crew:uri uri='${event.id}'/>">${event.title}</a></div>
                    </div>
                </c:when>
                <c:otherwise>
                    <h4 class="resultHeader">Sorry, you are not authorized to view the event details.</h4>
                </c:otherwise>
            </c:choose>

        </c:forEach>
    </c:if>

    <%-- provide navigation if there is more than one page --%>
    <c:if test="${nav.totalPages > 1}">
        <div class="pagination-bottom">
            <crew:nav navHelper="${nav}" params="${parameters}" className="pagination"/>
        </div>
    </c:if>
    <%-- End class="compact" --%>
    </div>


  </div><!-- close uobcms-content div -->
 </div><!-- close wrapper div -->


<div id="deptnavbottom" class="deptnavbottom-nav">
    <ul>
        <li class="">
            <a href="http://www.ilrt.bris.ac.uk/" title="ILRT home page">ILRT home</a>
        </li>
        <li class="">
            <a href="https://intranet.ilrt.bris.ac.uk/">Intranet</a>
        </li>
        <li class="">
            <a href="http://www.ilrt.bris.ac.uk/aboutus/map-directions/">Finding the ILRT</a>
        </li>
        <li class="">
            <a href="http://www.bristol.ac.uk/university/maps/">Finding the University</a>
        </li>
    </ul>
    <!--/htdig_noindex-->
     </div>
   <div id="footer" class="footer-nonav">
			<p>Updated 9 October 2010 by the
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
