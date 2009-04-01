<%@ page import="net.crew_vre.authorization.Permission" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<fieldset>
    <legend><strong><fmt:message key="harvester.addSource.legend"/></strong>
    </legend>


    <table>
        <tr>
            <td><strong><fmt:message key="harvester.form.location"/></strong></td>
            <c:choose>
                <c:when test="${type eq 'edit'}">
                    <td><form:input path="location" size="45" readonly="true"/></td>
                </c:when>
                <c:otherwise>
                    <td><form:input path="location" size="45"/></td>
                </c:otherwise>
            </c:choose>

            <td><form:errors path="location"/></td>
        </tr>
        <tr>
            <td><strong><fmt:message key="harvester.form.name"/></strong></td>
            <td><form:input path="name" size="45"/></td>
            <td><form:errors path="name"/></td>
        </tr>
        <tr>
            <td><strong><fmt:message key="harvester.form.description"/></strong>
            </td>
            <td><form:textarea path="description" rows="3" cols="40"/></td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td><strong><fmt:message key="harvester.form.blocked"/></strong></td>
            <td><form:checkbox path="blocked"/></td>
            <td>&nbsp;</td>
        </tr>
    </table>

</fieldset>

<fieldset>

    <legend><strong>Access Controls</strong></legend>

    <c:if test="${not empty source.authorityList}">

        <table id="permissions">
            <tr>
                <td>&nbsp;</td>
                <td>READ</td>
                <td>WRITE</td>
                <td>DELETE</td>
            </tr>

                <%-- get a list of ACLs assigned to this source --%>
            <c:forEach var="item" items="${source.authorityList}"
                       varStatus="status">
                <tr>
                    <td>${item.authority}</td>
                    <td>
                            <%-- search for READ permissions --%>
                        <input type="checkbox" name="AUTHORITY_${item.authority}"
                               value="${READ}"
                                <c:forEach var="permission"
                                           items="${item.permissions}">
                                    <c:if test="${permission eq READ}">checked='checked'</c:if>
                                </c:forEach>
                                />
                    </td>
                    <td>
                            <%-- search for WRITE permissions --%>
                        <input type="checkbox" name="AUTHORITY_${item.authority}"
                               value="${WRITE}"
                                <c:forEach var="permission"
                                           items="${item.permissions}">
                                    <c:if test="${permission eq WRITE}">checked='checked'</c:if>
                                </c:forEach>
                                />
                    <td>
                            <%-- search for DELETE permissions --%>
                        <input type="checkbox" name="AUTHORITY_${item.authority}"
                               value="${DELETE}"
                                <c:forEach var="permission"
                                           items="${item.permissions}">
                                    <c:if test="${permission eq DELETE}">checked='checked'</c:if>
                                </c:forEach>
                                />
                    </td>
                </tr>
            </c:forEach>

        </table>
    </c:if>

    <c:if test="${not empty authorities}">
        <div id="newRoleSelection">
            <p>Add other roles to assign permissions:</p>

            <p>
                <select id="otherRoles">
                    <c:forEach var="authority" items="${authorities}">
                        <option value="AUTHORIZATION_${authority}">${authority}</option>
                    </c:forEach>
                </select>
                <input id="addRolesButton" name="addRoles" type="submit" value="Add Role"/>
            </p>
        </div>
    </c:if>
</fieldset>