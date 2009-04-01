package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import net.crew_vre.events.dao.PaperDao;
import net.crew_vre.events.domain.Paper;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.vocabulary.IUGO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: PaperDaoImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class PaperDaoImpl implements PaperDao {

    public PaperDaoImpl(final DatasetFactory datasetFactory, JenaQueryUtility jqUtility) {
        this.dataset = datasetFactory.create();
        this.jqUtility = jqUtility;

        this.sparqlPaperTitle = jqUtility.loadSparql("/sparql/paper-details.rq");
        this.sparqlRelatedToEvents =
                jqUtility.loadSparql("/sparql/paper-details-related-to-event.rq");
    }

    public Paper findPaperById(final String id) {

        Paper paper = null;

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(id));

        QueryExecution qe = jqUtility.queryExecution(sparqlPaperTitle, dataset, initialBindings);
        ResultSet rs = qe.execSelect();

        // there should only be one...
        while (rs.hasNext()) {
            paper = paperDetails(rs.nextSolution());
        }

        qe.close();

        return paper;
    }

    public List<Paper> findPapersRelatedToEvent(String eventId) {

        List<Paper> results = new ArrayList<Paper>();

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("eventId", ModelFactory.createDefaultModel().createResource(eventId));

        QueryExecution qe = jqUtility.queryExecution(sparqlRelatedToEvents, dataset,
                initialBindings);
        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            results.add(paperDetails(rs.nextSolution()));
        }

        qe.close();

        return results;
    }


    private Paper paperDetails(QuerySolution qs) {

        Paper paper = new Paper();

        Resource resource = qs.getResource("id");
        paper.setId(resource.getURI());

        if (qs.getResource("graph") != null) {
            paper.setGraph(qs.getResource("graph").getURI());
        }

        // get the title
        if (qs.getLiteral("title") != null) {
            paper.setTitle(qs.getLiteral("title").getLexicalForm());
        }

        // get the description
        if (qs.getLiteral("description") != null) {
            paper.setDescription(qs.getLiteral("description").getLexicalForm());
        }

        // get the type
        if (qs.getResource("type") != null) {
            if (qs.getResource("type").equals(IUGO.Retrievable)) {
                paper.setRetrievable(true);
            }
        }
    
    return paper;
}

// sparql - get the title of an event
private String sparqlPaperTitle;

// sparql - papers related to events
private String sparqlRelatedToEvents;

/**
 * The dataset that is queried
 */
private final Dataset dataset;

/**
 * The utility used with Jena queries
 */
private final JenaQueryUtility jqUtility;

}
