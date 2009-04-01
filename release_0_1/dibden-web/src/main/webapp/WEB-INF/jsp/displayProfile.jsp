<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html>
<head><title><fmt:message key="profile.title"/></title></head>
<body>

<fieldset>
    <legend><strong><fmt:message key="profile.title"/></strong></legend>
    <p><strong><fmt:message key="profile.username"/></strong>&nbsp;<c:out value="${profile.username}"/></p>
    <p><strong><fmt:message key="profile.name"/></strong>&nbsp;<c:out value="${profile.name}"/></p>
    <p><strong><fmt:message key="profile.email"/></strong>&nbsp;<c:out value="${profile.email}"/></p>
</fieldset>

<fieldset>
    <legend><strong><fmt:message key="profile.actions"/></strong></legend>
    <ul>
        <li><a href="changePassword.do"><fmt:message key="profile.link.changePassword"/></a></li>
    </ul>
</fieldset>


</body>
</html>