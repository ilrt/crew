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
package net.crew_vre.web.navigation;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: NavHelper.java 1191 2009-03-31 13:38:51Z cmmaj $
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
