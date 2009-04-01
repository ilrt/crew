<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<div id="headerMessage">
    <authz:authorize ifAllGranted="ROLE_ANONYMOUS">
        <p><fmt:message key="register"/></p>
    </authz:authorize>
</div>