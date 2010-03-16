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
    <title><fmt:message key="repository.events.title"/></title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
</head>
<body>

    <div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNavLimited.jsp" %>

    <%-- The main content --%>
    <div id="mainBody">

        <div class="repository-management-container">

            <c:choose>
                <c:when test="${not empty events}">

                    <p><fmt:message key="repository.events.message"/></p>

                    <form:form method="post" action="./listRepositoryEvents.do"
                               commandName="listRepositoryEventsForm">
                        <table class="list-table">
                            <thead class="list-header">
                                <tr>
                                    <th>&nbsp;</th>
                                    <th><fmt:message key="repository.events.title"/></th>
                                    <th><fmt:message key="repository.events.startDate"/></th>
                                    <th><fmt:message key="repository.events.endDate"/></th>
                                    <th><fmt:message key="repository.events.description"/></th>
                                    <th><fmt:message key="repository.events.location"/></th>
                                    <th><fmt:message key="repository.events.eventUrl"/></th>
                                </tr>
                            </thead>
                            <tbody class="list-body">
                                <c:forEach var="events" items="${events}" varStatus="status">
                                    <tr>
                                        <td>
                                            <c:out value="${event.title}"/>
                                            <form:hidden path="eventId" value="${event.eventId}"/>
                                        </td>
                                        <td><c:out value="${event.startDate}"/></td>
                                        <td><c:out value="${event.endDate}"/></td>
                                        <td><c:out value="${event.description}"/></td>
                                        <td><c:out value="${event.location}"/></td>
                                        <td><c:out value="${event.eventUrl}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <p>
                            <input type="submit" name="editButton" value="<fmt:message key="repository.events.edit"/>"/>
                            <input type="submit" name="deleteButton" value="<fmt:message key="repository.events.delete"/>"/>
                            <input type="submit" name="addButton" value="<fmt:message key="repository.events.add"/>"/>
                        </p>
                    </form:form>
                </c:when>
                <c:otherwise>
                    <p><fmt:message key="repository.events.none"/></p>
                </c:otherwise>
            </c:choose>

        </div>

    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>
