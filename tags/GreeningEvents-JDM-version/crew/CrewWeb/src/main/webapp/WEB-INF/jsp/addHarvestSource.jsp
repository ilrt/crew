<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- icky, but can't use enums in jstl --%>
<% pageContext.setAttribute("READ", Permission.READ.intValue()); %>
<% pageContext.setAttribute("WRITE", Permission.WRITE.intValue()); %>
<% pageContext.setAttribute("DELETE", Permission.DELETE.intValue()); %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><fmt:message key="add.harvestsources.title"/></title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>

    <%@ include file="includes/harvestSourceHeader.jsp" %>

</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNavLimited.jsp" %>

    <%-- the logo banner --%>
    <%--<%@ include file="includes/logo.jsp" %>--%>

    <%-- The main content --%>
    <div id="mainBody">


        <%--@elvariable id="sources" type="javax.util.List"--%>

        <div class="harvester-management-container">

            <p><fmt:message key="harvester.addSource.msg"/></p>

            <form:form id="harvesterForm" method="post" commandName="source">

                <%@ include file="includes/harvestSourceForm.jsp" %>

                <p>
                    <input type="submit" name="addButton"
                           value="<fmt:message key="harvester.form.ok"/>"/>
                    <input type="submit" name="cancelButton"
                           value="<fmt:message key="harvester.form.cancel"/>"/>
                </p>

            </form:form>

        </div>
    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>