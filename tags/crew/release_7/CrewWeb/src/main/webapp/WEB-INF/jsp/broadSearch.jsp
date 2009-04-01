<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Search</title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNavAll.jsp" %>

    <%-- the logo banner --%>
    <%--<%@ include file="includes/logo.jsp" %>--%>

    <%-- The main content --%>
    <div id="mainBody">
       
	<div id="leftColumn"> <div class="bl"><div class="br"><div class="tl"><div class="tr"><div class="box">
                        <h4 class="box-header">Search</h4>	
		<form method="get" action="broadSearch.do">
      	    <h2>
      	    	<input type="search" name="searchTerm" value="${ searchTerm }"/>
      	    	<input type="submit" value="Search"/>
      	    </h2>
      	    <fieldset>
      	    	<legend>Limit to:</legend>
      	    	<input type="radio" name="type" value="all" checked="true"/> Everything<br/>
      	    	<input type="radio" name="type" value="people"/> People<br/>
      	    	<input type="radio" name="type" value="events"/> Events
      	    </fieldset>
        </form>
        </div></div></div></div></div> </div>
        <div id="middleColumn">
	        <c:set var="toggle" value="rowOdd"/>
	        <div>
	    <c:forEach var="aPage" items="${pages}">
	    	<c:choose>
	    	    <c:when test="${aPage == currentPage}">${aPage} </c:when>
	    	    <c:otherwise>
	    	        <a href="broadSearch.do?page=${aPage}&searchTerm=${searchTerm}&type=${type}">${aPage}</a>
	    	    </c:otherwise>
	    	</c:choose>
	    </c:forEach>
	    	<c:if test="${nextPage != -1}">
	    		<a href="broadSearch.do?page=${nextPage}&searchTerm=${searchTerm}&type=${type}">[next]</a>
	    	</c:if>
	    </div>
		<c:forEach var="item" items="${results}">
			<div class="${toggle} search-result">
                        <c:choose>
                           <c:when test="${toggle == 'rowOdd'}"><c:set var="toggle" value="rowEven"/></c:when>
                           <c:otherwise><c:set var="toggle" value="rowOdd"/></c:otherwise>
                        </c:choose>
				<p><a href="${ item.link }"><strong>${item.label}</strong></a>
                                <c:if test="${item.hasRecording}">
                                  <a href="${item.recordingLink}"><img src="images/video.png" alt="open recording"/></a>
                                </c:if>
                                </p>
				<p>${ item.description }</p>
				<c:if test="${ item.isAnnotated}">
				<fieldset class="anno-src">
                                <legend>${ item.annoAuthor }</legend>
                                ${ item.annoTitle }:
				${ item.annoDescription }</fieldset>
				</c:if>
			</div>
		</c:forEach>
        </div>
    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>
