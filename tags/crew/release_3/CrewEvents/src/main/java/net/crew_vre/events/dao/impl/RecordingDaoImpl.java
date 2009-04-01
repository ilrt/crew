package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import net.crew_vre.events.dao.RecordingDao;
import net.crew_vre.events.domain.Recording;
import net.crew_vre.events.domain.Screen;
import net.crew_vre.jena.query.DatasetFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is an implementation of <code>RecordingDao</code> that uses the Jena
 * Sematic Web Framework. SPARQL is used for querying the underlying RDF model.</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: RecordingDaoImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class RecordingDaoImpl implements RecordingDao {


    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param datasetFactory the factory that provides the dataset
     * @param jqUtility      utility that helps with Jena queries
     */
    public RecordingDaoImpl(final DatasetFactory datasetFactory, final JenaQueryUtility jqUtility) {
        this.dataset = datasetFactory.create();
        this.jqUtility = jqUtility;

        this.sparqlFindRecording = jqUtility.loadSparql("/sparql/recording-details.rq");
        this.sparqlFindScreenshots = jqUtility.loadSparql("/sparql/recording-screenshots.rq");


    }


    public Recording findRecordingById(final String id) {

        Recording recording = null;

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(id));

        // setup and execute the query
        QueryExecution qe = jqUtility.queryExecution(sparqlFindRecording, dataset, initialBindings);
        ResultSet rs = qe.execSelect();

        // there should only be one...
        while (rs.hasNext()) {

            recording = new Recording();

            QuerySolution qs = rs.nextSolution();

            if (qs.getResource("id") != null) {
                recording.setId(qs.getResource("id").getURI());
            }

            if (qs.getResource("eventId") != null) {
                recording.setEventId(qs.getResource("eventId").getURI());
            }

            if (qs.getResource("videoUrl") != null) {
                recording.setVideoUrl(qs.getResource("videoUrl").getURI());
            }

            if (qs.getResource("eventId") != null) {
                recording.setScreenUrl(qs.getResource("screenUrl").getURI());
            }

            if (qs.getLiteral("startTime") != null) {
                recording.setStartTime(qs.getLiteral("startTime").getLong());
            }

            if (qs.getLiteral("endTime") != null) {
                recording.setEndTime(qs.getLiteral("endTime").getLong());
            }

            recording.setScreens(findScreens(recording.getId()));
        }

        qe.close();

        return recording;
    }


    private List<Screen> findScreens(final String recordingId) {

        List<Screen> screens = new ArrayList<Screen>();

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("recordingId", ModelFactory.createDefaultModel()
                .createResource(recordingId));

        // setup and execute the query
        QueryExecution qe = jqUtility.queryExecution(sparqlFindScreenshots, dataset, initialBindings);
        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {

            Screen screen = new Screen();

            QuerySolution qs = rs.nextSolution();

            if (qs.getResource("subEvent") != null) {
                screen.setId(qs.getResource("subEvent").getURI());
            }

            if (qs.getLiteral("startTime") != null) {
                screen.setStartTime(qs.getLiteral("startTime").getLong());
            }

            if (qs.getLiteral("endTime") != null) {
                screen.setEndTime(qs.getLiteral("endTime").getLong());
            }

            if (qs.getLiteral("screen") != null) {
                screen.setImageName(qs.getLiteral("screen").getLexicalForm());
            }

            screens.add(screen);
        }

        qe.close();

        return screens;
    }


    // sparql to find recording
    /**
     private final String sparql = new StringBuilder().append("")
     .append("SELECT $id $eventId $videoUrl $screenUrl $startTime $endTime\n")
     .append("WHERE {\n")
     .append("$id rdf:type crew:Recording .\n")
     .append("$id crew:isRecordingAt $eventId .\n")
     .append("$id crew:hasVideoFile $videoUrl .\n")
     .append("$id crew:hasScreenFile $screenUrl .\n")
     .append("$id memetic:has-media-start-time $startTime .\n")
     .append("$id memetic:has-media-end-time $endTime\n")
     .append("}")
     .toString();

     // sparql to find screen shots
     private final String sparqlScreens = new StringBuilder().append("")
     .append("SELECT $subEvent $startTime $endTime $screen\n")
     .append("WHERE {\n")
     .append("$recordingId portal:has-sub-event $subEvent .\n")
     .append("$subEvent rdf:type <http://www.memetic-vre.net/ontologies/")
     .append("memetic-20050106-1#Screen> .\n")
     .append("$subEvent memetic:has-media-start-time $startTime .\n")
     .append("$subEvent memetic:has-media-end-time $endTime .\n")
     .append("$subEvent memetic:has-screen-capture $screen .\n")
     .append("}\n")
     .append("ORDER BY $startTime\n")
     .toString();
     **/

    private String sparqlFindRecording;
    private String sparqlFindScreenshots;

    private final Dataset dataset;
    private JenaQueryUtility jqUtility;
}
