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
    <%@ include file="includes/topNavLimited.jsp" %>

    <%-- the logo banner --%>
    <%--<%@ include file="includes/logo.jsp" %>--%>

    <%-- The main content --%>
    <div id="mainBody">

        <fieldset>

            <legend><strong>Administrative Actions</strong></legend>

            <security:authorize ifAllGranted="ROLE_ADMIN">
                <p><fmt:message key="admin.nav.msg"/></p>
                <ul>
                    <li><a href="./registration.do">Register new user</a></li>
                    <li><a href="./secured/admin/listUsers.do"><fmt:message key="admin.nav.list.users"/></a></li>
                    <li><a href="./secured/admin/listRoles.do"><fmt:message key="admin.nav.list.roles"/></a></li>
                    <li><a href="./secured/admin/listGroups.do"><fmt:message key="admin.nav.list.groups"/></a></li>
                    <li><a href="./secured/repository/listRepositoryEvents.do"><fmt:message key="admin.nav.list.events"/></a></li>
                    <li><a href="./secured/admin/addGroup.do"><fmt:message key="admin.nav.add.group"/></a></li>
                    <li><a href="./secured/admin/addRole.do"><fmt:message key="admin.nav.add.role"/></a></li>
                    <li><a href="./secured/repository/addRepositoryEvent.do"><fmt:message key="admin.nav.add.event"/></a></li>
                    <li><a href="./secured/admin/sparql.do"><fmt:message key="admin.nav.sparql"/></a></li>
					<li><a href="./secured/admin/reindex.do"><fmt:message key="admin.nav.reindex"/></a></li>
                </ul>
            </security:authorize>

            <security:authorize ifAllGranted="ROLE_HARVESTER_ADMIN">
                <p><fmt:message key="admin.nav.harvester"/></p>
                <ul>
                    <li><a  href="./secured/harvester/listHarvestSources.do"><fmt:message key="admin.nav.harvester.list"/></a></li>
                    <li><a  href="./secured/harvester/addHarvestSource.do"><fmt:message key="admin.nav.harvester.add"/></a></li>
                </ul>
            </security:authorize>

        </fieldset>

    </div>


    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>