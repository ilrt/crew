package org.ilrt.dibden;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Utility.java 93 2008-09-09 14:51:41Z cmmaj $
 */
public class Utility {

    private Utility() {
    }

    public static int calculatePages(int total, int max) {
        Double val = Math.ceil((total - 1) / max);
        return (val.intValue() + 1);
    }

    public static int calculateRecord(int pageNo) {
        return (pageNo - 1) * 10;
    }
}
