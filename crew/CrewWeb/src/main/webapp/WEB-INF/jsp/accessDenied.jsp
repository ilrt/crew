<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page isErrorPage="true" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><fmt:message key="error.AccesDenied"/></title>
    <style type="text/css" media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNavLimited.jsp" %>

    <%-- the logo banner --%>
    <%--<%@ include file="includes/logo.jsp" %>--%>

    <%-- The main content --%>
    <div id="mainBody">

        <% if (exception.getCause().getClass().getName() == "net.crew_vre.authorization.AccessDeniedException") {%>

            <h3><fmt:message key="error.AccesDenied"/></h3>

            <p><fmt:message key="error.AccesDenied.details"/></p>

        <% } else {%>
            <h3><fmt:message key="error.general"/></h3>

            <p><fmt:message key="error.general.details"/></p>

        <% } %>
    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>