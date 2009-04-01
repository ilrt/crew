<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html xmlns="http://www.w3.org/1999/xhtml">
<head><title><fmt:message key="password.title" /></title></head>
<body>


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
                <td><fmt:message key="register.email"/></td>
                <td><form:input path="email"/></td>
                <td><form:errors path="email"/></td>
            </tr>
        </table>

        <p><input type="submit" value='<fmt:message key="forgot.go" />'/></p>

    </fieldset>

</form:form>

</body>

</html>