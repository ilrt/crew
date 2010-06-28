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
    <link rel='stylesheet' type='text/css' media='screen'
          href='http://www.jiscdigitalmedia.ac.uk/?css=jdm/master.v.1267190280' />
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
                var map = new google.maps.Map(document.getElementById("mapDiv"), mapOptions);
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
                        contentString = '<a href="${place.locationImagesUrl}">Route images</a> to ${place.title}';
                    </c:when>
                    <c:when test="${place.title != null}">
                        contentString = '${place.title}';
                    </c:when>
                </c:choose>
                <c:if test="${place.locationDescription != null}">
                    contentString += '<br/>${place.locationDescription}';
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

        <div id="breadCrumb">
            <a href="listEvents.do"><spring:message code="event.crumb.events"/></a>
            <strong>&gt;</strong>
            <a href="displayEvent.do?eventId=<crew:uri uri='${eventId}'/>">${eventTitle}</a>
        </div>
        <div style="float:left">
            <h1>${place.title}</h1>
            <%--<h3><spring:message code="place.details"/></h3>--%>
        </div>
        <div class="clearDiv"></div>
        <div id="mapContainer">
            <div style="float: left">
                <div id="mapDiv"></div>
            </div>
            <div id="routeLinks">
                <h4>Routes to ${place.title}</h4>
                <c:if test="${startPointList != null}">
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
    </div>
    <!--End of Middle content-->
    <div class="clearDiv" style="height:2em;"></div>

<!-- End of content2 -->
</div>

		</div>
<!--end of wrqpper-->
</div>
<%@ include file="includes/jdm_footer.jsp" %>
	</body>
</html>
