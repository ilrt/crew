/**
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
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

package net.crew_vre.recorder;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.impl.SDBDatabase;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;

public class Test {

    public static void main(String[] args) throws Exception {
        Database database = new SDBDatabase(
            "jdbc:derby:/home/ts23/CREW-replace/Crew/CrewWeb/target/DB/Caboto",
            null, null, "derby", "layout2");
        Results results = database.executeSelectQuery(
            "SELECT ?g ?r ?p ?o WHERE { GRAPH ?g { ?r ?p ?o } } ORDER BY ?g ?r ?p",
            new QuerySolutionMap());
        ResultSet res = results.getResults();
        while (res.hasNext()) {
            QuerySolution solution = res.nextSolution();
            System.err.println(solution.get("g") + " " + solution.get("r")
                    + " " + solution.get("p") + " " + solution.get("o"));
        }

        Results results2 = database.executeSelectQuery(
                "SELECT ?r ?p ?o WHERE { ?r ?p ?o }",
                new QuerySolutionMap());
        ResultSet res2 = results2.getResults();
        while (res2.hasNext()) {
            QuerySolution solution = res2.nextSolution();
            System.err.println(solution.get("r")
                    + " " + solution.get("p") + " " + solution.get("o"));
        }
    }
}
