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
 * @version $Id: BaseRulesManager.java 1190 2009-03-31 13:22:30Z cmmaj $
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
