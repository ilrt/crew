<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
    <authz:authorize ifAllGranted="ROLE_ANONYMOUS">
        <li><fmt:message key="register"/></li>
    </authz:authorize>