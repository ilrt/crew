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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: EventFeedWriter.java 1132 2009-03-20 19:05:47Z cmmaj $
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
    public void write(Writer writer, List<Event> events, String baseUrl, String feedUrl,
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
        for (Event event : events) {

            SyndEntry item = new SyndEntryImpl();
            String url = eventUrlBase + event.getId();
            item.setLink(url);
            item.setUri(url);
            item.setTitle(event.getTitle());
            item.setPublishedDate(new Date());
            item.setUpdatedDate(new Date());
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
