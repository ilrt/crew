<%-- dtd and xml declaration --%>
<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="false" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><spring:message code="event.list.title"/></title>


    <c:if test="${not empty feedList}">
        <c:forEach var="feed" items="${feedList}">
            <link rel="alternate" href="${feed.feedUrl}" type="${feed.contentType}"
                  title="${feed.title}"/>
        </c:forEach>
    </c:if>

    <style type="text/css" media="screen">@import "./style.css";</style>
    <link rel='stylesheet' type='text/css' media='screen'
          href='http://www.jiscdigitalmedia.ac.uk/?css=jdm/master.v.1271248856' />
</head>	<body class="bodybg2">
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
<input type="text" name="keywords" id="search" value=""  /> <input type

Creating all files for displayRoute.do="submit" name="searchBtn" id="searchBtn" value="Search" title="Search" />
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
        <!--start of Left content-->

        <div id="leftCol">


    <%-- quick links --%>
    <%-- <%@ include file="includes/quickLinks.jsp" %> --%>
    <%-- the browser links --%>
    <%@ include file="includes/headerBrowse.jsp" %>

    <%-- the facets ---%>
    <%--
    <div class="bl">
        <div class="br">
            <div class="tl">
                <div class="tr">
                    <div class="box" id="facetNavigation">
                        <h4 class="box-header"><spring:message code="facet.title"/></h4>
                        <c:forEach var="facet" items="${facets}">
                            <crew:facet facet="${facet}" showEmpty="false" url="${url}"
                                        parameters="${parameters}"/>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
    --%>


</div>
<!--end of Left content-->

<!--start of Middle content-->
<div id="midCol" class="genericContent traininglist">
    <h1>Greening Events</h1>
    <div class="contentBlock">
    <%-- display info about the number of results --%>
    <div class="jdmResultDetails">
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
        </div>
            <%-- provide navigation if there is more than one page --%>
        <c:if test="${nav.totalPages > 1}">
            <crew:nav navHelper="${nav}" params="${parameters}" className="pagination"/>
        </c:if>
        </c:when>
        <c:otherwise>
            </div>
        </c:otherwise>
        </c:choose>

    </div>

    <%-- the results --%>
    <c:if test="${not empty listEvents}">
        <c:forEach var="event" items="${listEvents}" varStatus="rowNum">

            <c:choose>
                <c:when test="${not empty event.id}">
                    <div class="row">
                        <div class="rowleft">                            
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
                        </div>
                        <div class="rowmid">
                            <a href="./displayEvent.do?eventId=<crew:uri uri='${event.id}'/>">${event.title}</a>

                            <c:if test="${not empty event.locations}">
                                <br />
                                    <c:forEach var="location" items="${event.locations}" varStatus="rowNo">
                                        <c:if test="${location.name != 'Locations'}">${location.name};</c:if>
                                    </c:forEach>
                            </c:if>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <h5 class="resultHeader">Sorry, you are not authorized to view the event details.</h5>
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
        <!-- end contentBlock -->
    </div>

    </div>
<!--End of Middle content-->

<!--start of Right content-->
<div id="rightCol">

    <div class="calloutBox trainingBlock accountLinksBox">
    <%-- the header links --%>
    <ul id="accountLinks">
    <%@ include file="includes/headerLinks.jsp" %>

    <%-- register message --%>
    <%@ include file="includes/headerMessage.jsp" %>
    </ul>
    </div>

</div>
    <!--End of Right content-->
	<div class="clearDiv" style="height:2em;"></div>

<!-- End of content2 -->
</div>

		</div>
<!--end of wrqpper-->
</div>
<!--start of footer-->

<!--<div id="survey2"><a href="http://www.surveymonkey.com/s.aspx?sm=OKJIQzf8G0oeXKpzq63NTA_3d_3d">Website feedback</a></div>-->
    <div class="footer">
            <div class="footerInner">
                    <ul><li class="diamond" id="sitemapdiamond"><a href="/site-map/">Sitemap</a></li><li class="diamond" id="glossary"><a href="/glossary/">Glossary</a></li><li class="diamond" id="newsletter"><a href="/newsletter/">Newsletter</a></li><li class="diamond" id="mailinglist"><a href="/mailing-list/">Mailing list</a></li><li class="diamond" id="disclaimer"><a href="/disclaimer/">Disclaimer</a></li> <li class="copyright"><a href="/copyright/">Copyright &copy; 2010 JISC Digital Media</a></li></ul>
                    <span class="advancelogo"><a href="http://www.jiscadvance.ac.uk/"><img src="http://www.jiscdigitalmedia.ac.uk/images/site/jisc-advance-logo.png" border="0" width="150" height="43" alt="JISC Advance" /></a></span>

            </div>
<span class="printlogo"><a href="http://www.jiscdigitalmedia.ac.uk/"><img src="/images/site/print-logo.jpg" border="0" width="666" height="212" alt="JISC Digital Media" /></a></span>
    </div>

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-7006779-1");
pageTracker._trackPageview();
} catch(err) {}</script>
					<!--end of footer-->
	</body>
</html>