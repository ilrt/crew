package net.crew_vre.web.controller.feeds;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.MainEventService;
import net.crew_vre.web.feed.EventFeedWriter;
import org.joda.time.DateTime;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RecentlyCreatedEventsFeedController.java 1132 2009-03-20 19:05:47Z cmmaj $
 *
 **/
public class RecentlyCreatedEventsFeedController implements Controller {

    public RecentlyCreatedEventsFeedController(final Map<String, String> config,
                                        final MainEventService mainEventService) {
        this.config = config;
        this.mainEventService = mainEventService;
    }

    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        // get the request url
        String requestUrl = request.getRequestURL().toString();

        int periodInDays;

        String val = config.get("periodInDays");
        if (val != null) {
            periodInDays = Integer.parseInt(val);
        } else {
            periodInDays = FALL_BACK_DAYS;
        }

        // create a start and end date and get events
        DateTime endDate = new DateTime();
        DateTime startDate = endDate.minusDays(periodInDays);
        List<Event> events = mainEventService.getEventsByCreationDate(startDate, endDate);

        // create the base URI
        String baseUrl = requestUrl.substring(0, requestUrl.length() - RECENTLY_ADDED.length());

        // create base link to the event (missing the parameter value)
        String eventUrlBase = baseUrl + config.get("eventUrlFragment");

        // write the feed
        response.setContentType(config.get("contentType"));
        EventFeedWriter eventFeedWriter = new EventFeedWriter();
        try {

            eventFeedWriter.write(response.getWriter(), events, baseUrl, requestUrl,
                    eventUrlBase, config);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, String> config;
    private MainEventService mainEventService;
    private static final String RECENTLY_ADDED = "feeds/recentlyAdded.xml";
    private static final int FALL_BACK_DAYS = 10;
}