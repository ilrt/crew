package net.crew_vre.events;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id$
 */
public class Utility {

    /**
     * Private constrructor since it just has static methods.
     */
    private Utility() { }

    /**
     * <p>Utility to convert a date string into a DateTime instance.
     *
     * @param stringDate the date in string format
     * @return a DateTime represented by the string
     * @throws ParseException if there is an error parsing the string
     */
    public static DateTime parseString(String stringDate) throws ParseException {
        return ISODateTimeFormat.dateTimeParser().withOffsetParsed().parseDateTime(stringDate);
    }

}
