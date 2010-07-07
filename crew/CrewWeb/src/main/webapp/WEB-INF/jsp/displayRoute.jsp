<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <title><spring:message code="route.details"/></title>
    <style type="text/css" media="screen">@import "./style.css";</style>
    <link rel='stylesheet' type='text/css' media='screen'
          href='http://www.jiscdigitalmedia.ac.uk/?css=jdm/master.v.1267190280' />
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
              <c:choose>
                <c:when test="${kml != null}">
                  centrepoint = destination;
                </c:when>
                <c:otherwise>
                  centrepoint = startpiont;
                </c:otherwise>
              </c:choose>
              function initialize() {
                  directionsDisplay = new google.maps.DirectionsRenderer();
                  var mapOptions = {
                    zoom:14,
                    mapTypeId: google.maps.MapTypeId.ROADMAP,
                    center: centrepoint
                  }
                  map = new google.maps.Map(document.getElementById("mapDivLeft"), mapOptions);

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
                <%-- var ctaLayer = new google.maps.KmlLayer('http://www.ilrt.bris.ac.uk/~cmpac/kml/btm_ilrt.kml'); --%>
                ctaLayer.setMap(map);
              </c:otherwise>
          </c:choose>
              
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
            <body class="bodybg2" onload="initialize()">

        </c:when>
        <c:otherwise>
            </head>
            <body class="bodybg2">
        </c:otherwise>
    </c:choose>
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
    <%--
<!--start of Left content-->
    <div id="leftCol">
        <%@ include file="includes/headerBrowse.jsp" %>
    </div>
<!--end of Left content-->
--%>
<!--start of Middle content-->
    <div id="mapCol" class="traininglist map">

        <c:if test="${browseHistory[1] != null}">
            <div id="breadCrumb">
                <a href="listEvents.do"><spring:message code="event.crumb.events"/></a>
                <strong>&gt;</strong>
                <a href="${browseHistory[1].path}"><spring:message code="nav.back2"/>&nbsp;${place.title}</a>
            </div>
        </c:if>

        <div style="float:left">
            <%--<h3><spring:message code="route.details"/></h3>--%>
            <c:choose>
                <c:when test="${startPoint.title != null}">
                    <h1>Route from ${startPoint.title} to ${place.title}</h1>
                </c:when>
                <c:when test="${kml.title != null}">
                    <h1>Route from ${kml.title} to ${place.title}</h1>
                </c:when>
            </c:choose>
        </div>
        <div class="clearDiv"></div>
        <div>
            <div id="routeContainer">
                <div id="mapDivLeft"></div>
                <div id="routeDirections"></div>
            </div>
        </div>
    </div>
    <!--End of Middle content-->
    <div class="clearDiv" style="height:2em;"></div>

<!-- End of content2 -->
</div>

</div>
<!--end of wrapper-->
</div>
<%@ include file="includes/jdm_footer.jsp" %>
	</body>
</html>