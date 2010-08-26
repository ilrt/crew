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

                <form:form method="post" action="./addRepositoryEventWizard.do" commandName="repositoryEventForm">
                        <%-- Startpoint details --%>
                        <table class="details-table" style="border: 1px solid grey; padding: 3px; margin: 6px 0 6px 0">
                            <tr><td cols="6"><h3>Create route from a start point lat-long</h3></td></tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartPoint"/></strong></td>
                            <td><form:input size="30" path="startPoint1"/></td>
                            <td><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input size="30" path="startPointLat1"/></td>
                            <td><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input size="30" path="startPointLong1"/></td>
                            <td><form:errors path="startPoint1"/>
                                <form:errors path="startPointLat1"/>
                                <form:errors path="startPointLong1"/>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><strong><fmt:message key="repository.eventWaypoint"/></strong></td>
                            <td>&nbsp;</td>
                            <td>Lat: <form:input size="30" path="waypointLat1_1"/><br/>
                            Long: <form:input size="30" path="waypointLong1_1"/><br/><br/></td>
                            <td><strong><fmt:message key="repository.eventWaypoint"/></strong></td>
                            <td>Lat: <form:input size="30" path="waypointLat1_2"/><br/>
                            Long: <form:input size="30" path="waypointLong1_2"/><br/><br/></td>
                            <td>
                                <form:errors path="waypointLat1_1"/>
                                <form:errors path="waypointLong1_1"/>
                                <form:errors path="waypointLat1_2"/>
                                <form:errors path="waypointLong1_2"/>
                            </td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartPoint"/></strong></td>
                            <td><form:input size="30" path="startPoint2"/></td>
                            <td><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input size="30" path="startPointLat2"/></td>
                            <td><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input size="30" path="startPointLong2"/></td>
                            <td><form:errors path="startPoint2"/>
                                <form:errors path="startPointLat2"/>
                                <form:errors path="startPointLong2"/>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><strong><fmt:message key="repository.eventWaypoint"/></strong></td>
                            <td>&nbsp;</td>
                            <td>Lat: <form:input size="30" path="waypointLat2_1"/><br/>
                            Long: <form:input size="30" path="waypointLong2_1"/><br/><br/></td>
                            <td><strong><fmt:message key="repository.eventWaypoint"/></strong></td>
                            <td>Lat: <form:input size="30" path="waypointLat2_2"/><br/>
                            Long: <form:input size="30" path="waypointLong2_2"/><br/><br/></td>
                            <td>
                                <form:errors path="waypointLat2_1"/>
                                <form:errors path="waypointLong2_1"/>
                                <form:errors path="waypointLat2_2"/>
                                <form:errors path="waypointLong2_2"/>
                            </td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartPoint"/></strong></td>
                            <td><form:input size="30" path="startPoint3"/></td>
                            <td><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input size="30" path="startPointLat3"/></td>
                            <td><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input size="30" path="startPointLong3"/></td>
                            <td><form:errors path="startPoint3"/>
                                <form:errors path="startPointLat3"/>
                                <form:errors path="startPointLong3"/>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><strong><fmt:message key="repository.eventWaypoint"/></strong></td>
                            <td>&nbsp;</td>
                            <td>Lat: <form:input size="30" path="waypointLat3_1"/><br/>
                            Long: <form:input size="30" path="waypointLong3_1"/></td>
                            <td><strong><fmt:message key="repository.eventWaypoint"/></strong></td>
                            <td>Lat: <form:input size="30" path="waypointLat3_2"/><br/>
                            Long: <form:input size="30" path="waypointLong3_2"/></td>
                            <td>
                                <form:errors path="waypointLat3_1"/>
                                <form:errors path="waypointLong3_1"/>
                                <form:errors path="waypointLat3_2"/>
                                <form:errors path="waypointLong3_2"/>
                            </td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="_target3" value='Add KML files'/>
                        <input type="submit" name="_target1" value='Back to location information'/>
                        <input type="submit" name="_cancel" value='<fmt:message key="repository.cancel"/>'/>
                    </p>
                </form:form>

            </fieldset>
        </div>
                
    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>