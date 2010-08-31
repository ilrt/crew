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
