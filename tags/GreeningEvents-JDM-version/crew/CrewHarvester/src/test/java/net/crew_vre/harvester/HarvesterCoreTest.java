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
