package net.crew_vre.web.form;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: SparqlQueryForm.java 1005 2009-01-15 10:27:04Z cmmaj $
 */
public class SparqlQueryForm {

    public SparqlQueryForm() {
    }

    public SparqlQueryForm(String sparql) {
        this.sparql = sparql;
    }

    public String getSparql() {
        return sparql;
    }

    public void setSparql(String sparql) {
        this.sparql = sparql;
    }

    private String sparql;
}
