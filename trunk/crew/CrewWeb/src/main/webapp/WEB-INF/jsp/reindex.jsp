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
    <title><fmt:message key="reindexer.title"/></title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNavLimited.jsp" %>

    <%-- the logo banner --%>
    <%--<%@ include file="includes/logo.jsp" %>--%>

    <%-- The main content --%>
    <div id="mainBody">
		
		<c:choose>
			<c:when test="${indexRan}">
				<h4><fmt:message key="reindexer.completed"/>:</h4>
				<ul>
					<li><fmt:message key="reindexer.attempted"><fmt:param value="${attempted}"/></fmt:message></li>
					<li><fmt:message key="reindexer.errors"><fmt:param value="${errors}"/></fmt:message></li>
				</ul>
			</c:when>
			<c:otherwise>
				<h4><fmt:message key="reindexer.infomessage"/></h4>
			</c:otherwise>
		</c:choose>

      	<form method="post" action="reindex.do">
      	    <p><input type="submit" name="startReindex" value='<fmt:message key="reindexer.startreindex"/>'/></p>
        </form>

    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>