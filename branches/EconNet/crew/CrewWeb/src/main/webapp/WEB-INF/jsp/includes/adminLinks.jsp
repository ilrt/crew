<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<p class="user-management-nav">[
    <a href="${pageContext.request.contextPath}/secured/admin/listUsers.do"><fmt:message key="admin.nav.list.users"/></a> |
    <a href="${pageContext.request.contextPath}/secured/admin/listRoles.do"><fmt:message key="admin.nav.list.roles"/></a> |
    <a href="${pageContext.request.contextPath}/secured/admin/listGroups.do"><fmt:message key="admin.nav.list.groups"/></a> |
    <a href="${pageContext.request.contextPath}/secured/repository/listRepositoryEvents.do"><fmt:message key="admin.nav.list.events"/></a> |
    <a href="${pageContext.request.contextPath}/secured/admin/addGroup.do"><fmt:message key="admin.nav.add.group"/></a> |
    <a href="${pageContext.request.contextPath}/secured/admin/addRole.do"><fmt:message key="admin.nav.add.role"/></a> ] |
    <a href="${pageContext.request.contextPath}/secured/repository/addRepositoryEvent.do"><fmt:message key="admin.nav.add.event"/></a> ]</p>