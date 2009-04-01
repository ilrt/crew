<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><spring:message code="recording.title"/></title>
    <style type="text/css" media="screen">@import "./style.css";</style>

    <c:if test="${place.latitude != null}">

        <script type="text/javascript"
                src="http://www.google.com/jsapi?key=${googleMapKey}"></script>

        <script type="text/javascript">
            google.load("maps", "2.x");

            // Call this function when the page has been loaded
            function initialize() {
                var map = new google.maps.Map2(document.getElementById("map"));
                var point = new google.maps.LatLng(${place.latitude}, ${place.longitude});
                map.setCenter(point, 16);
                map.addOverlay(new GMarker(point));
                map.openInfoWindow(map.getCenter(), document.createTextNode("${place.title}"));
            }
            google.setOnLoadCallback(initialize);
        </script>

    </c:if>

</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNav.jsp" %>


    <%-- the logo banner --%>
    <%@ include file="includes/logo.jsp" %>

    <%-- The main content --%>
    <div id="mainBody">


        <%-- The left column: navigation --%>
        <div id="leftColumn">

            <%-- quick links --%>
            <%@ include file="includes/quickLinks.jsp" %>

        </div>

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
        <div id="middleColumn">


            <c:if test="${browseHistory[1] != null}">
                <div id="place-nav">
                    <p></p><a href="${browseHistory[1].path}"><spring:message
                        code="nav.back"/></a></p>
                </div>
            </c:if>

            <div id="place-details">

                <h3>${place.title}</h3>

                <fieldset>
                    <legend><strong><spring:message code="place.details"/></strong></legend>
                    <div id="map" style="width: 500px; height: 300px"></div>
                </fieldset>

            </div>


        </div>
    </div>


    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>