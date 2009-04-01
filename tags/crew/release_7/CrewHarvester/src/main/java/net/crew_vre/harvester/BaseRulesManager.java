package net.crew_vre.harvester;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.util.FileManager;
import jena.RuleMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: BaseRulesManager.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class BaseRulesManager {

    /**
     * Finds the files that make up the vocabularies and merges them into a model.
     *
     * @param vocabsPath the full path to the directory holding the vocabs.
     * @param rulesPath  the full path to the rules file.
     * @return a model representing the vocabs.
     * @throws Exception if there is an error.
     */
    public Model processVocabs(String vocabsPath, String rulesPath) throws Exception {

        List<String> fileList = new ArrayList<String>();

        processPath(new File(vocabsPath), fileList);
        FileManager fm = FileManager.get();

        Model vocabModel = ModelFactory.createDefaultModel();

        for (String path : fileList) {
            vocabModel = ModelFactory.createUnion(fm.loadModel(path), vocabModel);
        }

        return ModelFactory.createInfModel(createReasoner(rulesPath), vocabModel);
    }

    /**
     * Processes a directory looking for RDF files ... we files that end in .rdf. If the File
     * object is another directory then thus method is called recursively. The path of any files
     * found are added to a List.
     *
     * @param file     a file object representing a directory.
     * @param fileList a List to hold the full path of RDF files.
     * @throws Exception if there is an error.
     */
    public void processPath(File file, List<String> fileList) throws Exception {

        if (!file.exists()) {
            throw new Exception(file + " does not exist!");
        }

        File[] files = file.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                processPath(f, fileList);
            }
            if (f.getName().endsWith(".rdf")) {
                fileList.add(f.getAbsolutePath());
            }
        }
    }

    /**
     * Creates a reasoner based on the rules that are provided.
     *
     * @param rulesFile a rules file.
     * @return a reasoner.
     * @throws IOException if the rules file is not found.
     */
    public Reasoner createReasoner(String rulesFile) throws IOException {
        List rules = RuleMap.loadRules(rulesFile, new HashMap());
        return new GenericRuleReasoner(rules);
    }

}
