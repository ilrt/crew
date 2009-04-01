package net.crew_vre.web.controller.feeds;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.service.MainEventService;
import net.crew_vre.web.feed.EventFeedWriter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: UpcomingEventsFeedController.java 948 2008-11-28 14:25:26Z cmmaj $
 *
 **/
public class UpcomingEventsFeedController implements Controller {

    public UpcomingEventsFeedController(final Map<String, String> config,
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
        LocalDate startDate = new LocalDate();
        LocalDate endDate = startDate.plusDays(periodInDays);
        List<Event> events = mainEventService.getEventsByDate(startDate, endDate);

        // create the base URI
        String baseUrl = requestUrl.substring(0, requestUrl.length() - UPCOMING.length());

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
    private static final String UPCOMING = "feeds/upcomingEvents.xml";
    private static final int FALL_BACK_DAYS = 10;
}
