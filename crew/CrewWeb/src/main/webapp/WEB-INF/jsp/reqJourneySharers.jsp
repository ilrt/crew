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
    <title><fmt:message key="journeysharers.find.title"/></title>
    <style type="text/css" media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
</head>

<body>

<div id="container">

    <%-- banner navigation--%>
    <%-- <%@ include file="includes/topNavLimited.jsp" %> --%>

    <%-- the logo banner --%>
    <%--<%@ include file="includes/logo.jsp" %>--%>

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


        <div class="journeysharers-submission-container">

            <form:form action="./reqJourneySharers.do" commandName="journeySharersForm">
                <table class="list-table">
                    <thead class="list-header">
                        <tr>
                            <td>&nbsp;</td>
                            <td><fmt:message key="list.users.name"/></td>
                            <td><fmt:message key="list.users.distance"/></td>
                        </tr>
                    </thead>
                    <tbody class="list-body">
                        <c:forEach var="person" items="${journeysharers}" varStatus="status">
                            <c:choose>
                                <c:when test="${status.count % 2 == 1}"><tr></c:when>
                                <c:otherwise><tr class="alt-row"></c:otherwise>
                            </c:choose>
                            <td><form:checkbox path="usernames"
                                                  value="${person.username}"/></td>
                            <td>${person.name}</td>
                            <td>${distances[person.username]}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <p>
                    <fmt:message key="journeysharer.message.emailmessage"/>
                </p>
                <p>
                    <td><form:textarea path="message" rows="5" cols="60"/></td>
                </p>
                <p>
                    <input type="submit" name="sendEmails"
                           value='<fmt:message key="journeysharer.submit"/>'/>
                    <input type="submit" name="cancelButton"
                           value='Cancel'/>
                </p>
            </form:form>

        </div>

    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>