package net.crew_vre.harvester;

import com.hp.hpl.jena.rdf.model.Model;
import junit.framework.TestCase;
import net.crew_vre.harvester.impl.HarvestSourceImpl;
import net.crew_vre.harvester.impl.HarvesterImpl;
import org.caboto.jena.db.Database;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HarvesterCoreTest extends TestCase {


    @Test
    public void testHarvestSource() {

        // create a mock harvest object
        final HarvestSource harvestSource = new HarvestSourceImpl("http://example.org", "Test", "Test",
                false);
        List<HarvestSource> sources = new ArrayList<HarvestSource>();
        sources.add(harvestSource);

        Mockery context = new Mockery();

        final Database database = context.mock(Database.class);
        final Resolver resolver = context.mock(Resolver.class);
        final RulesManager rulesManager = context.mock(RulesManager.class);


        // the resolver will be fired

        context.checking(new Expectations() {{
            oneOf(rulesManager).fireRules(with(aNonNull(Model.class)));
            returnValue(aNonNull(Model.class));
        }});


        // the resolver will be fired
        context.checking(new Expectations() {{
            oneOf(resolver).get(harvestSource);
            returnValue(aNonNull(Model.class));
        }});

        // the database will be called - delete existing daya
        context.checking(new Expectations() {{
            oneOf(database).deleteAll("http://example.org");
        }});

        // the database will be called - add the new data
        context.checking(new Expectations() {{
            oneOf(database).addModel(with(aNonNull(String.class)), with(aNonNull(Model.class)));
        }});

        // harvest the data and get the message
        Harvester harvester = new HarvesterImpl(database, resolver, rulesManager);
        String msg = harvester.harvest(sources);

        assertEquals("Unexpected message", "Done", msg);

    }


}
