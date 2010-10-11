<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title><spring:message code="route.details"/></title>
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
        <c:when test="${place != null && (startPoint != null || kml != null)}">
            <script type="text/javascript"
                src="http://maps.google.com/maps/api/js?sensor=false"></script>
            <script type="text/javascript">
                var directionDisplay;
                var directionsService = new google.maps.DirectionsService();
                var map;
                var destination = new google.maps.LatLng(${place.latitude}, ${place.longitude});
              <c:if test="${startPoint != null}">
                var startpoint = new google.maps.LatLng(${startPoint.latitude}, ${startPoint.longitude});
              </c:if>
                var centrepoint;
                var streetViewStartPoint;
                var kmlStartPoint;
              <c:choose>
                <c:when test="${kml != null}">
                    <c:choose>
                        <c:when test="${not empty kml.startLat && not empty kml.startLong}">
                            kmlStartPoint = new google.maps.LatLng(${kml.startLat}, ${kml.startLong});
                            centrepoint = kmlStartPoint;
                            streetViewStartPoint = kmlStartPoint;
                        </c:when>
                        <c:otherwise>
                            centrepoint = destination;
                            streetViewStartPoint = destination;
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                centrepoint = startpoint;
                streetViewStartPoint = startpoint;
                </c:otherwise>
              </c:choose>
              function initialize() {
                  directionsDisplay = new google.maps.DirectionsRenderer();
                  var mapOptions = {
                    zoom:14,
                    mapTypeId: google.maps.MapTypeId.ROADMAP,
                    center: centrepoint,
                    streetViewControl: true
                  }
                  map = new google.maps.Map(document.getElementById("mapDivTop"), mapOptions);

          <c:choose>
              <c:when test="${startPoint != null}">
                  <%-- We have a start point for a route --%>
                  directionsDisplay.setMap(map);
                  directionsDisplay.setPanel(document.getElementById("routeDirections"));
                  var waypoints = [];
                  var waypointLatLng;
                  <c:if test="${startPoint.waypoints != null}">
                     <c:forEach var="waypoint" items="${startPoint.waypoints}">
                        waypointLatLng = new google.maps.LatLng(${waypoint.latitude},${waypoint.longitude});
                        waypoints.push({
                          location:waypointLatLng,
                          stopover:false
                        });
                     </c:forEach>
                  </c:if>
                  var route = {
                    origin: startpoint,
                    destination: destination,
                    waypoints: waypoints,
                    optimizeWaypoints: true,
                    travelMode: google.maps.DirectionsTravelMode.WALKING
                  };
                  directionsService.route(route, function(response, status) {
                    if (status == google.maps.DirectionsStatus.OK) {
                      directionsDisplay.setDirections(response);
                    }
                  });
              </c:when>
              <c:otherwise>
                  <%-- We have a KML file url for a KML overlay --%>
                  var ctaLayer = new google.maps.KmlLayer('${kmlUrl}');
                  <%-- var ctaLayer = new google.maps.KmlLayer('http://tc-greeningevents.ilrt.bris.ac.uk/repository/route_KML_c2e3365f010736c98f03baaeb9ba2947.kml');  --%>
                  ctaLayer.setMap(map);
              </c:otherwise>
          </c:choose>
                <%-- Add streetview --%>
                  var panoramaOptions = {
                    position: streetViewStartPoint
                  };
                  var panorama = new  google.maps.StreetViewPanorama(document.getElementById("streetViewBottom"), panoramaOptions);
                  map.setStreetView(panorama);
              }

                function calcRoute() {
                  var selectedMode = document.getElementById("mode").value;
                  var route = {
                      origin: startpoint,
                      destination: destination,
                      travelMode: google.maps.DirectionsTravelMode[selectedMode]
                  };
                  directionsService.route(route, function(response, status) {
                    if (status == google.maps.DirectionsStatus.OK) {
                      directionsDisplay.setDirections(response);
                    }
                  });
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
<c:if test="${browseHistory[1] != null}">
    >
    <a href="${browseHistory[1].path}">Travel information to the ILRT</a>
    > Route details
</c:if>
</p>

 <div id="uobcms-content">
    <c:choose>
        <c:when test="${startPoint.title != null}">
            <h1 id="pagetitle">Route from ${startPoint.title} to ${place.title}</h1>
        </c:when>
        <c:when test="${kml.title != null}">
            <h1 id="pagetitle">Route from ${kml.title} to ${place.title}</h1>
        </c:when>
    </c:choose>

    <div id="mapContainerWide">
        <div id="mapDivTop"></div>
        <div id="streetViewBottom"></div>
        <div id="routeDirections"></div>
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
