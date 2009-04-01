<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="bl">
    <div class="br">
        <div class="tl">
            <div class="tr">
                <div class="box" id="aboutCrew">
                    <h4 class="box-header"><fmt:message key="box.about.title"/></h4>

                    <p class="box-text"><fmt:message key="box.about.description"/>
                        <a href="${pageContext.request.contextPath}/about.html"><fmt:message key="box.about.more"/></a></p>
                </div>
            </div>
        </div>
    </div>
</div>