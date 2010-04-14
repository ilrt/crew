<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<div id="headerLinks">

    <ul>

        <%-- anonymous user - see a login link --%>
        <authz:authorize ifAllGranted="ROLE_ANONYMOUS">
            <li><a href="${pageContext.request.contextPath}/secured/displayProfile.do"><fmt:message key="topNav.login"/></a>&nbsp;|</li>
        </authz:authorize>

        <%-- authenticated - see a logout link and profile links --%>
        <authz:authorize ifNotGranted="ROLE_ANONYMOUS">
            <li><a href="${pageContext.request.contextPath}/logout.do"><fmt:message key="topNav.logout"/></a>&nbsp;|</li>
            <li><a href="${pageContext.request.contextPath}/secured/displayProfile.do"><fmt:message key="topNav.profile"/></a>&nbsp;|</li>
        </authz:authorize>

        <%-- admin link --%>
        <authz:authorize ifAnyGranted="ROLE_ADMIN,ROLE_HARVESTER_ADMIN">
            <li><a href="${pageContext.request.contextPath}/adminActions.do"><fmt:message key="topNav.admin"/></a></li>
        </authz:authorize>

    </ul>

</div>