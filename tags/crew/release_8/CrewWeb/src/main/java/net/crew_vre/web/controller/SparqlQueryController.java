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
 * @version $Id: SparqlQueryController.java 1033 2009-02-19 10:13:57Z cmmaj $
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
