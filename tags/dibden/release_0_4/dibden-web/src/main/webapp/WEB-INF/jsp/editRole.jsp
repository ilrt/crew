<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><fmt:message key="role.edit.title"/></title>
    <style type="text/css" media="screen">@import "<%=request.getContextPath()%>/style.css";</style>
</head>
<body>

<div class="user-management-container">

    <%@ include file="includes/adminLinks.jsp" %>

    <fieldset>
        <legend><strong><fmt:message key="role.title"/></strong></legend>

        <p><fmt:message key="role.edit.details"/></p>

        <form:form action="./editRole.do" method="post" commandName="roleForm">
            <table class="details-table">
                <tr>
                    <td><strong><fmt:message key="role.id"/></strong></td>
                    <td><form:input path="roleId" readonly="true"/></td>
                    <td><form:errors path="roleId"/></td>
                </tr>
                <tr>
                    <td><strong><fmt:message key="role.name"/></strong></td>
                    <td><form:input path="name"/></td>
                    <td><form:errors path="name"/></td>
                </tr>
                <tr>
                    <td><strong><fmt:message key="role.description"/></strong></td>
                    <td><form:input path="description"/></td>
                    <td><form:errors path="description"/></td>
                </tr>
            </table>
            <p>
                <input type="submit" name="updateRole" value='<fmt:message key="role.update"/>'/>
                <input type="submit" value='<fmt:message key="role.cancel"/>'/>
            </p>
        </form:form>

    </fieldset>
</div>

</body>
</html>