/*
 * @(#)RecordingDaoImpl.java
 * Created: 18 Aug 2008
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 */

package net.crew_vre.recordings.dao.impl;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.crew_vre.recordings.dao.RecordingDao;
import net.crew_vre.recordings.domain.Recording;
import net.crew_vre.recordings.domain.Stream;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
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

    // The file containing the sparql query to find streams of a recording
    private static final String FIND_STREAMS_SPARQL_FILE =
        "/sparql/findStreams.rq";

    private static final String SLASH = System.getProperty("file.separator");

    // The directory where the recordings are stored
    private String recordingsDirectory = null;

    // The database implementation
    private Database database = null;

    // The recording sparql query
    private String findRecordingSparql = null;

    // The streams sparql query
    private String findStreamsSparql = null;

    /**
     * Creates a new RecordingDaoImpl
     *
     * @param database The database
     * @param recDirectory The recording directory
     */
    public RecordingDaoImpl(final Database database,
            final String recDirectory) {
        init(database, recDirectory);
    }

    /**
     * Creates a new RecordingDaoImpl
     *
     * @param configFile The configuration file containing the recording
     *                   location
     * @param database The database
     * @throws IOException If the configuration file can't be read
     */
    public RecordingDaoImpl(final String configFile,
            final Database database) throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream(configFile));
        init(database, properties.getProperty("recordings.location"));
    }

    private void init(final Database database, final String recDirectory) {
        recordingsDirectory = recDirectory;
        if (!recordingsDirectory.endsWith(SLASH)) {
            recordingsDirectory += SLASH;
        }

        // Read the sparql queries
        findRecordingSparql = Utils.loadSparql(
            FIND_RECORDING_SPARQL_FILE);
        findStreamsSparql = Utils.loadSparql(
                FIND_STREAMS_SPARQL_FILE);
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

        // Execute the query;
        Results results = database.executeSelectQuery(findRecordingSparql,
                initialBindings);
        ResultSet resultSet = results.getResults();

        // Put the results into a new recording
        if (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            Recording recording = new Recording();
            recording.fillIn(solution, resultSet.getResultVars());

            // Set the recording directory
            recording.setDirectory(recordingsDirectory
                    + recording.getId() + SLASH);

            // Get the recording's streams
            initialBindings = new QuerySolutionMap();
            initialBindings.add("uri", ResourceFactory.createResource(
                    recording.getUri()));
            results = database.executeSelectQuery(findStreamsSparql,
                    initialBindings);
            List<Stream> streams = new Vector<Stream>();
            while (resultSet.hasNext()) {
                solution = resultSet.nextSolution();
                Stream stream = new Stream();
                stream.fillIn(solution, resultSet.getResultVars());
                stream.setFile(recording.getDirectory() + stream.getSsrc());
                streams.add(stream);
            }
            results.close();
            recording.setStreams(streams);

            // Return the recording
            return recording;
        }

        // If there are no results, return null
        return null;
    }
}
