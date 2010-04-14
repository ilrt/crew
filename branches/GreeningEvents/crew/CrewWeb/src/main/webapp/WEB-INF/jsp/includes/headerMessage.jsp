<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<div id="headerMessage">
    <authz:authorize ifAllGranted="ROLE_ANONYMOUS">
        <fmt:message key="register"/>
    </authz:authorize>
</div>