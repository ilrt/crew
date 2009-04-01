<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><fmt:message key="list.roles.title"/></title>
    <style type="text/css" media="screen">@import "<%=request.getContextPath()%>/style.css";</style>
</head>
<body>

<div class="user-management-container">

    <p class="user-management-nav">[ <a href="./listUsers.do">List Users</a> |
        <a href="./listRoles.do">List Roles</a> ]</p>

    <p class="user-management-message"><spring:message code="list.roles.total"
                                                       arguments="${total}"/></p>

    <c:if test="${errors.errorCount > 0}">
        <p class="user-management-error">
            <c:forEach var="errMsgObj" items="${errors.allErrors}">
                <spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
            </c:forEach>
        </p>
    </c:if>

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
        <form:form commandName="listRoleForm" method="post">

            <table class="list-table">
                <thead class="list-header">
                    <tr>
                        <td>&nbsp;</td>
                        <td><fmt:message key="list.roles.id"/></td>
                        <td><fmt:message key="list.roles.name"/></td>
                        <td><fmt:message key="list.roles.description"/></td>
                    </tr>
                </thead>
                <tbody class="list-body">
                    <c:forEach var="userRole" items="${results}" varStatus="status">
                        <c:choose>
                            <c:when test="${status.count % 2 == 1}"><tr></c:when>
                            <c:otherwise><tr class="alt-row"></c:otherwise>
                        </c:choose>
                        <td><form:radiobutton path="roleId" value="${userRole.roleId}"/></td>
                        <td>${userRole.roleId}</td>
                        <td>${userRole.name}</td>
                        <td>${userRole.description}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <p>
                <input type="submit" name="newRole" value="Create Role"/>
                <input type="submit" name="editRole" value="Edit Role"/>
                <input type="submit" name="deleteRole" value="Delete Role"/>
            </p>
        </form:form>
    </c:if>

</div>

</body>
</html>