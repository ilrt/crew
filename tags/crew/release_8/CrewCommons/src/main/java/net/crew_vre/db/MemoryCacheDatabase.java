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
 * @version $Id: MemoryCacheDatabase.java 1123 2009-03-20 10:56:58Z cmdms $
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
