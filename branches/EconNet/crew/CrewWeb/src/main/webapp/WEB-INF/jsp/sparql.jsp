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
    <title><fmt:message key="sparql.title"/></title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
</head>
<body>

<div id="container">

    <%-- banner navigation--%>
    <%@ include file="includes/topNavLimited.jsp" %>

    <%-- the logo banner --%>
    <%--<%@ include file="includes/logo.jsp" %>--%>

    <%-- The main content --%>
    <div id="mainBody">


        <div class="sparql-container">

            <c:if test="${not empty results}">

                <p><fmt:message key="sparql.results"/></p>

                <table id="sparql-results-table">
                    <c:if test="${not empty vars}">
                        <tr id="sparql-results-table-header">
                            <c:forEach var="item" items="${vars}">
                                <th>${item}</th>
                            </c:forEach>
                        </tr>
                    </c:if>

                    <c:forEach var="row" items="${results}">
                        <tr>
                            <c:forEach var="item" items="${row}">
                                <td>${item}</td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>

            <p><fmt:message key="sparql.warnings"/></p>

            <form:form method="post" commandName="sparql">
                <p><form:textarea path="sparql" cols="100" rows="10"/></p>

                <p>
                    <input type="submit" name="addGroup" value='<fmt:message key="sparql.run"/>'/>
                </p>
            </form:form>
        </div>

    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>
