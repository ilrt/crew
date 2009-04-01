package net.crew_vre.domain;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk) and Andrew G D Rowley
 * @version $Id: DomainObject.java 793 2008-07-07 14:45:19Z cmmaj $
 */
public abstract class DomainObject {

    // The graph containing the object
    private String graph;

    // The RDF url of the object
    private String uri = null;

    /**
     * Gets the graph
     * @return The graph
     */
    public String getGraph() {
        return graph;
    }

    /**
     * Sets the graph
     * @param graph The graph to set
     */
    public void setGraph(String graph) {
        this.graph = graph;
    }

    /**
     * Returns the uri
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the uri
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the value of a variable using an RDF Node
     * @param variable The variable to set the value of
     * @param node The value to set to
     * @throws UnknownVariableException if the variable cannot be set
     */
    public void setVariable(String variable, RDFNode node)
            throws UnknownVariableException {
        Class cls = getClass();
        String setMethod = "set" + variable.substring(0, 1).toUpperCase()
            + variable.substring(1);
        Class paramType = null;
        Object value = null;
        if (node.isLiteral()) {
            Literal literal = (Literal) node;
            RDFDatatype datatype = literal.getDatatype();
            paramType = datatype.getJavaClass();
            value = literal.getValue();
        } else {
            paramType = String.class;
            value = ((Resource) node).getURI();
        }
        try {
            Method method = cls.getMethod(setMethod,
                    new Class[]{paramType});
            method.invoke(this, new Object[]{value});
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnknownVariableException();
        }
    }

    /**
     * Fills in the details of an object from a solution
     *
     * @param solution The solution to the query
     * @param vars The variables to set
     */
    public void fillIn(QuerySolution solution, List<String> vars) {
        Iterator<String> iterator = vars.listIterator();
        while (iterator.hasNext()) {
            String var = iterator.next();
            RDFNode value = solution.get(var);
            try {
                setVariable(var, value);
            } catch (UnknownVariableException e) {
                e.printStackTrace();
            }
        }
    }
}

