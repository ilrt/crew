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
package net.crew_vre.web.feed;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.EventPart;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EventFeedWriter.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class EventFeedWriter {

    /**
     *
     * @param writer            the writer to output the feed.
     * @param events            the list of events that will be processed.
     * @param baseUrl           the base URL of the web application
     * @param feedUrl           the url of the feed
     * @param eventUrlBase      the base URL used to link to the event
     * @param config            map holding config details for the feed, such as the title and
     *                          description.
     * @throws FeedException    if there is an error.
     * @throws IOException      if there is an error.
     */
    public void write(Writer writer, List<EventPart> events, String baseUrl, String feedUrl,
                      String eventUrlBase, Map<String, String> config)
            throws FeedException, IOException {

        // create the feed object
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(config.get("format"));
        feed.setLanguage(config.get("language"));
        feed.setTitle(config.get("title"));
        feed.setDescription(config.get("descriptiom"));
        feed.setLink(baseUrl);
        feed.setUri(feedUrl);
        feed.setPublishedDate(new Date());

        // create link
        SyndLink syndLink = new SyndLinkImpl();
        syndLink.setHref(feed.getUri());
        syndLink.setRel("self");
        feed.setLinks(Collections.singletonList(syndLink));

        // list of enteries
        ArrayList<SyndEntry> items = new ArrayList<SyndEntry>();

        // iterate over the events
        for (EventPart event : events) {

            SyndEntry item = new SyndEntryImpl();
            String url = eventUrlBase + event.getId();
            item.setLink(url);
            item.setUri(url);
            item.setTitle(event.getTitle());
            // Sets dc:date in feed item
            item.setPublishedDate(event.getStartDate().toDateTimeAtStartOfDay().toDate());
            // item.setUpdatedDate(new Date());
            SyndContent content = new SyndContentImpl();
            content.setValue(event.getDescription());
            item.setDescription(content);
            items.add(item);
        }

        feed.setEntries(items);

        // write that feed!
        WireFeedOutput output = new WireFeedOutput();
        WireFeed wireFeed = feed.createWireFeed();
        output.output(wireFeed, writer);
    }
}
