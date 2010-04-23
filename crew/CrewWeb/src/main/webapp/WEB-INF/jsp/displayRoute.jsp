<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <title><spring:message code="route.details"/></title>
    <style type="text/css" media="screen">@import "./style.css";</style>
    <c:choose>
        <c:when test="${place != null && startPoint != null}">
            <script type="text/javascript"
                src="http://maps.google.com/maps/api/js?sensor=false"></script>
            <script type="text/javascript">
                var directionDisplay;
                var directionsService = new google.maps.DirectionsService();
                var map;
              var destination = new google.maps.LatLng(${place.latitude}, ${place.longitude});
              var startpoint = new google.maps.LatLng(${startPoint.latitude}, ${startPoint.longitude});
              function initialize() {
                  directionsDisplay = new google.maps.DirectionsRenderer();
                  var mapOptions = {
                    zoom:14,
                    mapTypeId: google.maps.MapTypeId.ROADMAP,
                    center: startpoint
                  }
                  map = new google.maps.Map(document.getElementById("map"), mapOptions);
                  directionsDisplay.setMap(map);

                  var route = {
                    origin: startpoint,
                    destination: destination,
                    travelMode: google.maps.DirectionsTravelMode.DRIVING
                  };
                  directionsService.route(route, function(response, status) {
                    if (status == google.maps.DirectionsStatus.OK) {
                      directionsDisplay.setDirections(response);
                    }
                  });
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
            <body onload="initialize()">

        </c:when>
        <c:otherwise>
            </head>
            <body>
        </c:otherwise>
    </c:choose>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNavAll.jsp" %>


    <%-- the logo banner --%>
    <%--<%@ include file="includes/logo.jsp" %>--%>

    <%-- The main content --%>
    <div id="mainBody">


        <%-- The left column: navigation --%>
        <!-- <div id="leftColumn"> -->

            <%-- quick links --%>
            <%--<%@ include file="includes/quickLinks.jsp" %>--%>

        <!-- </div> -->

        <%-- The right column: RSS Feeds etc --%>
        <div id="rightColumn">

            <%-- quick links --%>
            <%@ include file="includes/box-aboutCrew.jsp" %>

            <%-- upcoming events --%>
            <%--
            <%@ include file="includes/box-upcomingEvents.jsp" %>
            --%>

            <%-- recently added events --%>
            <%--
            <%@ include file="includes/box-recentlyAdded.jsp" %>
            --%>

        </div>

        <!-- Middle column: main content -->
        <div id="detailsColumn">

            <c:if test="${browseHistory[1] != null}">
                <div id="breadCrumb">
                    <a href="${browseHistory[1].path}"><spring:message code="nav.back2"/>&nbsp;${place.title}</a>
                </div>
            </c:if>

            <div id="place-details">

                <h3>Route from ${startPoint.title} to ${place.title}</h3>
                <fieldset>
                    <legend><strong><spring:message code="route.details"/></strong></legend>
                    <div id="map" style="width: 600px; height: 400px; float: left"></div>
                    <div id="routeLinks" style="border: solid grey; border-width: 1px; padding: 2px; margin-left: 605px">
                        <strong><spring:message code="route.mode"/></strong><br/>
                        <select id="mode" onchange="calcRoute();">
                          <option value="DRIVING">Driving</option>
                          <option value="WALKING">Walking</option>
                        </select>
                    </div>
                </fieldset>

            </div>


        </div>
    </div>


    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>