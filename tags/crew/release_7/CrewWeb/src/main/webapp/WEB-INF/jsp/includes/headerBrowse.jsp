<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div id="headerBrowse">
    <ul>
        <li><a href="${pageContext.request.contextPath}/listEvents.do"><fmt:message key="qlinks.events"/></a></li>
        <li><a href="${pageContext.request.contextPath}/listPeople.do"><fmt:message key="qlinks.people"/></a></li>
        <li><a href="${pageContext.request.contextPath}/broadSearch.do">Search</a></li>
    </ul>
</div>
