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
package net.crew_vre.harvester.impl;

import java.io.File;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import net.crew_vre.harvester.BaseRulesManager;
import net.crew_vre.harvester.RulesManager;
import net.crew_vre.harvester.jena.rules.ToXSDDateRule;
import net.crew_vre.harvester.jena.rules.CreateResource;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: RulesManagerImpl.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class RulesManagerImpl extends BaseRulesManager implements RulesManager {

    public RulesManagerImpl(String rulesFileName, String vocabDirName) throws Exception {

        // register our rules
        BuiltinRegistry builtinRegistry = BuiltinRegistry.theRegistry;
        builtinRegistry.register(new ToXSDDateRule());
        builtinRegistry.register(new CreateResource());

        // get the full path to the rules file
        rulesFullPath = new File(this.getClass().getClassLoader()
                .getResource(rulesFileName).toURI()).getAbsolutePath();

        // get the full path to the vocabs
        String vocabsPath = new File(this.getClass().getClassLoader()
                .getResource(vocabDirName).toURI()).getAbsolutePath();

        // create the merged vocabs
        vocabs = processVocabs(vocabsPath, rulesFullPath);

    }

    public Model fireRules(Model model) {

        try {

            // create an inference model
            InfModel inf = ModelFactory.createInfModel(createReasoner(rulesFullPath), vocabs, model);

            // create a new model based on the original and deductions of the reasoner
            return ModelFactory.createUnion(model, inf.getDeductionsModel());

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }


    private Model vocabs;
    private final String rulesFullPath;
}
