package net.crew_vre.events;

import junit.framework.TestCase;
import org.joda.time.LocalDate;
import org.junit.Test;

public class UtilityTest extends TestCase {

    @Test
    public void testLocalDate() throws Exception {

        String dateOne = "2008-11-24";
        LocalDate localDateOne = Utility.parseStringToLocalDate(dateOne);

        assertEquals("Incorrect year", 2008, localDateOne.getYear());
        assertEquals("Incorrect month", 11, localDateOne.getMonthOfYear());
        assertEquals("Incorrect date", 24, localDateOne.getDayOfMonth());

        String dateTwo = "2008-12-31";
        LocalDate localDayteTwo = Utility.parseStringToLocalDate(dateTwo);

        assertEquals("Incorrect year", 2008, localDayteTwo.getYear());
        assertEquals("Incorrect month", 12, localDayteTwo.getMonthOfYear());
        assertEquals("Incorrect date", 31, localDayteTwo.getDayOfMonth());

    }


}
