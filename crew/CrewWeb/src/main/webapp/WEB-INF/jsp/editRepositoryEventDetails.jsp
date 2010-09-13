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
        <title><fmt:message key="repository.edit.title"/></title>
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
        <div class="repository-management-container">

            <%@ include file="includes/adminLinks.jsp" %>

            <fieldset>
                <legend><strong><fmt:message key="repository.title.details"/></strong></legend>
                <p><fmt:message key="repository.edit.details"/></p>

                <form:form method="post" action="./editRepositoryEventWizard.do" commandName="repositoryEventForm">
                    <form:hidden path="eventId"/>
                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="repository.eventTitle"/></strong></td>
                            <td><form:input size="30" path="title"/></td>
                            <td><span class="error"><form:errors path="title"/></span></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartDate"/></strong></td>
                            <td><form:input size="30" path="startDate"/></td>
                            <td><span class="error"><form:errors path="startDate"/></span></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventEndDate"/></strong></td>
                            <td><form:input size="30" path="endDate"/></td>
                            <td><span class="error"><form:errors path="endDate"/></span></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventDescription"/></strong></td>
                            <td><form:textarea path="description" rows="5" cols="60"/></td>
                            <td><span class="error"><form:errors path="description"/></span></td>
                        </tr>
                    </table>

                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="repository.eventUrl"/></strong></td>
                            <td><form:input size="40" path="eventUrl"/></td>
                            <td><span class="error"><form:errors path="eventUrl"/></span></td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="_target1" value='Edit location details'/>
                        <input type="submit" name="_finish" value='Finish'/>
                        <input type="submit" name="_cancel" value='<fmt:message key="repository.cancel"/>'/>
                    </p>
                </form:form>

            </fieldset>
        </div>
                
    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>
    </body>
</html>
