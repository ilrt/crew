package net.crew_vre.web.history;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A class used to keep track of what the user has browsed in the application.
 * The class is used by Spring MVC controllers that are used to find certain
 * artefacts such as events, people and places.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 * @see History
 */
public class BrowseHistory {

    /**
     * <p>Add a browse request to the history.</p>
     *
     * @param request the request is used to get the session object which will hold the
     *                browse history. It is also used to get the request URL.
     * @param title   the name of the artefact visited, such as the title of an event.
     */
    @SuppressWarnings("unchecked")
    public void addHistory(HttpServletRequest request, String title) {

        // get the browse history
        HttpSession session = request.getSession(true);

        List browseHistory = (List) session.getAttribute("browseHistory");

        if (browseHistory == null) {

            if (logger.isInfoEnabled()) {
                logger.info("No browse history. Creating a new one.");
            }

            browseHistory = new ArrayList<History>();
        }

        // get the request path
        StringBuffer url = request.getRequestURL();

        if (request.getQueryString() != null) {
            url.append("?").append(request.getQueryString());
        }

        // add the request path to the history
        if (browseHistory.size() > 0) {
            History history = (History) browseHistory.get(0);
            if (!history.getPath().equals(url.toString())) {
                browseHistory.add(0, new History(url.toString(), title));
            }
        } else {
            browseHistory.add(0, new History(url.toString(), title));
        }

        // update the session
        session.setAttribute("browseHistory", browseHistory);
    }

    /**
     * <p>Delete the history from the session.</p>
     *
     * @param request http request holding the session
     */
    public void clearHistory(HttpServletRequest request) {

        HttpSession session = request.getSession();

        if (session != null) {
            session.removeAttribute("browseHistory");
        }
    }

    /**
     * <p>Method that keeps track of the request that created the search. It
     * is used in the navigation to return to the search results.</p>
     *
     * @param request the http request
     */
    public void addSearchHistory(HttpServletRequest request) {

        // get the uri -> the request that creates the search
        StringBuffer url = request.getRequestURL();

        // add the query string if there is one
        if (request.getQueryString() != null) {
            url.append("?").append(request.getQueryString());
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("resultsUrl", url);
    }


    private Logger logger = Logger.getLogger("net.crew_vre.web.history.BrowseHistory");
}
