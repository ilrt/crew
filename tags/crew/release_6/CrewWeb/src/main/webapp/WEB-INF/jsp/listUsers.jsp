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
    <title><fmt:message key="list.title"/></title>
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


        <div class="user-management-container">

            <%@ include file="includes/adminLinks.jsp" %>

            <p class="user-management-message"><spring:message code="list.total"
                                                               arguments="${total}"/></p>

            <c:if test="${pages > 1}">
                <p class="user-management-pag">Pages:
                    <c:forEach begin="1" end="${pages}" step="1" varStatus="aPage">
                        <c:choose>
                            <c:when test="${aPage.index == page}">
                                <strong>${aPage.index}</strong>
                            </c:when>
                            <c:otherwise>
                                <a href="?page=${aPage.index}">${aPage.index}</a>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${not aPage.last}">|</c:if>
                    </c:forEach>
                </p>
            </c:if>

            <c:if test="${total > 0}">
                <form:form commandName="listUserForm">
                    <table class="list-table">
                        <thead class="list-header">
                            <tr>
                                <td>&nbsp;</td>
                                <td><fmt:message key="list.users.username"/></td>
                                <td><fmt:message key="list.users.name"/></td>
                                <td><fmt:message key="list.users.email"/></td>
                                <td><fmt:message key="list.users.created"/></td>
                                <td><fmt:message key="list.users.groups"/></td>
                            </tr>
                        </thead>
                        <tbody class="list-body">
                            <c:forEach var="person" items="${results}" varStatus="status">
                                <c:choose>
                                    <c:when test="${status.count % 2 == 1}"><tr></c:when>
                                    <c:otherwise><tr class="alt-row"></c:otherwise>
                                </c:choose>
                                <td><form:radiobutton path="username"
                                                      value="${person.username}"/></td>
                                <td>${person.username}</td>
                                <td>${person.name}</td>
                                <td>${person.email}</td>
                                <td><fmt:formatDate value="${person.creationDate}"
                                                    pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td>
                                    <c:forEach var="userGroup" items="${person.groups}"
                                               varStatus="status">
                                        ${userGroup.groupId}<c:if test="${not status.last}">,</c:if>
                                    </c:forEach>
                                </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <p>
                        <input type="submit" name="editUser"
                               value='<fmt:message key="list.users.edit"/>'/>
                        <input type="submit" name="deleteUser"
                               value='<fmt:message key="list.users.delete"/>'/>
                    </p>
                </form:form>
            </c:if>

        </div>

    </div>

    <%-- the logo banner --%>
<%@ include file="includes/footer.jsp" %>