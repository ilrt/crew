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

        <%-- Script for Google map lat long finder --%>
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/localEventRouteMaps.js"></script>
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
                <legend><strong><fmt:message key="repository.title.route"/></strong></legend>
                <p><fmt:message key="repository.add.route"/></p>

                <form:form method="post" action="./addRepositoryEventWizard.do" commandName="repositoryEventForm">
                        <%-- Startpoint details --%>
                        <table class="details-table" style="border: 1px solid grey; padding: 3px; margin: 6px 0 6px 0">
                            <tr><td colspan="7"><h2>Create routes from a start point lat-long</h2></td></tr>
                        <tr>
                            <td colspan="7">
                                <!-- Google map here -->
                                <p><span onclick="toggleMap('map_canvas_1',1)" style="text-decoration:underline; color: -webkit-link; cursor: pointer">Click to toggle lat/long map</span>
                                for Start point 1</p>
                                <div id="map_canvas_1" style="display: none; width: 600px; height: 400px; border-style: solid; border-width: 1px"></div>
                            </td>
                        </tr>
                        <tr>
                            <td style="width:15%"><strong><fmt:message key="repository.eventStartPoint1"/></strong></td>
                            <td><form:input size="30" path="startPoint1"/></td>
                            <td style="width:10%"><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input size="30" path="startPointLat1"/></td>
                            <td style="width:10%"><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input size="30" path="startPointLong1"/></td>
                            <td><span class="error"><form:errors path="startPoint1"/>
                                <form:errors path="startPointLat1"/>
                                <form:errors path="startPointLong1"/>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td style="text-align:right" valign="top"><strong><fmt:message key="repository.eventWaypoint1"/></strong></td>
                            <td>&nbsp;</td>
                            <td>Lat:&nbsp;&nbsp;&nbsp;<form:input size="30" path="waypointLat1_1"/><br/>
                            Long:&nbsp;<form:input size="30" path="waypointLong1_1"/><br/><br/></td>
                            <td valign="top"><strong><fmt:message key="repository.eventWaypoint2"/></strong></td>
                            <td>Lat:&nbsp;&nbsp;&nbsp;<form:input size="30" path="waypointLat1_2"/><br/>
                            Long:&nbsp;<form:input size="30" path="waypointLong1_2"/><br/><br/></td>
                            <td><span class="error">
                                <form:errors path="waypointLat1_1"/>
                                <form:errors path="waypointLong1_1"/>
                                <form:errors path="waypointLat1_2"/>
                                <form:errors path="waypointLong1_2"/>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="7">
                                <!-- Google map here -->
                                <p><span onclick="toggleMap('map_canvas_2',2)" style="text-decoration:underline; color: -webkit-link; cursor: pointer">Click to toggle lat/long map</span>
                                for Start point 2</p>
                                <div id="map_canvas_2" style="display: none; width: 600px; height: 400px; border-style: solid; border-width: 1px"></div>
                            </td>
                        </tr>
                        <tr>
                            <td style="width:15%"><strong><fmt:message key="repository.eventStartPoint2"/></strong></td>
                            <td><form:input size="30" path="startPoint2"/></td>
                            <td style="width:10%"><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input size="30" path="startPointLat2"/></td>
                            <td style="width:10%"><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input size="30" path="startPointLong2"/></td>
                            <td><span class="error"><form:errors path="startPoint2"/>
                                <form:errors path="startPointLat2"/>
                                <form:errors path="startPointLong2"/>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td style="text-align:right" valign="top"><strong><fmt:message key="repository.eventWaypoint1"/></strong></td>
                            <td>&nbsp;</td>
                            <td>Lat:&nbsp;&nbsp;&nbsp;<form:input size="30" path="waypointLat2_1"/><br/>
                            Long:&nbsp;<form:input size="30" path="waypointLong2_1"/><br/><br/></td>
                            <td valign="top"><strong><fmt:message key="repository.eventWaypoint2"/></strong></td>
                            <td>Lat:&nbsp;&nbsp;&nbsp;<form:input size="30" path="waypointLat2_2"/><br/>
                            Long:&nbsp;<form:input size="30" path="waypointLong2_2"/><br/><br/></td>
                            <td><span class="error">
                                <form:errors path="waypointLat2_1"/>
                                <form:errors path="waypointLong2_1"/>
                                <form:errors path="waypointLat2_2"/>
                                <form:errors path="waypointLong2_2"/>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="7">
                                <!-- Google map here -->
                                <p><span onclick="toggleMap('map_canvas_3',3)" style="text-decoration:underline; color: -webkit-link; cursor: pointer">Click to toggle lat/long map</span>
                                for Start point 3</p>
                                <div id="map_canvas_3" style="display: none; width: 600px; height: 400px; border-style: solid; border-width: 1px"></div>
                            </td>
                        </tr>
                        <tr>
                            <td style="width:15%"><strong><fmt:message key="repository.eventStartPoint3"/></strong></td>
                            <td><form:input size="30" path="startPoint3"/></td>
                            <td style="width:10%"><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input size="30" path="startPointLat3"/></td>
                            <td style="width:10%"><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input size="30" path="startPointLong3"/></td>
                            <td><span class="error"><form:errors path="startPoint3"/>
                                <form:errors path="startPointLat3"/>
                                <form:errors path="startPointLong3"/>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td style="text-align:right" valign="top"><strong><fmt:message key="repository.eventWaypoint1"/></strong></td>
                            <td>&nbsp;</td>
                            <td>Lat:&nbsp;&nbsp;&nbsp;<form:input size="30" path="waypointLat3_1"/><br/>
                            Long:&nbsp;<form:input size="30" path="waypointLong3_1"/></td>
                            <td valign="top"><strong><fmt:message key="repository.eventWaypoint2"/></strong></td>
                            <td>Lat:&nbsp;&nbsp;&nbsp;<form:input size="30" path="waypointLat3_2"/><br/>
                            Long:&nbsp;<form:input size="30" path="waypointLong3_2"/></td>
                            <td><span class="error">
                                <form:errors path="waypointLat3_1"/>
                                <form:errors path="waypointLong3_1"/>
                                <form:errors path="waypointLat3_2"/>
                                <form:errors path="waypointLong3_2"/>
                                </span>
                            </td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="_target3" value='Next'/>
                        <input type="submit" name="_target1" value='Back to location information'/>
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