<div id="navBanner">
    <div id="navLinks">
        <ul>
            <li><a href="./"><spring:message code="topNav.home"/></a>&nbsp;|</li>
            <li><a href="./help.html"><spring:message code="topNav.help"/></a>&nbsp;|</li>
            <li><a href="./about.html"><spring:message code="topNav.about"/></a>&nbsp;|</li>
            <% if (request.getUserPrincipal() == null) { %>
                <li><a href="./registration.do"><spring:message code="topNav.create"/>&nbsp;|</a></li>
                <li><a href="./displayProfile.do"><spring:message code="topNav.login"/></a></li>
            <% } else { %>
                <li><a href="./displayProfile.do"><spring:message code="topNav.profile"/></a>&nbsp;|</li>
                <li><a href="./logout.do"><spring:message code="topNav.logout"/></a></li>
            <% } %>
        </ul>
    </div>
</div>