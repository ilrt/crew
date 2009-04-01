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
    <title><fmt:message key="add.harvestsources.title"/></title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNav.jsp" %>

    <%-- the logo banner --%>
    <%@ include file="includes/logo.jsp" %>

    <%-- The main content --%>
    <div id="mainBody">


        <%--@elvariable id="sources" type="javax.util.List"--%>

        <div class="harvester-management-container">

            <p><fmt:message key="harvester.addSource.msg"/></p>

            <fieldset>
                <legend><strong><fmt:message key="harvester.addSource.legend"/></strong></legend>

                <form:form method="post" commandName="source">

                    <table>
                        <tr>
                            <td><strong><fmt:message key="harvester.form.location"/></strong></td>
                            <td><form:input path="location" size="40"/></td>
                            <td><form:errors path="location"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="harvester.form.name"/></strong></td>
                            <td><form:input path="name" size="40"/></td>
                            <td><form:errors path="name"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="harvester.form.description"/></strong></td>
                            <td><form:textarea path="description" rows="3" cols="40"/></td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="harvester.form.blocked"/></strong></td>
                            <td><form:checkbox path="blocked"/></td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td colspan="3">
                                <input type="submit" name="addButton" value="<fmt:message key="harvester.form.ok"/>"/>
                                <input type="submit" name="cancelButton" value="<fmt:message key="harvester.form.cancel"/>"/>
                            </td>
                        </tr>
                    </table>
                </form:form>

            </fieldset>

        </div>
    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>