<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="resource-type" content="document" />
<meta name="distribution" content="GLOBAL" />
<meta name="description" content="Intute - Conferences and events" />
<meta name="copyright" content="Intute 2009" />
<meta name="keywords" content="internet; resource; catalogue" />
<meta name="author" content="intute" />
<meta http-equiv="content-language" content="en" />

    <title><spring:message code="recording.title"/></title>
<style type="text/css" media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
<link rel="stylesheet" type="text/css" media="screen" href="http://www.intute.ac.uk/reset.css" />
<link rel="stylesheet" type="text/css" media="screen" href="http://www.intute.ac.uk/intute.css" />
<link rel="stylesheet" type="text/css" media="print" href="http://www.intute.ac.uk/intute-print.css" />
<script type="text/javascript" src="./js/prototype.js"></script>
<script type="text/javascript" src="./js/annotations.js"></script>

   <%--[if IE]>
        <link rel="stylesheet" href="http://www.intute.ac.uk/intute-ie.css" type="text/css">
        <![endif]--%>

<%--myintute code ############################################################## --%>
<script language="javascript" src="/myintute/scripts/button.js"
type="text/javascript"></script>
<script language="javascript" src="/myintute/scripts/functions.js"
type="text/javascript"></script>
<script language="javascript" src="/myintute/scripts/init.js"
type="text/javascript"></script>
<script language="javascript" src="/myintute/scripts/dosearch.js"
type="text/javascript"></script>
<script language="javascript" src="/myintute/scripts/shiv.js"
type="text/javascript"></script>
<script src="/myintute/scripts/prototype.js"
type="text/javascript"></script>
<script src="/myintute/scripts/scriptaculous.js"
type="text/javascript"></script>
<%-- ########################################################################### --%>

<script src="http://www.intute.ac.uk/scripts/jquery-1.3.2.min.js" type="text/javascript" charset="utf-8"></scri
pt>
<script src="http://www.intute.ac.uk/scripts/jquery.hoverIntent.minified.js" type="text/javascript" charset="ut
f-8"></scrip$
<script src="http://www.intute.ac.uk/scripts/mega-dropdown.js" type="text/javascript" charset="utf-8"></script>

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
<body onload="initializeAnnotations();setCookie();">

<%@ include file="includes/menu-services.jsp" %>

<%--CONTENT CONTAINER--%>
<div class="content-background">
<div class="content-container center">

<div class="content" id="content-full-width">

<div id="locationDetails">

            <c:if test="${browseHistory[1] != null}">
                <div id="place-nav">
                    <p class="breadcrumbs smalltext"><a href="${browseHistory[1].path}"><spring:message
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

<%-- end locationDetails --%>
</div>

<%--MyIntute--%>
<span id="load"></span>
<div id="container" class="myintute-container"><noscript>
<p><img src="/myintute/mockup_files/off.png" alt="tick" /><br/><b><font
color="red">MyInute functionality requires the use of Javascript and
cookies.</font></b>
<br/>To use MyIntute please enable javascript and cookies in your
browser.</p>

</noscript>
</div>

<%--end of Myintute--%>
<%-- end content-container --%>
</div>

<%--important div to prevent IE guillotine bug--%>
<div style="clear: both"></div>

<%-- end content container --%>
</div>

<%-- end content background --%>
</div>


<%-- FOOTER --%>
<%@ include file="includes/footer.jsp" %>
</body>
</html>

