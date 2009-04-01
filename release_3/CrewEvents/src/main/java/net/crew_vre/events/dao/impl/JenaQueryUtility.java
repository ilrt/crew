package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.Dataset;

/**
 * <p>We need to have the ability to intercept SPARQL queries before they are processed
 * by Jena. By defining an interface we can use Spring AOP to intercept the SPARQL.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol..ac.uk)
 * @version: $Id: JenaQueryUtility.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public interface JenaQueryUtility {

    /**
     * <p>A utility method that returns a Jena <code>QueryExecution</code> object - it handles the
     * common task of creating the query and executing it - but it also allows us to use AOP to
     * intercept the SPAQRL and data bindings if necessary.</p>
     *
     * @param sparql    the SPARQL query
     * @param dataset   the dataset to query with the SPARQL
     * @param qsm       the bindings for the query
     * @return the <code<QueryExecution object.
     */
    QueryExecution queryExecution(final String sparql, final Dataset dataset,
                                  QuerySolutionMap qsm);


    QueryExecution queryExecution(final String sparql, final Dataset dataset);

    /**
     * <p>A utility method that loads a SPARQL query from a file and places the contents in a
     * <code>String</code>
     *
     * @param sparqlPath    the path of the SPARQL file
     * @return a <code>String</code> holding the SPARQL
     */
    String loadSparql(final String sparqlPath);
}
