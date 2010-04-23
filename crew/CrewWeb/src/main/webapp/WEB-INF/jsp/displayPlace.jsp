<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <title><spring:message code="place.details"/></title>
    <style type="text/css" media="screen">@import "./style.css";</style>
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
                var map = new google.maps.Map(document.getElementById("map"), mapOptions);
                var marker = new google.maps.Marker({
                    position: latlng,
                    map: map,
                    title:"${place.title}"
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


            <div id="breadCrumb">
                <a href="listEvents.do"><spring:message code="event.crumb.events"/></a>
                <strong>&gt;</strong>
                <a href="displayEvent.do?eventId=<crew:uri uri='${eventId}'/>">${eventTitle}</a>
            </div>

            <div id="place-details">

                <h3>${place.title}</h3>

                <fieldset>
                    <legend><strong><spring:message code="place.details"/></strong></legend>
                    <div id="map" style="width: 600px; height: 400px; float: left"></div>
                    <div id="routeLinks" style="border: solid grey; border-width: 1px; padding: 2px; margin-left: 605px">
                        <h4>Routes to ${place.title}</h4>
                        <c:if test="${startPointList != null}">
                            <c:forEach var="startPoint" items="${startPointList}">
                                <a href="./displayRoute.do?placeId=<crew:uri uri='${place.id}'/>&amp;startPointId=<crew:uri uri='${startPoint.id}'/>">${startPoint.title}</a><br />
                            </c:forEach>
                        </c:if>
                    </div>
                </fieldset>

            </div>


        </div>
    </div>


    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>