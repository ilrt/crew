<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="bl">
    <div class="br">
        <div class="tl">
            <div class="tr">
                <div class="box" id="quickLinks">
                    <h4 class="box-header"><fmt:message key="qlinks.title"/></h4>
                    <ul class="box-list">
                        <li class="box-list-item"><a href="${pageContext.request.contextPath}/"><fmt:message key="qlinks.home"/></a></li>
                        <li class="box-list-item"><a href="${pageContext.request.contextPath}/listEvents.do"><fmt:message key="qlinks.events"/></a></li>
                        <li class="box-list-item"><a href="${pageContext.request.contextPath}/listPeople.do"><fmt:message key="qlinks.people"/></a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>