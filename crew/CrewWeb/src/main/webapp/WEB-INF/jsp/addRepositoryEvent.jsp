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
        <title><fmt:message key="repository.add.title"/></title>
        <style type="text/css" media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
    </head>

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
                <legend><strong><fmt:message key="repository.title"/></strong></legend>
                <p><fmt:message key="repository.add.details"/></p>

                <form:form method="post" action="./addRepositoryEvent.do" commandName="repositoryEventForm">
                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="repository.eventTitle"/></strong></td>
                            <td><form:input path="title"/></td>
                            <td><form:errors path="title"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartDate"/></strong></td>
                            <td><form:input path="startDate"/></td>
                            <td><form:errors path="startDate"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventEndDate"/></strong></td>
                            <td><form:input path="endDate"/></td>
                            <td><form:errors path="endDate"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventDescription"/></strong></td>
                            <td><form:textarea path="description" rows="4" cols="60"/></td>
                            <td><form:errors path="description"/></td>
                        </tr>
                    </table>
                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocation"/></strong></td>
                            <td><form:input path="location"/></td>
                            <td><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input path="latitude"/></td>
                            <td><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input path="longitude"/></td>
                            <td><form:errors path="location"/>
                                <form:errors path="latitude"/>
                                <form:errors path="longitude"/>
                            </td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartPoint"/></strong></td>
                            <td><form:input path="startPoint1"/></td>
                            <td><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input path="startPointLat1"/></td>
                            <td><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input path="startPointLong1"/></td>
                            <td><form:errors path="startPoint1"/>
                                <form:errors path="startPointLat1"/>
                                <form:errors path="startPointLong1"/>
                            </td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartPoint"/></strong></td>
                            <td><form:input path="startPoint2"/></td>
                            <td><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input path="startPointLat2"/></td>
                            <td><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input path="startPointLong2"/></td>
                            <td><form:errors path="startPoint2"/>
                                <form:errors path="startPointLat2"/>
                                <form:errors path="startPointLong2"/>
                            </td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartPoint"/></strong></td>
                            <td><form:input path="startPoint3"/></td>
                            <td><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input path="startPointLat3"/></td>
                            <td><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input path="startPointLong3"/></td>
                            <td><form:errors path="startPoint3"/>
                                <form:errors path="startPointLat3"/>
                                <form:errors path="startPointLong3"/>
                            </td>
                        </tr>
                    </table>
                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="repository.eventUrl"/></strong></td>
                            <td><form:input path="eventUrl"/></td>
                            <td><form:errors path="eventUrl"/></td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="addButton" value='<fmt:message key="repository.add"/>'/>
                        <input type="submit" name="cancelButton" value='<fmt:message key="repository.cancel"/>'/>
                    </p>
                </form:form>

            </fieldset>
        </div>
                
    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>
