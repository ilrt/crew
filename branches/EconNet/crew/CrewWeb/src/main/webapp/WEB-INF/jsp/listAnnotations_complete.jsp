<%-- dtd and xml declaration --%>
<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ page session="false" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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

    <%-- the facets ---%>
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


</div>

<%-- The right column: RSS Feeds etc --%>
<div id="rightColumn">

    <%-- quick links --%>
    <%@ include file="includes/box-aboutCrew.jsp" %>

    <%-- upcoming events --%>
    <%@ include file="includes/box-upcomingEvents.jsp" %>

    <%-- recently added events --%>
    <%@ include file="includes/box-recentlyAdded.jsp" %>

</div>

<!-- Middle column: main content -->
<div id="middleColumn">

<form method="post">
	<input type="hidden" name="annotatorId" value="${annotatorId}" />
	<strong>Annotations for ${event.title}</strong>
	<table>
	<c:if test="${not empty annotations}">
		<c:forEach var="annotation" items="${annotations}" >
	
			<tr><td><input type="checkbox" name="annotationId" value="${annotation.annotationId}"></td><td>${annotation.listText}</td></tr>
		</c:forEach>
	</c:if> 
	</table>
	<p><input type="submit" /></p>
</form>


</div>


<%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>