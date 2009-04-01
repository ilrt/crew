package net.crew_vre.web.jena;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import net.crew_vre.jena.query.DatasetFactory;

/**
 * <p>Class details</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: ClasspathFileFactoryImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class ClasspathFileFactoryImpl implements DatasetFactory {

    public ClasspathFileFactoryImpl(String path) {

        Model model = ModelFactory.createFileModelMaker(getClass()
                .getClassLoader().getResource("").getPath()).openModel(path);

        dataSource = com.hp.hpl.jena.query.DatasetFactory.create(model);
    }

    public Dataset create() {
        return dataSource;
    }

    private DataSource dataSource;
}
