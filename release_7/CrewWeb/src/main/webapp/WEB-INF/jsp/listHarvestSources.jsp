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
    <title><fmt:message key="harvester.sources.title"/></title>
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


        <%--@elvariable id="sources" type="javax.util.List"--%>

        <div class="harvester-management-container">

            <c:choose>
                <c:when test="${not empty sources}">

                    <p><fmt:message key="harvester.sources.message"/></p>

                    <form:form method="post" action="./listHarvestSources.do"
                               commandName="listSourcesForm">
                        <table class="list-table">
                            <thead class="list-header">
                                <tr>
                                    <th>&nbsp;</th>
                                    <th><fmt:message key="harvester.sources.location"/></th>
                                    <th><fmt:message key="harvester.sources.name"/></th>
                                    <th><fmt:message key="harvester.sources.description"/></th>
                                    <th><fmt:message key="harvester.sources.lastVisit"/></th>
                                    <th><fmt:message key="harvester.sources.lastStatus"/></th>
                                    <th><fmt:message key="harvester.sources.blocked"/></th>
                                </tr>
                            </thead>
                            <tbody class="list-body">
                                <c:forEach var="source" items="${sources}" varStatus="status">
                                    <tr>
                                        <td><form:radiobutton path="id"
                                                              value="${source.location}"/></td>
                                        <td><c:out value="${source.location}"/></td>
                                        <td><c:out value="${source.name}"/></td>
                                        <td><c:out value="${source.description}"/></td>
                                        <td><c:out value="${source.lastVisited}"/></td>
                                        <td><c:out value="${source.lastStatus}"/></td>
                                        <td><c:out value="${source.blocked}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <p>
                            <input type="submit" name="editButton" value="<fmt:message key="harvester.sources.edit"/>"/>
                            <input type="submit" name="deleteButton" value="<fmt:message key="harvester.sources.delete"/>"/>
                            <input type="submit" name="addButton" value="<fmt:message key="harvester.sources.add"/>"/>
                            <input type="submit" name="harvestButton" value="<fmt:message key="harvester.sources.harvest"/>"/>
                        </p>
                    </form:form>
                </c:when>
                <c:otherwise>
                    <p><fmt:message key="harvester.sources.none"/></p>
                </c:otherwise>
            </c:choose>

        </div>

    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>
