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
package net.crew_vre.web.controller;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import net.crew_vre.web.form.SparqlQueryForm;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: SparqlQueryController.java 1191 2009-03-31 13:38:51Z cmmaj $
 */
public class SparqlQueryController extends SimpleFormController {

    public SparqlQueryController(Database database) {
        this.database = database;
    }

    public ModelAndView onSubmit(Object command, BindException errors) throws Exception {

        ModelAndView mav = new ModelAndView("sparql");

        SparqlQueryForm form = null;

        List<List> results = new ArrayList<List>();

        if (command != null) {

            form = (SparqlQueryForm) command;

            Results _results = database.executeSelectQuery(form.getSparql(), null);

            ResultSet resultset = _results.getResults();

            List vars = resultset.getResultVars();

            while (resultset.hasNext()) {

                List<String> row = new ArrayList<String>();

                QuerySolution qs = (QuerySolution) resultset.next();

                for (Object var : vars) {

                    RDFNode node = qs.get((String) var);

                    if (node == null) {
                        row.add(null);
                    } else {
                        row.add(node.toString());
                    }
                }

                results.add(row);
            }

            _results.close();

            mav.addObject("vars", vars);
            mav.addObject("results", results);
        }


        mav.addObject("sparql", form);
        return mav;
    }

    private Database database;


}
