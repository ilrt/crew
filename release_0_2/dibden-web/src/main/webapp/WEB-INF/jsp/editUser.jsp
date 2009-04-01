<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><fmt:message key="user.edit.title"/></title>
    <style type="text/css" media="screen">@import "<%=request.getContextPath()%>/style.css";</style>
</head>
<body>

<div class="user-management-container">

    <form:form action="./editUser.do" method="post" commandName="userForm">

        <%-- USER DETAILS --%>

        <fieldset>
            <legend><strong>User Details</strong></legend>

            <p><fmt:message key="user.edit.details"/></p>

            <table class="details-table">
                <tr>
                    <td><strong><fmt:message key="user.username"/></strong></td>
                    <td><form:input path="username" readonly="true"/></td>
                    <td><form:errors path="username"/></td>
                </tr>
                <tr>
                    <td><strong><fmt:message key="user.name"/></strong></td>
                    <td><form:input path="name"/></td>
                    <td><form:errors path="name"/></td>
                </tr>
                <tr>
                    <td><strong><fmt:message key="user.email"/></strong></td>
                    <td><form:input path="email"/></td>
                    <td><form:errors path="email"/></td>
                </tr>
            </table>
            <p>
                <input type="submit" name="updateUser" value="Update"/>
                <input type="submit" value="Cancel"/>
            </p>
        </fieldset>

        <%-- REMOVE ROLES --%>

        <c:if test="${not empty userForm.userRoles}">
            <fieldset>
                <legend><strong>Roles assigned to this user</strong></legend>
                <table>
                    <c:forEach var="userRole" items="${userForm.userRoles}">
                        <tr>
                            <td><form:radiobutton path="userRoleId" value="${userRole.roleId}"/></td>
                            <td>${userRole.roleId}</td>
                        </tr>
                    </c:forEach>
                </table>
                <p>
                    <input type="submit" name="removeRole" value="Remove"/>
                    <input type="submit" value="Cancel"/>
                </p>
            </fieldset>
        </c:if>

        <%-- ADD ROLES --%>

        <c:if test="${not empty userForm.roles}">
            <fieldset>
                <legend><strong>Assign new roles to this user</strong></legend>
            <p>
                <form:select path="addRoleId" multiple="false">
                    <c:forEach var="role" items="${userForm.roles}">
                        <form:option value="${role.roleId}"/>
                    </c:forEach>
                </form:select>
                <input type="submit" name="addRole" value="Add"/>
                <input type="submit" value="Cancel"/>
            </p>
            </fieldset>
        </c:if>
    </form:form>

</div>

</body>
</html>