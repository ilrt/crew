package net.crew_vre.web.history;

/**
 * <p>The class is used to encapsulate a request for an item in the web application. It
 * used to keep track of a user's browse history.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: History.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class History {

    public History(String path, String title) {
        this.title = title;
        this.path = path;
    }

    private String title;

    private String path;

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof History)) {
            return false;
        }

        History other = (History) o;

        return this.getPath().equals(other.getPath());
    }

    public int hashCode() {
        return this.getPath().hashCode();
    }

}
