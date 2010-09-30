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
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/localEventKMLMaps.js"></script>
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
                <legend><strong><fmt:message key="repository.title.kml"/></strong></legend>
                <p><fmt:message key="repository.add.kml"/></p>

                <form:form method="post" action="./addRepositoryEventWizard.do" commandName="repositoryEventForm">
                        <%-- KML files --%>
                    <table class="details-table" style="border: 1px solid grey; padding: 3px; margin: 6px 0 6px 0">
                        <tr><td cols="4"><h3>Create route from a KML file</h3></td></tr>
                        <tr>
                            <td colspan="4">
                                <!-- Google map here -->
                                <p><span onclick="toggleMap('map_canvas_1',1)" style="text-decoration:underline; color: -webkit-link; cursor: pointer">Click to toggle lat/long map</span>
                                for route 1</p>
                                <div id="map_canvas_1" style="display: none; width: 600px; height: 400px; border-style: solid; border-width: 1px"></div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <strong>Route title:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong>
                                <form:input size="30" path="kmlTitle1"/><br/><br/>
                                <strong>Route start lat:&nbsp;&nbsp;&nbsp;</strong> <form:input size="15" path="kmlStartLat1"/><br/>
                                <strong>Route start long:</strong> <form:input size="15" path="kmlStartLong1"/>
                            </td>
                            <td>Type:
                                <form:select path="kmlType1">
                                    <form:option value="walking" label="Walking"/>
                                    <form:option value="cycling" label="Cycling"/>
                                    <form:option value="other" label="Other"/>
                                </form:select>
                            </td>
                            <td><strong>Copy 'n' Paste KML file</strong>: <form:textarea path="kmlXml1" rows="5" cols="60"/></td>
                            <td><span class="error">
                                <form:errors path="kmlType1"/>
                                <form:errors path="kmlType1"/>
                                <form:errors path="kmlXml1"/>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <!-- Google map here -->
                                <p><span onclick="toggleMap('map_canvas_2',2)" style="text-decoration:underline; color: -webkit-link; cursor: pointer">Click to toggle lat/long map</span>
                                for route 2</p>
                                <div id="map_canvas_2" style="display: none; width: 600px; height: 400px; border-style: solid; border-width: 1px"></div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <strong>Route title:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong>
                                <form:input size="30" path="kmlTitle2"/><br/><br/>
                                <strong>Route start lat:&nbsp;&nbsp;&nbsp;</strong> <form:input size="15" path="kmlStartLat2"/><br/>
                                <strong>Route start long:</strong> <form:input size="15" path="kmlStartLong2"/>
                            </td>
                            <td>Type:
                                <form:select path="kmlType2">
                                    <form:option value="walking" label="Walking"/>
                                    <form:option value="cycling" label="Cycling"/>
                                    <form:option value="other" label="Other"/>
                                </form:select>
                            </td>
                            <td><strong>Copy 'n' Paste KML file</strong>: <form:textarea path="kmlXml2" rows="5" cols="60"/></td>
                            <td><span class="error">
                                <form:errors path="kmlType2"/>
                                <form:errors path="kmlType2"/>
                                <form:errors path="kmlXml2"/>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <!-- Google map here -->
                                <p><span onclick="toggleMap('map_canvas_3',3)" style="text-decoration:underline; color: -webkit-link; cursor: pointer">Click to toggle lat/long map</span>
                                for route 3</p>
                                <div id="map_canvas_3" style="display: none; width: 600px; height: 400px; border-style: solid; border-width: 1px"></div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <strong>Route title:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</strong>
                                <form:input size="30" path="kmlTitle3"/><br/><br/>
                                <strong>Route start lat:&nbsp;&nbsp;&nbsp;</strong> <form:input size="15" path="kmlStartLat3"/><br/>
                                <strong>Route start long:</strong> <form:input size="15" path="kmlStartLong3"/>
                            </td>
                            <td>Type:
                                <form:select path="kmlType3">
                                    <form:option value="walking" label="Walking"/>
                                    <form:option value="cycling" label="Cycling"/>
                                    <form:option value="other" label="Other"/>
                                </form:select>
                            </td>
                            <td><strong>Copy 'n' Paste KML file</strong>: <form:textarea path="kmlXml3" rows="5" cols="60"/></td>
                            <td><span class="error">
                                <form:errors path="kmlType3"/>
                                <form:errors path="kmlType3"/>
                                <form:errors path="kmlXml3"/>
                                </span>
                            </td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="_finish" value='Finish'/>
                        <input type="submit" name="_target2" value='Back to route information'/>
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