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
    <%@ include file="includes/topNav.jsp" %>

    <%-- the logo banner --%>
    <%@ include file="includes/logo.jsp" %>

    <%-- The main content --%>
    <div id="mainBody">


        <fieldset>
            <legend><strong><fmt:message key="profile.title"/></strong></legend>
            <p><strong><fmt:message key="profile.username"/></strong>&nbsp;<c:out
                    value="${profile.username}"/></p>

            <p><strong><fmt:message key="profile.name"/></strong>&nbsp;<c:out
                    value="${profile.name}"/></p>

            <p><strong><fmt:message key="profile.email"/></strong>&nbsp;<c:out
                    value="${profile.email}"/></p>
        </fieldset>

        <fieldset>
            <legend><strong><fmt:message key="profile.actions"/></strong></legend>
            <ul>
                <li><a href="changePassword.do"><fmt:message key="profile.link.changePassword"/></a>
                </li>
                <li><a href="${pageContext.request.contextPath}/">Home</a></li>
            </ul>

            <security:authorize ifAllGranted="ROLE_ADMIN">
                <p><fmt:message key="admin.nav.msg"/></p>
                <ul>
                    <li><a href="./admin/listUsers.do"><fmt:message key="admin.nav.list.users"/></a></li>
                    <li><a href="./admin/listRoles.do"><fmt:message key="admin.nav.list.roles"/></a></li>
                    <li><a href="./admin/listGroups.do"><fmt:message key="admin.nav.list.groups"/></a>
                    </li>
                    <li><a href="./admin/addGroup.do"><fmt:message key="admin.nav.add.group"/></a></li>
                    <li><a href="./admin/addRole.do"><fmt:message key="admin.nav.add.role"/></a></li>
                </ul>
            </security:authorize>

            <security:authorize ifAllGranted="ROLE_HARVESTER_ADMIN">
                <p><fmt:message key="admin.nav.harvester"/></p>
                <ul>
                    <li><a  href="./harvester/listHarvestSources.do"><fmt:message key="admin.nav.harvester.list"/></a></li>
                    <li><a  href="./harvester/addHarvestSource.do"><fmt:message key="admin.nav.harvester.add"/></a></li>
                </ul>
            </security:authorize>

        </fieldset>

    </div>


    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>