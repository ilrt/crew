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

package net.crew_vre.recordings.dao.impl;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.crew_vre.domain.JenaFiller;
import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.media.rtptype.RtpTypeRepository;
import net.crew_vre.recordings.Utility;
import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.ReplayLayout;
import net.crew_vre.recordings.domain.ReplayLayoutPosition;
import net.crew_vre.recordings.domain.Stream;
import net.crew_vre.recordings.layout.LayoutRepository;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Implements the Recording Data Access Object
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class RecordingDaoImpl implements RecordingDao {

    // The file containing the sparql query to find a recording
    private static final String FIND_RECORDING_SPARQL_FILE =
        "/sparql/findRecording.rq";

    // The file containing the sparql query to find a recording
    private static final String FIND_RECORDING_OF_EVENT_SPARQL_FILE =
        "/sparql/findRecordingOfEvent.rq";

    // The file containing the sparql query to find streams of a recording
    private static final String FIND_STREAMS_SPARQL_FILE =
        "/sparql/findStreams.rq";

    // The file containing the sparql query to find Layouts of a recording
    private static final String FIND_LAYOUTS_SPARQL_FILE =
        "/sparql/findLayouts.rq";

    // The file containing the sparql query to find streams of a recording layout
    private static final String FIND_LAYOUTPOSTIONS_SPARQL_FILE =
        "/sparql/findLayoutPositions.rq";

    private static final String SLASH = System.getProperty("file.separator");

    private RtpTypeRepository rtpTypeRepository;

    private LayoutRepository layoutRepository;

    // The directory where the recordings are stored
    private String recordingsDirectory = null;

    // The database implementation
    private Database database = null;

    // The recording sparql query
    private String findRecordingSparql = null;

    // The recording of event sparql query
    private String findRecordingOfEventSparql = null;

    // The streams sparql query
    private String findStreamsSparql = null;

    // The layout sparql query
    private String findLayoutsSparql = null;

    // The layout position sparql query
    private String findLayoutPositionsSparql = null;

    /**
     * Creates a new RecordingDaoImpl
     *
     * @param database The database
     * @param recDirectory The recording directory
     * @param rtpTypeRepository The RTP Type repository
     * @param layoutRepository The Layout repository
     */
    public RecordingDaoImpl(final Database database,
            final String recDirectory,
            final RtpTypeRepository rtpTypeRepository,
            final LayoutRepository layoutRepository) {
        init(database, recDirectory, rtpTypeRepository, layoutRepository);
    }

    /**
     * Creates a new RecordingDaoImpl
     *
     * @param configFile The configuration file containing the recording
     *                   location
     * @param database The database
     * @param rtpTypeRepository The RTP Type repository
     * @param layoutRepository The layout repository
     * @throws IOException If the configuration file can't be read
     */
    public RecordingDaoImpl(final String configFile,
            final Database database, final RtpTypeRepository rtpTypeRepository,
            final LayoutRepository layoutRepository) throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream(configFile));
        init(database, properties.getProperty("recordings.location"),
                rtpTypeRepository, layoutRepository);
    }

    private void init(final Database database, final String recDirectory,
            final RtpTypeRepository rtpTypeRepository,
            final LayoutRepository layoutRepository) {
        this.database = database;
        recordingsDirectory = recDirectory;
        this.rtpTypeRepository = rtpTypeRepository;
        this.layoutRepository = layoutRepository;
        if (!recordingsDirectory.endsWith(SLASH)) {
            recordingsDirectory += SLASH;
        }

        // Read the sparql queries
        findRecordingSparql = Utils.loadSparql(
            FIND_RECORDING_SPARQL_FILE);
        findRecordingOfEventSparql = Utils.loadSparql(
                FIND_RECORDING_OF_EVENT_SPARQL_FILE);
        findStreamsSparql = Utils.loadSparql(
                FIND_STREAMS_SPARQL_FILE);
        findLayoutsSparql = Utils.loadSparql(
                FIND_LAYOUTS_SPARQL_FILE);
        findLayoutPositionsSparql = Utils.loadSparql(
                FIND_LAYOUTPOSTIONS_SPARQL_FILE);
    }


    private Recording findRecording(QuerySolutionMap initialBindings) {
        // Execute the query;
        Results results = database.executeSelectQuery(findRecordingSparql,
                initialBindings);
        ResultSet resultSet = results.getResults();

        // Put the results into a new recording
        if (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            Recording recording = new Recording();
            JenaFiller.fillIn(recording, solution, resultSet.getResultVars());

            // Set the recording directory
            recording.setDirectory(recordingsDirectory
                    + recording.getId() + SLASH);

            // Get the recording's streams
            initialBindings = new QuerySolutionMap();
            initialBindings.add("recuri", ResourceFactory.createResource(
                    recording.getUri()));
            Results streamResults = database.executeSelectQuery(
                    findStreamsSparql, initialBindings);
            ResultSet streamResultSet = streamResults.getResults();
            List<Stream> streams = new Vector<Stream>();
            while (streamResultSet.hasNext()) {
                solution = streamResultSet.nextSolution();
                Stream stream = new Stream(rtpTypeRepository);
                JenaFiller.fillIn(stream, solution,
                        streamResultSet.getResultVars());
                stream.setFile(recording.getDirectory() + stream.getSsrc());
                streams.add(stream);
            }
            streamResults.close();
            recording.setStreams(streams);

            // Get the recording's layouts
            initialBindings = new QuerySolutionMap();
            initialBindings.add("recuri", ResourceFactory.createResource(
                    recording.getUri()));
            Results layoutResults = database.executeSelectQuery(
                    findLayoutsSparql, initialBindings);
            ResultSet layoutResultSet = layoutResults.getResults();
            List<ReplayLayout> replayLayouts = new Vector<ReplayLayout>();
            while (layoutResultSet.hasNext()) {
                solution = layoutResultSet.nextSolution();
                ReplayLayout replayLayout = new ReplayLayout(layoutRepository);
                JenaFiller.fillIn(replayLayout, solution,
                        layoutResultSet.getResultVars());
                replayLayout.setRecording(recording);
                replayLayouts.add(replayLayout);
                findlayoutPositions(replayLayout);
            }
            layoutResults.close();
            recording.setReplayLayouts(replayLayouts);
            results.close();

            // Return the recording
            return recording;
        }

        results.close();

        // If there are no results, return null
        return null;
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#findRecordingById(
     *     java.lang.String)
     */
    public Recording findRecordingById(final String id) {

        // Set the known variables
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ResourceFactory.createPlainLiteral(id));
        return findRecording(initialBindings);
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#findRecordingByUri(
     *     java.lang.String)
     */
    public Recording findRecordingByUri(final String uri) {
        // Set the known variables
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("uri", ResourceFactory.createResource(uri));
        return findRecording(initialBindings);
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#findRecordingByStreamUri(
     *    java.lang.String)
     */
    public Recording findRecordingByStreamUri(String streamUri) {

        // Set the known variables
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("uri", ResourceFactory.createResource(
                streamUri));

        // Execute the query;
        Results streamResults = database.executeSelectQuery(findStreamsSparql,
                initialBindings);
        ResultSet streamResultSet = streamResults.getResults();
        String recordingUri = null;
        while (streamResultSet.hasNext()) {
            QuerySolution solution = streamResultSet.nextSolution();
            recordingUri = solution.get("recuri").toString();
        }
        streamResults.close();
        if (recordingUri != null) {
            Recording recording = findRecordingByUri(recordingUri);
            return recording;
        }
        return null;
    }

    private List<ReplayLayoutPosition> findlayoutPositions(
            ReplayLayout replayLayout) {
        // Set the known variables
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("layoutUri", ResourceFactory.createResource(
                replayLayout.getUri()));
        Results results = database.executeSelectQuery(findLayoutPositionsSparql,
                initialBindings);
        ResultSet resultSet = results.getResults();
        List<ReplayLayoutPosition> replayLayoutPositions =
            new Vector<ReplayLayoutPosition>();
        while (resultSet.hasNext()) {
             QuerySolution solution = resultSet.nextSolution();
             String position = solution.getLiteral("name").getLexicalForm();
             String streamUri = solution.getResource("stream").getURI();
             Stream stream = null;
             for (Stream str : replayLayout.getRecording().getStreams()) {
                 if (str.getUri().equals(streamUri)) {
                     stream = str;
                     break;
                 }
             }
             if (stream != null) {
                 replayLayout.setStream(position, stream);
             }
        }
        results.close();
        return replayLayoutPositions;
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#addRecording(
     *     net.crew_vre.recordings.domain.Recording)
     */
    public void addRecording(Recording recording) {

        try {
            Model model = Utility.getModelForRecording(recording);
            database.addModel(recording.getGraph(), model);

            if (recording.getStreams() != null) {
                for (Stream stream : recording.getStreams()) {
                    addStream(stream);
                }
            }
            if (recording.getReplayLayouts() != null) {
                for (ReplayLayout layout : recording.getReplayLayouts()) {
                    addReplayLayout(layout);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#deleteRecording(
     *     net.crew_vre.recordings.domain.Recording)
     */
    public void deleteRecording(Recording recording) {

        Model model = Utility.getModelForRecording(recording);
        database.deleteModel(recording.getGraph(), model);

        if (recording.getStreams() != null) {
            for (Stream stream : recording.getStreams()) {
                deleteStream(stream);
            }
        }
        if (recording.getReplayLayouts() != null) {
            for (ReplayLayout layout : recording.getReplayLayouts()) {
                deleteLayout(layout);
            }
        }
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#deleteStream(
     * net.crew_vre.recordings.domain.Stream)
     */
    public void deleteStream(Stream stream) {
        Model model = Utility.createModelForStream(stream);
        database.deleteModel(stream.getGraph(), model);
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#addStream(
     *     net.crew_vre.recordings.domain.Stream)
     */
    public void addStream(Stream stream) {
        Model model = Utility.createModelForStream(stream);
        database.addModel(stream.getGraph(), model);
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#deleteLayout(
     *     net.crew_vre.recordings.domain.ReplayLayout)
     */
    public void deleteLayout(ReplayLayout replayLayout) {
        database.deleteModel(replayLayout.getGraph(),
                Utility.createModelForLayout(replayLayout));
    }

    private void removeLayoutWithSameTimeAndDiffName(
            ReplayLayout replayLayout) {
        Recording recording = replayLayout.getRecording();
        List<ReplayLayout> replayLayouts = recording.getReplayLayouts();
        for (ReplayLayout repLayout : replayLayouts) {
            if (repLayout.getTime() == replayLayout.getTime()) {
                if (!repLayout.getName().equals(replayLayout.getName())) {
                    deleteLayout(repLayout);
                }
            }
        }
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#addReplayLayout(
     *     net.crew_vre.recordings.domain.ReplayLayout)
     */
    public void addReplayLayout(ReplayLayout replayLayout) {
        removeLayoutWithSameTimeAndDiffName(replayLayout);
        database.addModel(replayLayout.getGraph(),
                Utility.createModelForLayout(replayLayout));
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#findRecordingsOfEvent(
     *     java.lang.String)
     */
    public List<Recording> findRecordingsOfEvent(String eventId) {

        // Set the known variables
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("eventUri",
                ResourceFactory.createResource(eventId));

        // Execute the query;
        Results results = database.executeSelectQuery(
                findRecordingOfEventSparql, initialBindings);
        ResultSet resultSet = results.getResults();

        Vector<Recording> recordings = new Vector<Recording>();

        // Put the results into a new recording
        while (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            String recordingUri = solution.getResource("uri").toString();
            Recording recording = findRecordingByUri(recordingUri);
            recordings.add(recording);
        }

        return recordings;
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#findRecordings()
     */
    public List<Recording> findRecordings() {
        QuerySolutionMap initialBindings = new QuerySolutionMap();

        // Execute the query;
        Results results = database.executeSelectQuery(findRecordingSparql,
                initialBindings);
        ResultSet resultSet = results.getResults();

        Vector<Recording> recordings = new Vector<Recording>();

        // Put the results into a new recording
        while (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            String recordingUri = solution.getResource("uri").toString();
            Recording recording = findRecordingByUri(recordingUri);
            recordings.add(recording);
        }

        return recordings;
    }

    /**
     *
     * @see net.crew_vre.recordings.dao.RecordingDao#setRecordingEvent(
     *     net.crew_vre.recordings.domain.Recording, java.lang.String)
     */
    public void setRecordingEvent(Recording recording, String eventUri) {
        if (eventUri != null) {
            database.updateProperty(recording.getGraph(), recording.getUri(),
                Crew.IS_RECORDING_OF, ResourceFactory.createResource(eventUri));
        } else if (recording.getEventUri() != null) {
            Model model = database.getUpdateModel();
            Resource resource = model.createResource(recording.getUri());
            resource.addProperty(Crew.IS_RECORDING_OF, recording.getEventUri());
            database.deleteModel(recording.getGraph(), model);
        }
        recording.setEventUri(eventUri);
    }
}
