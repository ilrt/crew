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
