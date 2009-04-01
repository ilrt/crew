<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div id="navBanner">
    <div id="navLinks">
        <ul>
            <li><a href="${pageContext.request.contextPath}/"><fmt:message key="topNav.home"/></a>&nbsp;|</li>
            <li><a href="${pageContext.request.contextPath}/help.html"><fmt:message key="topNav.help"/></a>&nbsp;|</li>
            <li><a href="${pageContext.request.contextPath}/about.html"><fmt:message key="topNav.about"/></a>&nbsp;|</li>
            <% if (request.getUserPrincipal() == null) { %>
                <li><a href="${pageContext.request.contextPath}/registration.do"><fmt:message key="topNav.create"/>&nbsp;|</a></li>
                <li><a href="${pageContext.request.contextPath}/secured/displayProfile.do"><fmt:message key="topNav.login"/></a></li>
            <% } else { %>
                <li><a href="${pageContext.request.contextPath}/secured/displayProfile.do"><fmt:message key="topNav.profile"/></a>&nbsp;|</li>
                <li><a href="${pageContext.request.contextPath}/logout.do"><fmt:message key="topNav.logout"/></a></li>
            <% } %>
        </ul>
    </div>
</div>