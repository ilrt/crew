<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><fmt:message key="password.title"/></title>
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

<form:form method="post">

    <fieldset>
        <legend><strong><fmt:message key="password.title"/></strong></legend>

        <spring:hasBindErrors name="registrationBean">
            <c:forEach var="errMsgObj" items="${errors.allErrors}">
                <spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
            </c:forEach>
        </spring:hasBindErrors>

        <table>
            <tr>
                <td><fmt:message key="register.oldPassword"/></td>
                <td><form:password path="oldPassword"/></td>
                <td><form:errors path="oldPassword"/></td>
            </tr>
            <tr>
                <td><fmt:message key="register.password"/></td>
                <td><form:password path="passwordOne"/></td>
                <td><form:errors path="passwordOne"/></td>
            </tr>
            <tr>
                <td><fmt:message key="register.confirm.password"/></td>
                <td><form:password path="passwordTwo"/></td>
                <td><form:errors path="passwordTwo"/></td>
            </tr>
        </table>

        <p><input type="submit" value='<fmt:message key="password.update" />'/></p>

    </fieldset>

</form:form>

</div>


<%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>