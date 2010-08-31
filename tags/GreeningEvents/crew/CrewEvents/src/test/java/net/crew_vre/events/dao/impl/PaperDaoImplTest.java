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
package net.crew_vre.events.dao.impl;

import junit.framework.TestCase;
import net.crew_vre.events.Constants;
import net.crew_vre.events.dao.PaperDao;
import net.crew_vre.events.domain.Paper;

import java.io.IOException;
import java.util.List;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.FileDatabase;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: PaperDaoImplTest.java 1189 2009-03-31 13:14:53Z cmmaj $
 */
public class PaperDaoImplTest extends TestCase {

    private PaperDao paperDao;

    @Override
    public void setUp() {
        try {
            Database database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);

            paperDao = new PaperDaoImpl(database);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void testPaperById() {
        Paper paper = paperDao.findPaperById(Constants.PAPER_ONE_ID);
        assertNotNull("The paper should not be null", paper);
        assertNotNull("The graph should not be null", paper.getGraph());
        assertNotNull("The title should not be null", paper.getTitle());
        assertNotNull("The description should not be null", paper.getTitle());
        assertTrue("The paper is retrievable", paper.isRetrievable());
    }

    public void testFindPapersRelatedToEvent() {
        List<Paper> results = paperDao.findPapersRelatedToEvent(Constants.EVENT_TWO_ID);
        assertNotNull("The results should not be null", results);
        assertEquals("There should be two paper", 2, results.size());

        for (Paper p : results) {
            assertNotNull("The graph should not be null", p.getGraph());
        }
    }

}
