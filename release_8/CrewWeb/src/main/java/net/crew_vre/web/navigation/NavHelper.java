package net.crew_vre.web.navigation;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class NavHelper {

    public NavHelper(final int totalResults, final int maxResultsPerPage, final int currentPage) {
        this.totalResults = totalResults;
        this.maxResultsPerPage = maxResultsPerPage;
        this.currentPage = currentPage;

        totalPages();
        boundaries();

    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getRecordsPageStart() {
        return ((currentPage - 1) * maxResultsPerPage) + 1;
    }

    public int getRecordsPageEnd() {

        int end = currentPage * maxResultsPerPage;

        if (end > totalResults) {
            end = totalResults;
        }

        return end;
    }

    public int getCurrentPage() {
        return currentPage;
    }

//    public void setCurrentPage(int currentPage) {
//        this.currentPage = currentPage;
//    }


    public boolean hasNext() {
        return next > 0;
    }

    public boolean hasPrev() {
        return prev > 0;
    }

    public int getPrevious() {
        return prev;
    }

    public int getNext() {
        return next;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    private void totalPages() {
        Double val = Math.ceil((totalResults - 1) / maxResultsPerPage);
        totalPages = (val.intValue() + 1);
    }

    private void boundaries() {

        if (totalPages <= PAGES_IN_NAV_LIMIT) {
            startPage = 1;
            endPage = totalPages;
        } else {

            startPage = currentPage - 5;
            endPage = currentPage + 5;


            if (startPage < 1) {
                startPage = 1;
            }

            if (startPage > (totalPages - (PAGES_IN_NAV_LIMIT))) {
                startPage = (totalPages - PAGES_IN_NAV_LIMIT);
            }

            if (endPage > totalPages) {
                endPage = totalPages;
            }

            if (endPage < PAGES_IN_NAV_LIMIT) {
                endPage = PAGES_IN_NAV_LIMIT;
            }


            // - do we have prev and next
            if (currentPage < totalPages) {
                next = currentPage + 1;
            }

            if (currentPage > 1) {
                prev = currentPage - 1;
            }

        }

    }


    private int totalResults;
    private int maxResultsPerPage;
    private int currentPage;

    private int totalPages;
    private int startPage;
    private int endPage;
    private int next = 0;
    private int prev = 0;


    private int PAGES_IN_NAV_LIMIT = 11;

}
