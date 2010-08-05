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

    <%-- banner navigation--%>
    <%@ include file="includes/topNavLimited.jsp" %>

    </div>

    <%-- The main content --%>
    <div id="mainBody">


        <div class="journeysharers-submission-container">

            <form:form action="./findJourneySharers.do" method="POST">
                <%-- Show submit form with or without a message --%>

                <%-- FIND CAR SHARERS --%>

                <fieldset>
                    <legend><strong>Find delegates to car share with</strong></legend>

                    <p><fmt:message key="journeysharers.description"/></p>

                    <p>
                        <select name="maxDistance">
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5" selected="selected">5</option>
                            <option value="6">6</option>
                            <option value="7">7</option>
                            <option value="8">8</option>
                            <option value="9">9</option>
                            <option value="10">10</option>
                        </select>
                        Maximum distance (km)
                    </p>

                    <p>
                        <input type="submit" name="findJourneySharers" value="<fmt:message key="journeysharers.submitbutton"/>"/>
                        <input type="submit" name="cancelButton" value="Reset"/>
                    </p>
                    <c:if test="${not empty message}">
                        <p style="font-size: larger; font-weight: bold">${message}</p>
                    </c:if>
                </fieldset>
            </form:form>

        </div>

    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>