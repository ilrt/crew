package net.crew_vre.web.navigation;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class NavHelper {

    public NavHelper(final int totalResults, final int maxResultsPerPage, final int currentPage) {
        this.totalResults = totalResults;
        this.maxResultsPerPage = maxResultsPerPage;
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        Double val = Math.ceil((totalResults - 1) / maxResultsPerPage);
        return (val.intValue() + 1);
    }

    public int getRecordsPageStart() {
        return ((currentPage -1 ) * maxResultsPerPage) + 1;
    }

    public int getRecordsPageEnd() {

        int end =  currentPage * maxResultsPerPage;

        if (end > totalResults) {
            end = totalResults;
        }

        return end;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    private int totalResults;
    private int maxResultsPerPage;
    private int currentPage;
}
