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
    <title><fmt:message key="group.title"/></title>
    <style type="text/css" media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNav.jsp" %>

    <%-- the logo banner --%>
    <%@ include file="includes/logo.jsp" %>

    <%-- The main content --%>
    <div id="mainBody">


        <div class="user-management-container">

            <%@ include file="includes/adminLinks.jsp" %>

            <fieldset>
                <legend><strong><fmt:message key="group.title"/></strong></legend>

                <p><fmt:message key="group.addRoles.message"/></p>

                <p><fmt:message key="group.add.details"/></p>

                <form:form method="post" action="addGroup.do" commandName="groupForm">
                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="group.id"/></strong></td>
                            <td><form:input path="groupId"/></td>
                            <td><form:errors path="groupId"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="group.name"/></strong></td>
                            <td><form:input path="name"/></td>
                            <td><form:errors path="name"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="group.description"/></strong></td>
                            <td><form:input path="description"/></td>
                            <td><form:errors path="description"/></td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="addGroup"
                               value='<fmt:message key="group.add"/>'/>
                        <input type="submit" value='<fmt:message key="group.cancel"/>'/>
                    </p>
                </form:form>

            </fieldset>

        </div>

    </div>


    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>