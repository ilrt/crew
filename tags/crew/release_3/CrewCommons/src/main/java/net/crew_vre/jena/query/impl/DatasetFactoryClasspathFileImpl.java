package net.crew_vre.jena.query.impl;

import com.hp.hpl.jena.query.Dataset;
import net.crew_vre.io.RdfFileFilter;
import net.crew_vre.jena.query.DatasetFactory;
import net.crew_vre.jena.exception.DatasetFactoryException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>An implementation that looks at files in the classpath under the "classes" directory
 * to find RDF data files.</p>
 *
 * <p>In the test environment the default model is stored under "/graphs" and the named
 * graphs are stored under "/graphs/named".</p>
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DatasetFactoryClasspathFileImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DatasetFactoryClasspathFileImpl implements DatasetFactory {

    /**
     * @param defaultModelFile path to the file holding the default model.
     * @param namedGraphsDir   path to the directory holding the named graphs.
     * @throws DatasetFactoryException thrown if the file or directory cannot be found.
     */
    public DatasetFactoryClasspathFileImpl(final String defaultModelFile,
                                           final String namedGraphsDir)
            throws DatasetFactoryException {

        // get the default model
        File aDefaultModelFile = new File(getClass().getResource(defaultModelFile).getPath());

        if (!aDefaultModelFile.exists() || !aDefaultModelFile.isFile()) {
            throw new DatasetFactoryException(new StringBuilder()
                    .append(aDefaultModelFile.getAbsolutePath())
                    .append(" - The file specified as the default model is either ")
                    .append("does not exist or is not a file").toString());
        }

        // find the named graphs
        File namedGraphsPath = new File(getClass().getResource(namedGraphsDir).getPath());

        if (!namedGraphsPath.exists() || !namedGraphsPath.isDirectory()) {
            throw new DatasetFactoryException(new StringBuilder()
                    .append("The directory specified for the named graphs is ")
                    .append("either does not exist or is not a directory").toString());
        }

        List<String> namedGraphsList = new ArrayList<String>();

        File[] namedGraphFiles = namedGraphsPath.listFiles(new RdfFileFilter());

        for (File namedGraphFile : namedGraphFiles) {
            namedGraphsList.add(namedGraphFile.getAbsolutePath());
        }

        dataset = com.hp.hpl.jena.query.DatasetFactory.create(aDefaultModelFile.getAbsolutePath(),
                namedGraphsList);
    }

    public Dataset create() {
        return dataset;
    }

    private Dataset dataset;
}
