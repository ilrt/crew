package net.crew_vre.web.startup;

import java.io.File;

import net.crew_vre.harvester.BaseRulesManager;
import org.caboto.jena.db.Database;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: Vocabularies.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class Vocabularies extends BaseRulesManager {

    public Vocabularies(String rulesFileName, String vocabDirName, Database database) {
        this.rulesFileName = rulesFileName;
        this.vocabDirName = vocabDirName;
        this.database = database;
    }

    public void init() throws Exception {

        // full path to the rules
        String rulesFullPath = new File(this.getClass().getClassLoader()
                .getResource(rulesFileName).toURI()).getAbsolutePath();

        // get the full path to the vocabs
        String vocabsPath = new File(this.getClass().getClassLoader()
                .getResource(vocabDirName).toURI()).getAbsolutePath();

        // create the merged vocabs
        Model vocabs = processVocabs(vocabsPath, rulesFullPath);

        System.out.println("Adding vocabs...");
        database.addModel(null, vocabs);

        vocabs.close();
    }


    private Database database;
    private String rulesFileName;
    private String vocabDirName;
}
