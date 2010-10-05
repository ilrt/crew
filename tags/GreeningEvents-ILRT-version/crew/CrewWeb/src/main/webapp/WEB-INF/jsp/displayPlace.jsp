<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title><spring:message code="place.details"/></title>
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
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <c:choose>
        <c:when test="${place.latitude != null}">
            <script type="text/javascript"
                src="http://maps.google.com/maps/api/js?sensor=false"></script>
            <script type="text/javascript">
              function initialize() {
                var latlng = new google.maps.LatLng(${place.latitude}, ${place.longitude});
                var mapOptions = {
                  zoom: 17,
                  center: latlng,
                  mapTypeId: google.maps.MapTypeId.ROADMAP
                };
                var map = new google.maps.Map(document.getElementById("mapDivLeft"), mapOptions);
                var marker = new google.maps.Marker({
                    position: latlng,
                    map: map,
                    title:"${place.title}"
                });
                var contentString;
                <c:choose>
			<%--
                    <c:when test="${place.title != null && place.locationUrl != null}">
                        contentString = '<a href="${place.locationUrl}">${place.title}</a>';
                    </c:when>
                    <c:when test="${place.title != null}">
                        contentString = '${place.title}';
                    </c:when>
			--%>
                    <c:when test="${place.title != null && place.locationImagesUrl != null}">
                        contentString = '<a href="${place.locationImagesUrl}">Images</a> of route to ${place.title}';
                    </c:when>
                    <c:when test="${place.title != null}">
                        contentString = '${place.title}';
                    </c:when>
                </c:choose>
                <c:if test="${place.locationDescription != null}">
                    contentString += '<br/><br/>${place.locationDescription}';
                </c:if>
			<%--
                <c:if test="${place.locationImagesUrl != null}">
                    contentString += '<br/><a href="${place.locationImagesUrl}">Location images</a>';
                </c:if>
			--%>
                <c:if test="${place.locationThumbUrl != null}">
                    contentString += '<br/><img src="${place.locationThumbUrl}"/>';
                </c:if>
                var infowindow = new google.maps.InfoWindow({
                    content: contentString
                });
                infowindow.open(map,marker);

              }
            </script>

            </head>
            <body id="bristol-ac-uk" onload="initialize()">

        </c:when>
        <c:otherwise>
            </head>
            <body id="bristol-ac-uk">
        </c:otherwise>
    </c:choose>
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
      <a href="displayEvent.do?eventId=<crew:uri uri='${eventId}'/>">${eventTitle}</a>
       <!--/UserTrail-->
</p>

 <div id="uobcms-content">
    <h1 id="pagetitle">${place.title} location information</h1>
    <div id="mapContainer">
        <div id="mapDivLeft"></div>
        <div id="routeLinks">
            <c:if test="${startPointList != null}">
                <h3>Walking routes to ${place.title} from:</h3>
            <ul>
                <c:forEach var="startPoint" items="${startPointList}">
                <li><a href="./displayRoute.do?placeId=<crew:uri uri='${place.id}'/>&amp;startPointId=<crew:uri uri='${startPoint.id}'/>">${startPoint.title}</a></li>
                </c:forEach>
            </ul>
            </c:if>
            <c:if test="${kmlList != null}">
            <ul>
                <c:forEach var="kml" items="${kmlList}">
                <li><a href="./displayRoute.do?placeId=<crew:uri uri='${place.id}'/>&amp;kml=<crew:uri uri='${kml.id}'/>">${kml.title}</a></li>
                </c:forEach>
            </ul>
            </c:if>
        </div>
    </div>

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
