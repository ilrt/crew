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
    <title><fmt:message key="register.title"/></title>
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

<form:form commandName="registrationCommand" method="post">

    <spring:hasBindErrors name="registrationBean">
        <ul>
            <c:forEach var="errMsgObj" items="${errors.allErrors}">
                <li><spring:message code="${errMsgObj.code}"
                                    text="${errMsgObj.defaultMessage}"/></li>
            </c:forEach>
        </ul>
    </spring:hasBindErrors>

    <fieldset>

        <legend><strong><fmt:message key="register.title"/></strong></legend>

        <table>
            <tr>
                <td><fmt:message key="register.username"/></td>
                <td><form:input path="userName"/></td>
                <td><form:errors path="userName"/></td>
            </tr>
            <tr>
                <td><fmt:message key="register.name"/></td>
                <td><form:input path="name"/></td>
                <td><form:errors path="name"/></td>
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
            <tr>
                <td><fmt:message key="register.email"/></td>
                <td><form:input path="emailOne"/></td>
                <td><form:errors path="emailOne"/></td>
            </tr>
            <tr>
                <td><fmt:message key="register.email.confirm"/></td>
                <td><form:input path="emailTwo"/></td>
                <td><form:errors path="emailTwo"/></td>
            </tr>
            <tr>
                <td><fmt:message key="register.postcode"/></td>
                <td><form:input path="postcode"/></td>
                <td><form:errors path="postcode"/></td>
            </tr>
        </table>

        <script type="text/javascript"
                src="http://api.recaptcha.net/challenge?k=${publicKey}">
        </script>
        <noscript>
            <iframe src="http://api.recaptcha.net/noscript?k=${publicKey}"
                    height="300" width="500" frameborder="0">&nbsp;</iframe>
            <br/>
            <form:input path="recaptcha_challenge_field" size="40"/>
            <form:hidden path="recaptcha_response_field"/>
        </noscript>

        <p><form:errors path="recaptcha_challenge_field"/></p>

        <p><input type="submit" value='<fmt:message key="register.create" />'/></p>

    </fieldset>

</form:form>


</div>


<%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>