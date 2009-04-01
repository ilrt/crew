<div class="bl"><div class="br"><div class="tl"><div class="tr">
    <div class="box" id="recentlyAdded">
	    <h4 class="box-header"><spring:message code="box.recentlyAdded.title" />
            <a href="./feeds/recentlyAdded.xml"><img class="feedImage" alt="<spring:message code='image.alt.feed'/>" src="./images/feed-icon-14x14.png" /></a></h4>
            <c:choose>
                <c:when test="${not empty recentlyAddedEvents}">
                    <ul class="box-list">
                        <c:forEach var="event" items="${recentlyAddedEvents}">
                            <li class="box-list-item"><a href="./displayEvent.do?eventId=<crew:uri uri='${event.id}'/>">${event.title}</a><br />
                                <joda:format value="${event.startDate}" pattern="dd MMMM yyyy" /> -
                                    <joda:format value="${event.endDate}" pattern="dd MMMM yyyy" />
                            </li>
                        </c:forEach>
                     </ul>
                </c:when>
                <c:otherwise>
                    <p class="box-message"><spring:message code="box.recentlyAdded.none" /></p>
                </c:otherwise>
            </c:choose>
	</div>
</div></div></div></div>