<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><fmt:message key="profile.title"/></title>
    <style type="text/css" media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%-- <%@ include file="includes/topNavLimited.jsp" %> --%>

    <%-- the logo banner --%>
    <%--<%@ include file="includes/logo.jsp" %>--%>
    <div id="headerContainer">

        <div id="headerLogo">
            <a href="../"><img
                    src="http://www.jiscdigitalmedia.ac.uk/images/site/logo.gif"
                    alt="JISC Digital Media Logo"
                    width="279" height="55"
                    style="margin-bottom: 2em"/></a>
        </div>

    </div>

    <%-- The main content --%>
    <div id="mainBody">


        <fieldset style="width: 400px">
            <legend><strong><fmt:message key="profile.title"/></strong></legend>
            <p><strong><fmt:message key="profile.username"/></strong>&nbsp;<c:out
                    value="${profile.username}"/></p>

            <p><strong><fmt:message key="profile.name"/></strong>&nbsp;<c:out
                    value="${profile.name}"/></p>

            <p><strong><fmt:message key="profile.email"/></strong>&nbsp;<c:out
                    value="${profile.email}"/></p>
        </fieldset>

        <fieldset style="width: 400px">
            <legend><strong><fmt:message key="profile.actions"/></strong></legend>
            <ul>
                <li><a href="changePassword.do"><fmt:message key="profile.link.changePassword"/></a>
                </li>
                <li><a href="${pageContext.request.contextPath}/">Home</a></li>
            </ul>
            
        </fieldset>

    </div>


    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>