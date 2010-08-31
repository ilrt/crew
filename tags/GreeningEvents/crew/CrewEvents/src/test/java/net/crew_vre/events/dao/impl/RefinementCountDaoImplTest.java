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
import net.crew_vre.events.dao.RefinementDao;
import net.crew_vre.events.domain.facet.Refinement;

import java.io.IOException;
import java.util.List;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.Data;
import org.caboto.jena.db.impl.FileDatabase;

/**
 * @author Mike Jones (mike.a.jones@gmail.com)
 * @version $Id: RefinementCountDaoImplTest.java 1189 2009-03-31 13:14:53Z cmmaj $
 */
public class RefinementCountDaoImplTest extends TestCase {

    private RefinementDao dao;

    @Override
    public void setUp() {
        try {
            database = new FileDatabase(Constants.DEFAULT_MODEL,
                    Constants.NAMED_GRAPHS);
            dao = new RefinementDaoImpl();


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void testCountRefinementAlphaNumericWithEventType() throws Exception {

        String type = "http://www.ilrt.bristol.ac.uk/iugo#MainEvent";
        String fragment = new StringBuilder()
                .append("?id <http://purl.org/dc/elements/1.1/title> ?name .\n")
                .append("FILTER (regex(str(?name), \"^t\", \"i\"))").toString();

        int count = dao.countRefinements(fragment, type, null, database.getData()).size();

        assertEquals("There should be 1 event refinement", 1, count);
    }

    public void testCountRefinementAlphaNumericWithPersonType() throws Exception {

        String type = "http://xmlns.com/foaf/0.1/Person";
        String fragment = new StringBuilder()
                .append("?id <http://xmlns.com/foaf/0.1/family_name> ?name .\n")
                .append("FILTER (regex(str(?name), \"^b\", \"i\"))").toString();

        int count = dao.countRefinements(fragment, type, null, database.getData()).size();

        assertEquals("There should be 1 person refinement", 1, count);
    }

    public void testLocationNames() throws Exception {

        String widerProperty = "http://www.w3.org/2004/02/skos/core#broader";
        String rootConcept = "http://www.ilrt.bristol.ac.uk/iugo/location/#locations";

        List<Refinement> refinements = dao.findNames(widerProperty, rootConcept,
                database.getData());

        //for (Refinement refinement : refinements) {
        //    System.out.println("> " + refinement.getName());
        //}

        assertNotNull("The list of refinements should not be null", refinements);
        assertEquals("There should be 7 continents", 7, refinements.size());

    }

    Database database;

}
