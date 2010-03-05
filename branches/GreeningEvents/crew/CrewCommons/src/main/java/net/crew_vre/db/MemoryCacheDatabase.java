/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
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
package net.crew_vre.db;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.JDBC;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sparql.util.Context;

import org.caboto.jena.db.AbstractDatabase;
import org.caboto.jena.db.Data;
import org.caboto.jena.db.DataException;
import org.caboto.jena.db.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.Properties;


/**
 * Copies the graphs held in store and holds them in memory. It provides hooks for updating and
 * removing graphs.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: MemoryCacheDatabase.java 1187 2009-03-31 12:44:25Z cmmaj $
 */
public class MemoryCacheDatabase extends AbstractDatabase {


    // ---------- CONSTRUCTORS

    private Database database;

    public MemoryCacheDatabase(String configPrefix, String dbConfigFile)
    	throws Exception {
    	this(configPrefix, dbConfigFile, null);
    }

	public MemoryCacheDatabase(String configPrefix, String dbConfigFile, Database db) 
    	throws Exception {
    	
    	this.database = db;
    	
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream(dbConfigFile));
        String jdbcUrl = properties.getProperty(configPrefix + ".url");
        String username = properties.getProperty(configPrefix + ".username");
        String password = properties.getProperty(configPrefix + ".password");
        String dbtype = properties.getProperty(configPrefix + ".type");
        String dblayout = properties.getProperty(configPrefix + ".layout");

        String driver = JDBC.getDriver(DatabaseType.fetch(dbtype));
        JDBC.loadDriver(driver);

        Connection sqlConn = DriverManager.getConnection(jdbcUrl, username, password);

        StoreDesc storeDesc = new StoreDesc(dblayout, dbtype);
        SDBConnection conn = new SDBConnection(sqlConn);
        Store store = SDBFactory.connectStore(conn, storeDesc);

        Dataset dataset = SDBFactory.connectDataset(store);

        init(dataset);

        // cleanup
        store.close();
        sqlConn.close();
    }
	
	@Override public Context getQueryContext() { return (database == null) ? null : database.getQueryContext(); }

    // ---------- PUBLIC METHODS

    public Data getData() throws DataException {
        return new MemoryCacheData(datasource, getQueryContext());
    }


    /**
     * Generate a new cache in memory.
     *
     * @param dataset the dataset used to access data.
     */
    public void generateCache(Dataset dataset) {

        // copy the default model
        Model defaultModel = ModelFactory.createDefaultModel();
        Model originalDefaultModel = dataset.getDefaultModel();
        defaultModel.add(originalDefaultModel);
        datasource.setDefaultModel(defaultModel);
        System.out.println("Copying default model: " + defaultModel.size() + " triples");
        originalDefaultModel.close();

        // copy the graphs
        Iterator iter = dataset.listNames();

        while (iter.hasNext()) {

            String uri = (String) iter.next();
            Model model = ModelFactory.createDefaultModel();
            Model graphModel = dataset.getNamedModel(uri);
            model.add(graphModel);
            System.out.println("Copying " + uri + " graph: " + model.size() + " triples");
            datasource.addNamedModel(uri, model);
            graphModel.close();
        }

    }

    /**
     * Update the cache for a specific graph.
     *
     * @param uri   the uri of the graph. Null if the default graph.
     * @param model the model that will be added to the cache.
     */
    public void updateGraph(String uri, Model model) {

        if (uri == null) {
            datasource.getDefaultModel().add(model);
        } else {
            if (datasource.containsNamedModel(uri)) {
                datasource.replaceNamedModel(uri, model);
            } else {
                datasource.addNamedModel(uri, model);
            }
        }
    }


    public void removeGraph(String uri) {

        if (uri == null) {
            datasource.getDefaultModel().removeAll();
        } else {
            datasource.removeNamedModel(uri);
        }
    }    


    // ---------- PRIVATE METHODS

    private void init(Dataset dataset) {

        // create the datasource used by this implementation
        datasource = DatasetFactory.create();

        // generate a memory cache from the dataset
        generateCache(dataset);
    }


    private DataSource datasource;


    private class MemoryCacheData implements Data {

        private Context context;

		public MemoryCacheData(Dataset dataset, Context context) {
            this.dataset = dataset;
            this.context = context;
        }

        public Dataset getDataset() {
            return dataset;
        }

        public Model getModel(String uri) {
            return uri == null ? dataset.getDefaultModel() : dataset.getNamedModel(uri);
        }

        public void close() {
            // do nothing
        }

        private Dataset dataset;

		public Context getContext() { return context; }
    }
}
