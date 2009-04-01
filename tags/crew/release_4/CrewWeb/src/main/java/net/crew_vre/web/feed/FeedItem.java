package net.crew_vre.web.feed;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 *
 **/
public class FeedItem {

    public FeedItem(String contentType, String feedUrl, String title) {
        this.contentType = contentType;
        this.feedUrl = feedUrl;
        this.title = title;

        System.out.println("> " + contentType + " " + feedUrl + " " + title);

    }

    public String getContentType() {
        return contentType;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getTitle() {
        return title;
    }

    private String contentType;
    private String feedUrl;
    private String title;
}
