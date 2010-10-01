<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<div id="headerContainer">

    <div id="headerLogo">
        <a href="${pageContext.request.contextPath}/"><img
                src="${pageContext.request.contextPath}/images/crew-logo.png" alt="CREW Logo"
                width="182" height="75"></a>
    </div>

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
                <li><a href="${pageContext.request.contextPath}/adminActions.do"><fmt:message key="topNav.admin"/></a>&nbsp;|</li>
            </authz:authorize>

            <%-- see links these whatever --%>
            <li><a href="${pageContext.request.contextPath}/help.html"><fmt:message key="topNav.help"/></a>&nbsp;|</li>
            <li><a href="${pageContext.request.contextPath}/about.html"><fmt:message key="topNav.about"/></a></li>

        </ul>
    </div>

    <div id="headerBrowse">
        <ul>
            <li><a href="${pageContext.request.contextPath}/listEvents.do"><fmt:message key="qlinks.events"/></a></li>
            <li><a href="${pageContext.request.contextPath}/listPeople.do"><fmt:message key="qlinks.people"/></a></li>
        </ul>
    </div>

    <div id="headerSearch">
        <input type="text"><input type="submit" value="Search">
    </div>

    <div id="headerMessage">
        <authz:authorize ifAllGranted="ROLE_ANONYMOUS">
            <p>Not registered? You can <a href="">create</a> an account.</p>
        </authz:authorize>
    </div>

</div>
