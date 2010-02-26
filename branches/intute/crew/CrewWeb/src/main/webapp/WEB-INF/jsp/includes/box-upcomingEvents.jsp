<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<div class="bl">
    <div class="br">
        <div class="tl">
            <div class="tr">
                <div class="box" id="upcomingEvents">
                    <h4 class="box-header"><fmt:message key="box.upcoming.title"/>
                        <a href="${pageContext.request.contextPath}/feeds/upcomingEvents.xml" style="border-bottom:0;">
				<img class="feedImage" 
				alt="<fmt:message key='image.alt.feed'/>"
                                src="${pageContext.request.contextPath}/images/feed-icon-14x14.png"/></a>
                    </h4>
                    <c:choose>
                        <c:when test="${not empty upcomingEvents}">
                            <ul class="box-list">
                                <c:forEach var="event" items="${upcomingEvents}">
                                    <c:url var="upcoming"
                                           context="${pageContext.request.contextPath}"
                                           value="/displayEvent.do">
                                        <c:param name="eventId">${event.id}</c:param>
                                    </c:url>
                                    <li class="box-list-item">
					<a href="${upcoming}">${event.title}</a><br/>
					<c:choose>
		                            <c:when test="${event.singleDay == true}">
                		                <joda:format value="${event.startDate}"
                                	             pattern="dd MMMM yyyy"/>
		                            </c:when>
                		            <c:otherwise>
                               			 <joda:format value="${event.startDate}"
                                         	    pattern="dd MMMM yyyy"/> -
		                                <joda:format value="${event.endDate}"
                	                             pattern="dd MMMM yyyy"/>
                        		    </c:otherwise>
		                        </c:choose>                                        
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <p class="box-message"><fmt:message key="box.upcoming.none"/></p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>
