package net.crew_vre.web.form;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: SparqlQueryForm.java 1132 2009-03-20 19:05:47Z cmmaj $
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
