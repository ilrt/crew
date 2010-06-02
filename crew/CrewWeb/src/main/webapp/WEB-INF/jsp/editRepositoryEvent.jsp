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
                <p><fmt:message key="repository.edit.details"/></p>

                <form:form method="post" action="./editRepositoryEvent.do" commandName="repositoryEventForm">
                    <form:hidden path="eventId"/>
                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="repository.eventTitle"/></strong></td>
                            <td><form:input size="30" path="title"/></td>
                            <td><form:errors path="title"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartDate"/></strong></td>
                            <td><form:input size="30" path="startDate"/></td>
                            <td><form:errors path="startDate"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventEndDate"/></strong></td>
                            <td><form:input size="30" path="endDate"/></td>
                            <td><form:errors path="endDate"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventDescription"/></strong></td>
                            <td><form:textarea path="description" rows="4" cols="60"/></td>
                            <td><form:errors path="description"/></td>
                        </tr>
                    </table>
                    <table class="details-table" style="border: 1px solid grey; padding: 3px; margin: 6px 0 6px 0">
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocation"/></strong></td>
                            <td><form:input size="30" path="location"/></td>
                            <td><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input size="30" path="latitude"/></td>
                            <td><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input size="30" path="longitude"/></td>
                            <td><form:errors path="location"/>
                                <form:errors path="latitude"/>
                                <form:errors path="longitude"/>
                            </td>
                        </tr>
                    </table>
                        <%-- Location information --%>
                    <table class="details-table" style="border: 1px solid grey; padding: 3px; margin: 6px 0 6px 0">
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocationDescription"/></strong></td>
                            <td><form:textarea path="locationDescription" rows="5" cols="60"/></td>
                            <td><form:errors path="locationDescription"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocationUrl"/></strong></td>
                            <td><form:input size="40" path="locationUrl"/></td>
                            <td><form:errors path="locationUrl"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocationThumbUrl"/></strong></td>
                            <td><form:input size="40" path="locationThumbUrl"/></td>
                            <td><form:errors path="locationThumbUrl"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocationImagesUrl"/></strong></td>
                            <td><form:input size="40" path="locationImagesUrl"/></td>
                            <td><form:errors path="locationImagesUrl"/></td>
                        </tr>
                    </table>
                        <%-- Startpoint details --%>
                        <table class="details-table" style="border: 1px solid grey; padding: 3px; margin: 6px 0 6px 0">
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartPoint"/></strong>
                                <form:hidden path="startPointId1"/></td>
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
                            <td><form:hidden path="waypointId1_1"/>
                                Lat: <form:input size="30" path="waypointLat1_1"/><br/>
                                Long: <form:input size="30" path="waypointLong1_1"/><br/><br/></td>
                            <td><strong><fmt:message key="repository.eventWaypoint"/></strong></td>
                            <td><form:hidden path="waypointId1_2"/>
                                Lat: <form:input size="30" path="waypointLat1_2"/><br/>
                                Long: <form:input size="30" path="waypointLong1_2"/><br/><br/></td>
                            <td>
                                <form:errors path="waypointLat1_1"/>
                                <form:errors path="waypointLong1_1"/>
                                <form:errors path="waypointLat1_2"/>
                                <form:errors path="waypointLong1_2"/>
                            </td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartPoint"/></strong>
                                <form:hidden path="startPointId2"/></td>
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
                            <td><form:hidden path="waypointId2_1"/>
                                Lat: <form:input size="30" path="waypointLat2_1"/><br/>
                                Long: <form:input size="30" path="waypointLong2_1"/><br/><br/></td>
                            <td><strong><fmt:message key="repository.eventWaypoint"/></strong></td>
                            <td><form:hidden path="waypointId2_2"/>
                                Lat: <form:input size="30" path="waypointLat2_2"/><br/>
                                Long: <form:input size="30" path="waypointLong2_2"/><br/><br/></td>
                            <td>
                                <form:errors path="waypointLat2_1"/>
                                <form:errors path="waypointLong2_1"/>
                                <form:errors path="waypointLat2_2"/>
                                <form:errors path="waypointLong2_2"/>
                            </td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventStartPoint"/></strong>
                                <form:hidden path="startPointId3"/></td>
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
                            <td><form:hidden path="waypointId3_1"/>
                                Lat: <form:input size="30" path="waypointLat3_1"/><br/>
                                Long: <form:input size="30" path="waypointLong3_1"/></td>
                            <td><strong><fmt:message key="repository.eventWaypoint"/></strong></td>
                            <td><form:hidden path="waypointId3_2"/>
                                Lat: <form:input size="30" path="waypointLat3_2"/><br/>
                                Long: <form:input size="30" path="waypointLong3_2"/></td>
                            <td>
                                <form:errors path="waypointLat3_1"/>
                                <form:errors path="waypointLong3_1"/>
                                <form:errors path="waypointLat3_2"/>
                                <form:errors path="waypointLong3_2"/>
                            </td>
                        </tr>
                    </table>
                        <%-- KML files --%>
                    <table class="details-table" style="border: 1px solid grey; padding: 3px; margin: 6px 0 6px 0">
                        <tr>
                            <td><strong>Route title</strong>
                                <form:hidden path="kmlId1"/></td>
                            <td><form:input size="30" path="kmlTitle1"/></td>
                            <td>Type:
                                <form:select path="kmlType1">
                                    <form:option value="walking" label="Walking"/>
                                    <form:option value="cycling" label="Cycling"/>
                                    <form:option value="other" label="Other"/>
                                </form:select>
                            </td>
                            <td><strong>Copy 'n' Paste KML file</strong>: <form:textarea path="kmlXml1" rows="5" cols="60"/></td>
                            <td>
                                <form:errors path="kmlType1"/>
                                <form:errors path="kmlType1"/>
                                <form:errors path="kmlXml1"/>
                            </td>
                        </tr>
                        <tr>
                            <td><strong>Route title</strong>
                                <form:hidden path="kmlId2"/></td>
                            <td><form:input size="30" path="kmlTitle2"/></td>
                            <td>Type:
                                <form:select path="kmlType2">
                                    <form:option value="walking" label="Walking"/>
                                    <form:option value="cycling" label="Cycling"/>
                                    <form:option value="other" label="Other"/>
                                </form:select>
                            </td>
                            <td><strong>Copy 'n' Paste KML file</strong>: <form:textarea path="kmlXml2" rows="5" cols="60"/></td>
                            <td>
                                <form:errors path="kmlType2"/>
                                <form:errors path="kmlType2"/>
                                <form:errors path="kmlXml2"/>
                            </td>
                        </tr>
                        <tr>
                            <td><strong>Route title</strong>
                                <form:hidden path="kmlId3"/></td>
                            <td><form:input size="30" path="kmlTitle3"/></td>
                            <td>Type:
                                <form:select path="kmlType3">
                                    <form:option value="walking" label="Walking"/>
                                    <form:option value="cycling" label="Cycling"/>
                                    <form:option value="other" label="Other"/>
                                </form:select>
                            </td>
                            <td><strong>Copy 'n' Paste KML file</strong>: <form:textarea path="kmlXml3" rows="5" cols="60"/></td>
                            <td>
                                <form:errors path="kmlType3"/>
                                <form:errors path="kmlType3"/>
                                <form:errors path="kmlXml3"/>
                            </td>
                        </tr>
                    </table>

                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="repository.eventUrl"/></strong></td>
                            <td><form:input size="40" path="eventUrl"/></td>
                            <td><form:errors path="eventUrl"/></td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="updateButton" value='<fmt:message key="repository.update"/>'/>
                        <input type="submit" name="cancelButton" value='<fmt:message key="repository.cancel"/>'/>
                    </p>
                </form:form>

            </fieldset>
        </div>
                
    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>
