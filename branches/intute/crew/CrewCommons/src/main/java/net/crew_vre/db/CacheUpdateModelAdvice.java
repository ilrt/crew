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
package net.crew_vre.db;

import com.hp.hpl.jena.rdf.model.Model;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * Spring AOP advice that sits in front of a database implementation allowing the data to
 * be cached by an in memory implementation - MemoryCacheDatabase.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: CacheUpdateModelAdvice.java 1187 2009-03-31 12:44:25Z cmmaj $
 */
public class CacheUpdateModelAdvice implements AfterReturningAdvice {

    public CacheUpdateModelAdvice(MemoryCacheDatabase database) {
        this.database = database;
    }

    public void afterReturning(Object o, Method method, Object[] objects, Object o1)
            throws Throwable {

        if (method.getName().equals("addModel")) {

            String uri = (String) objects[0];
            Model model = (Model) objects[1];
            database.updateGraph(uri, model);

        } else if (method.getName().equals("deleteModel") || method.getName().equals("deleteAll")) {

            String uri = (String) objects[0];

            if (uri != null) {
                database.removeGraph(uri);
            }

        }

    }

    private MemoryCacheDatabase database;
}
