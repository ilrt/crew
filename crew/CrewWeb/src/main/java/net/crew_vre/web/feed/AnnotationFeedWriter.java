/**
 * Copyright (c) 2008-20010, University of Bristol
 * Copyright (c) 2008-2010, University of Manchester
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

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EventFeedWriter.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class AnnotationFeedWriter {

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
    private Logger logger = Logger.getLogger("net.crew_vre.web.feed.AnnotationFeedWriter");

    public void write(Writer writer, String eventId, List<HashMap<String,String>> annotations, String baseUrl,
            String feedUrl, Map<String, String> config)
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
        if (annotations != null) {
            for (HashMap<String,String> annotation : annotations) {

                SyndEntry item = new SyndEntryImpl();
                String annotationUrl = annotation.get("annotationUrl");
                item.setUri(annotationUrl);
                
                // annotation url returns RDF so not suitable for link - use a link to the web site event page
                // (which may not work very well in the mobile browser!)
                String eventUrl = baseUrl + "displayEvent.do?eventId=" + eventId;
                item.setLink(eventUrl);

                item.setTitle(annotation.get("commentTitle"));

                // Sets dc:date in feed item
                Date publishedDate = null;
                try {
                    //DateFormat df = DateFormat.getDateTimeInstance();
                    //df.setLenient(true);
                    //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    String createdDateTime = annotation.get("createdDateTime");

                    // Having problems parsing date - removing time zone pattern ("+01:00")
                    createdDateTime = createdDateTime.substring(0, createdDateTime.lastIndexOf("+"));
                    publishedDate = df.parse(createdDateTime);
                    item.setPublishedDate(publishedDate);
                } catch (ParseException pe) {
                    // Incorrect date format - just add current date
                    if (logger.isDebugEnabled()) {
                            logger.debug("Error parsing creation date: " + pe.getMessage());
                    }
                    item.setPublishedDate(new Date());
                }

                // Set author
                String authorURI = annotation.get("authorUri");
                if (authorURI != null && !authorURI.equals("")) {
                    //URI takes the form: http://localhost:9090/CrewWeb/annotation/person/phil/
                    // Remove final forward slash
                    String author = authorURI.substring(0, authorURI.length()-1);
                    author = author.substring(author.lastIndexOf("/")+1, author.length());
                    item.setAuthor(author);
                }

                SyndContent content = new SyndContentImpl();
                content.setValue(annotation.get("commentDescription"));
                item.setDescription(content);
                items.add(item);
            }
        }

        feed.setEntries(items);

        // write that feed!
        WireFeedOutput output = new WireFeedOutput();
        WireFeed wireFeed = feed.createWireFeed();
        output.output(wireFeed, writer);
    }
}
