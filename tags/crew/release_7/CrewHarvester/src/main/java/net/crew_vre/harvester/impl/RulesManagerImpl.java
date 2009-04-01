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
 * @version $Id: RulesManagerImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
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
