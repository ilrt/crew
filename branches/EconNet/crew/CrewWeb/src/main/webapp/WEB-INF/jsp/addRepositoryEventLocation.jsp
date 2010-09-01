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
        <script type="text/javascript">
		var map;
		var infowindow;
		var marker;
		var listener;
		var controlOnText;
		var controlOffText;

                function initializeMap() {
                    var latlng = new google.maps.LatLng(53.80065082633023, -4.06494140625);
                    var mapOptions = {
                        zoom:5,
                        center: latlng,
                        draggableCursor: 'pointer',
                        mapTypeId: google.maps.MapTypeId.ROADMAP
                    };
                    map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);


                  // Create the DIV to hold the lat/lng off control and call the switchOffLatLngControl() constructor
                  // passing in this DIV.
                  var latLngOffControlDiv = document.createElement('DIV');
                  var latLngOffControl = new switchOffLatLngControl(latLngOffControlDiv, map);

                  map.controls[google.maps.ControlPosition.TOP_RIGHT].push(latLngOffControlDiv);

                  // Create the DIV to hold the lat/lng on control and call the switchOnLatLngControl() constructor
                  // passing in this DIV.
                  var latLngOnControlDiv = document.createElement('DIV');
                  var latLngOnControl = new switchOnLatLngControl(latLngOnControlDiv, map);

                  map.controls[google.maps.ControlPosition.TOP_RIGHT].push(latLngOnControlDiv);


              }

              function toggleMap(id) {
                var element = document.getElementById(id);
                if (element.style.display != 'none') {
                    element.style.display = 'none';
                } else {
                    element.style.display = '';
                    initializeMap();
                }
              }

                function switchOnLatLngControl(controlDiv, map) {

                    // Set CSS styles for the DIV containing the control
                    // Setting padding to 5 px will offset the control
                    // from the edge of the map
                    controlDiv.style.padding = '5px';

                    // Set CSS for the control border
                    var controlUI = document.createElement('DIV');
                    controlUI.style.backgroundColor = 'white';
                    controlUI.style.borderStyle = 'solid';
                    controlUI.style.borderWidth = '2px';
                    controlUI.style.cursor = 'pointer';
                    controlUI.style.textAlign = 'center';
                    controlUI.title = 'Click to enable display of latitude and longitude';
                    controlDiv.appendChild(controlUI);

                    // Set CSS for the control interior
                    controlOnText = document.createElement('DIV');
                    controlOnText.style.fontFamily = 'Arial,sans-serif';
                    controlOnText.style.fontSize = '12px';
                    controlOnText.style.paddingLeft = '4px';
                    controlOnText.style.paddingRight = '4px';
                    controlOnText.innerHTML = 'Lat/Long on';
                    controlUI.appendChild(controlOnText);

                    // Setup the event listener that will allow switching on of the click for lat/lng function
                    google.maps.event.addDomListener(controlUI, 'click', function() {
                        setLatLngListener();
                    });
                }

                function switchOffLatLngControl(controlDiv, map) {

                    // Set CSS styles for the DIV containing the control
                    // Setting padding to 5 px will offset the control
                    // from the edge of the map
                    controlDiv.style.padding = '5px';

                    // Set CSS for the control border
                    var controlUI = document.createElement('DIV');
                    controlUI.style.backgroundColor = 'white';
                    controlUI.style.borderStyle = 'solid';
                    controlUI.style.borderWidth = '2px';
                    controlUI.style.cursor = 'pointer';
                    controlUI.style.textAlign = 'center';
                    controlUI.title = 'Click to disable display of latitude and longitude';
                    controlDiv.appendChild(controlUI);

                    // Set CSS for the control interior
                    controlOffText = document.createElement('DIV');
                    controlOffText.style.fontFamily = 'Arial,sans-serif';
                    controlOffText.style.fontSize = '12px';
                    controlOffText.style.paddingLeft = '4px';
                    controlOffText.style.paddingRight = '4px';
                    controlOffText.style.fontWeight = 'bold';
                    controlOffText.innerHTML = 'Lat/Long off';
                    controlUI.appendChild(controlOffText);

                    // Setup the event listener that will allow switching off of the click for lat/lng function
                    google.maps.event.addDomListener(controlUI, 'click', function() {
                        disableLatLngListener();
                    });
                }

                function setLatLngListener() {
                    listener = google.maps.event.addListener(map, 'click', function(event) {
                            showLatLng(event.latLng);
                    });
                    controlOffText.style.fontWeight = 'normal';
                    controlOnText.style.fontWeight = 'bold';
                }

                function disableLatLngListener() {
                    if (listener != null) {
                        google.maps.event.removeListener(listener);
                        controlOffText.style.fontWeight = 'bold';
                        controlOnText.style.fontWeight = 'normal';
                    }
                }


		function showLatLng(location) {
                    document.getElementById('repositoryEventForm').latitude.value = location.lat();
                    document.getElementById('repositoryEventForm').longitude.value = location.lng();
		}


            </script>


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
                <legend><strong><fmt:message key="repository.title.location"/></strong></legend>
                <p><fmt:message key="repository.add.location"/></p>

                <form:form method="post" action="./addRepositoryEventWizard.do" commandName="repositoryEventForm">
                        <%-- Location information --%>
                    <table class="details-table" style="border: 1px solid grey; padding: 3px; margin: 6px 0 6px 0">
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocation"/></strong></td>
                            <td><form:input size="30" path="location"/></td>
                            <td><strong><fmt:message key="repository.eventLatitude"/></strong></td>
                            <td><form:input size="30" path="latitude"/></td>
                            <td><strong><fmt:message key="repository.eventLongitude"/></strong></td>
                            <td><form:input size="30" path="longitude"/></td>
                            <td><span class="error"><form:errors path="location"/>
                                <form:errors path="latitude"/>
                                <form:errors path="longitude"/></span>
                            </td>
                        </tr>
                    </table>
                    <!-- Google map here -->
                    <p onclick="toggleMap('map_canvas')" style="text-decoration:underline; color: -webkit-link; cursor: pointer">Click to toggle lat/long map</p>
                    <div id="map_canvas" style="display: none; width: 600px; height: 400px; border-style: solid; border-width: 1px"></div>
                    <table class="details-table" style="border: 1px solid grey; padding: 3px; margin: 6px 0 6px 0">
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocationDescription"/></strong></td>
                            <td><form:textarea path="locationDescription" rows="5" cols="60"/></td>
                            <td><span class="error"><form:errors path="locationDescription"/></span></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocationUrl"/></strong></td>
                            <td><form:input size="40" path="locationUrl"/></td>
                            <td><span class="error"><form:errors path="locationUrl"/></span></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocationThumbUrl"/></strong></td>
                            <td><form:input size="40" path="locationThumbUrl"/></td>
                            <td><span class="error"><form:errors path="locationThumbUrl"/></span></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="repository.eventLocationImagesUrl"/></strong></td>
                            <td><form:input size="40" path="locationImagesUrl"/></td>
                            <td><span class="error"><form:errors path="locationImagesUrl"/></span></td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="_target2" value='Next'/>
                        <input type="submit" name="_target0" value='Back to event details'/>
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