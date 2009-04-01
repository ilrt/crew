package net.crew_vre.harvester.impl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import junit.framework.TestCase;
import net.crew_vre.harvester.RulesManager;
import net.crew_vre.harvester.jena.rules.ToXSDDateRule;
import org.junit.Test;

public class RulesManagerImplTest extends TestCase {

    @Test
    public void testFireRules() throws Exception {

        // get the event model
        Model model = ModelFactory.createDefaultModel();
        model.read(this.getClass().getClassLoader()
                .getResourceAsStream("rdf/event.rdf"), null);

        // register our rules
        BuiltinRegistry builtinRegistry = BuiltinRegistry.theRegistry;
        builtinRegistry.register(new ToXSDDateRule());

        // create the rules manager
        RulesManager rulesManager = new RulesManagerImpl("rules/crew.rules", "vocabs/");
        Model rulesModel = rulesManager.fireRules(model);

        assertTrue("The new model should be larger", rulesModel.size() > model.size());
    }
}
