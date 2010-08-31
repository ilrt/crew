<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="false" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title><spring:message code="event.list.title"/></title>
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/drupal_style_main.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/style_print.css" type="text/css" media="print" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <meta http-equiv="Content-Language" content="en-uk" />
    <link rel="Help" href="http://economicsnetwork.ac.uk/tenways" />
    <c:if test="${not empty feedList}">
        <c:forEach var="feed" items="${feedList}">
    <link rel="alternate" href="${feed.feedUrl}" type="${feed.contentType}" title="${feed.title}"/>
        </c:forEach>
    </c:if>
</head>

<body>
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
                                <h1 class="title">Greening Events</h1> <%-- Mid column main page title --%>
                                <%-- breadcrumb --%>
                            </div>
    <!-- /#content-header -->
    <div id="content-area">
    <%-- START OF CONTENT --%>
        <div id="node-89" class=" node-inner">
                <div class="content">
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
                            <h4><a href="./displayEvent.do?eventId=<crew:uri uri='${event.id}'/>">${event.title}</a></h4>

                            <c:if test="${not empty event.locations}">
                                    <c:forEach var="location" items="${event.locations}" varStatus="rowNo">
                                        <c:if test="${location.name != 'Locations'}"><p>${location.name};</p></c:if>
                                    </c:forEach>
                            </c:if>
                            <p>
                        <c:choose>
                            <c:when test="${event.singleDay == true}">
                                <joda:format value="${event.startDate}"
                                             pattern="dd MMMM yyyy"/>
                            </c:when>
                            <c:otherwise>
                                <joda:format value="${event.startDate}"
                                             pattern="dd MMMM yyyy"/> -
                                <joda:format value="${event.endDate}"
                                             pattern="dd MMMM yyyy"/>
                            </c:otherwise>
                        </c:choose>
                            </p>
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
    
    <%-- End class="content" --%>
    </div>

        <%-- End id="node-89" class=" node-inner" --%>
    </div>

    <%-- END id="content-area" --%>
    </div>

    <%-- end yui-u first --%>
    </div>

<!--
    <%-- RIGHT COLUMN --%>
                        <div class="yui-u"><%-- width: 32%; float: right --%>
                            <div id="rightcol">
                                <div id="sidebar-right-inner" class="region region-right">
                                    <div id="block-block-12" class="">
                                    <h3 class="title"><%-- Right col title --%></h3>
                                        <div class="content">
                                            <div class="compact">

                                            <%-- RIGHT COLUMN CONTENT --%>

                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
    <%-- END RIGHT COLUMN --%>
-->

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
