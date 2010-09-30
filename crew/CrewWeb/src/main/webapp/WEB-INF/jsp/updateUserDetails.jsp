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
    <title><fmt:message key="user.edit.title"/></title>
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


        <div class="user-management-container">

            <form:form action="./updateUserDetails.do" method="post" commandName="userForm">

                <%-- USER DETAILS --%>

                <fieldset>
                    <legend><strong>Details for ${userForm.name}</strong></legend>

                    <p><fmt:message key="user.edit.details"/></p>

                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="user.username"/></strong></td>
                            <td><form:input path="username" readonly="true"/></td>
                            <td><form:errors path="username"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="user.name"/></strong></td>
                            <td><form:input path="name"/></td>
                            <td><form:errors path="name"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="user.postcode"/></strong></td>
                            <td><form:input path="postcode"/></td>
                            <td><form:errors path="postcode"/></td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td colspan="2">
                                <strong><fmt:message key="user.postcode.warning"/></strong>
                            </td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="user.email"/></strong></td>
                            <td><form:input path="email"/></td>
                            <td><form:errors path="email"/></td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="updateUser" value="Update"/>
                        <input type="submit" value="Cancel"/>
                    </p>
                </fieldset>

            </form:form>

        </div>

    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>