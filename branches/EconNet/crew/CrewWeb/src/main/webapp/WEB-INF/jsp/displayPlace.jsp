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
    <style type="text/css" media="screen">@import "./style.css";</style>
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/drupal_style_main.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/style_print.css" type="text/css" media="print" />
<c:if test="${not empty event}">
    <meta name="caboto-annotation" content="${event.id}"/>
</c:if>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <meta http-equiv="Content-Language" content="en-uk" />
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <link rel="Help" href="http://economicsnetwork.ac.uk/tenways" />
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
                var map = new google.maps.Map(document.getElementById("mapDivCentre"), mapOptions);
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
            <body onload="initialize()">

        </c:when>
        <c:otherwise>
            </head>
            <body>
        </c:otherwise>
    </c:choose>

    <div id="doc2" class="yui-t2">
        <div id="hd">
            <div id="header">
                <a href="http://economicsnetwork.ac.uk/" name="top" id="top" accesskey="1">
                        <img src="http://economicsnetwork.ac.uk/nav/acadlogo.gif" id="logo" alt="Economics Network of the Higher Education Academy" title="Home Page of the Economics Network" style="width:292px;height:151px;border:0" />
                </a>
                <ul id="navlist">
                    <li class="toabout">
                            <a href="http://economicsnetwork.ac.uk/about" accesskey="2">About Us</a>
                    </li>
                    <li class="topubs">
                            <a href="http://economicsnetwork.ac.uk/journals" accesskey="3">Lecturer Resources</a>
                    </li>
                    <li class="tores">
                            <a href="http://economicsnetwork.ac.uk/resources" accesskey="4">Learning Materials</a>
                    </li>
                    <li class="tofunds">
                            <a href="http://economicsnetwork.ac.uk/projects" accesskey="5">Projects&nbsp;&amp; Funding</a>
                    </li>
                    <li class="tonews">
                            <a href="http://economicsnetwork.ac.uk/news" accesskey="6">News&nbsp;&amp; Events</a>
                    </li>
                    <li class="tothemes">
                            <a href="http://economicsnetwork.ac.uk/subjects/" accesskey="7">Browse by Topic</a>
                    </li>
                    <li id="help">
                            <a href="http://economicsnetwork.ac.uk/tenways" style="color:#036" accesskey="?">Help</a>
                    </li>
                </ul>
            </div>
            <div id="homelink">
                    <a href="http://economicsnetwork.ac.uk/">Home</a>
            </div>
        </div>
        <div id="bd">
            <div id="yui-main">
                <div class="yui-b">
                    <div class="yui-gc">
                        <div class="yui-u first" id="content"> <!-- width: 66%; float: left -->
                            <div id="content-header">
                                <h1 class="title">${place.title} location information</h1> <%-- Mid column main page title --%>
                                <%-- BREAD CRUMB --%>
                                <div id="breadCrumb">
                                    <a href="listEvents.do"><spring:message code="event.crumb.events"/></a>
                                    <strong>&gt;</strong>
                                    <a href="displayEvent.do?eventId=<crew:uri uri='${eventId}'/>">${eventTitle}</a>
                                </div>
                            </div>
                            <!-- /#content-header -->
                            <div id="content-area">
                            <%-- START OF CONTENT --%>
                                <div id="node-89" class=" node-inner">
                                    <div class="content">
                                        <div class="compact">
                                            <div id="mapDivCentre"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="yui-u"> <%-- width: 32%; float: right --%>
                            <div id="rightcol">
                                <div id="sidebar-right-inner" class="region region-right">
                                    <div id="block-block-12" class="">
                                    <h3 class="title">Walking routes to ${place.title} from:</h3>
                                        <div class="content">
                                            <div class="compact">
                                                <div id="routeLinks">
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
                                    </div>
                                </div>
                            </div>
                        </div>
		<%-- END RIGHT COLUMN --%>


            </div>
        </div>
    </div>

    <%-- LEFT NAV --%>
    <div class="yui-b">		<!--googleoff: all-->
            <div id="snav">
                    <div class="snavtop"></div>
                    <form method="get" action="http://search2.openobjects.com/kbroker/hea/economics/search.lsim" class="sform">
                            <fieldset>
                                    <input type="text" name="qt" size="15" maxlength="1000" value="" class="sbox" style="width:100px" />
                                    <input id="submit" type="submit" value="Search" class="gobutton" style="width:4em" />
                                    <input type="hidden" name="sr" value="0" />
                                    <input type="hidden" name="nh" value="10" />
                                    <input type="hidden" name="cs" value="iso-8859-1" />
                                    <input type="hidden" name="sc" value="hea" />
                                    <input type="hidden" name="sm" value="0" />
                                    <input type="hidden" name="mt" value="1" />
                                    <input type="hidden" name="ha" value="1022" />
                            </fieldset>
                    </form>
                    <%-- Left nav links --%>
                    <div class="content">
                            <ul>
                                    <%@ include file="includes/headerLinks.jsp" %>
                                    <%@ include file="includes/headerBrowse.jsp" %>
                            </ul>
                    </div>
            </div>
            <div class="snavbtm"></div>
            <%@include file="includes/econNetLeftNavBottom.jsp" %>
            </div>
    </div>

 <%@ include file="includes/footer.jsp" %>

    </div>
    <script src="http://www.economicsnetwork.ac.uk/gatag.js" type="text/javascript"></script>
    <script type="text/javascript">var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));</script>
    <script type="text/javascript">try{var pageTracker = _gat._getTracker("UA-1171701-1");pageTracker._trackPageview();} catch(err) {}</script>

</body>
</html>
