package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: JenaQueryUtilityImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class JenaQueryUtilityImpl implements JenaQueryUtility {

    public JenaQueryUtilityImpl() {
    }

    public QueryExecution queryExecution(final String sparql, final Dataset dataset,
                                         final QuerySolutionMap qsm) {

        if (qsm != null) {
            return QueryExecutionFactory.create(sparql, dataset, qsm);
        } else {
            return QueryExecutionFactory.create(sparql, dataset);
        }
    }

    public QueryExecution queryExecution(final String sparql, final Dataset dataset) {
        return QueryExecutionFactory.create(sparql, dataset);
    }

    public String loadSparql(final String sparqlPath) {

        StringBuffer buffer = new StringBuffer();

        try {

            InputStream is = getClass().getResourceAsStream(sparqlPath);
            BufferedReader d = new BufferedReader(new InputStreamReader(is));

            String s;

            while ((s = d.readLine()) != null) {
                buffer.append(s);
                buffer.append("\n");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return buffer.toString();
    }
}
