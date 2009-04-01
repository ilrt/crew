/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.crew_vre.web.controller.feeds;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventPart;
import net.crew_vre.web.facade.ListEventsFacade;
import net.crew_vre.web.feed.EventFeedWriter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RecentlyCreatedEventsFeedController.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class RecentlyCreatedEventsFeedController implements Controller {

    public RecentlyCreatedEventsFeedController(final Map<String, String> config,
                                               final ListEventsFacade listEventsFacade) {
        this.config = config;
        this.listEventsFacade = listEventsFacade;
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
        List<EventPart> events = listEventsFacade.displayRecentlyAddedEvents(periodInDays);

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
    private ListEventsFacade listEventsFacade;
    private static final String RECENTLY_ADDED = "feeds/recentlyAdded.xml";
    private static final int FALL_BACK_DAYS = 10;
}