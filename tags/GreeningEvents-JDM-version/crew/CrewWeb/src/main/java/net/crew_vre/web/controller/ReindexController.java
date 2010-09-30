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
package net.crew_vre.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.impl.LarqIndexedDatabase;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.emory.mathcs.backport.java.util.Collections;

public class ReindexController implements Controller {
	
	final static private Log log = LogFactory.getLog(ReindexController.class);
	final private List<Database> databases;
	
	public ReindexController(final List<Database> databases) {
		this.databases = databases;
	}
	
	public ReindexController(final Database database) {
		this(Collections.singletonList(database));
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("reindex");
		boolean indexingAttempted = false;
		int errors = 0;
		int attempted = 0;
		if ("POST".equals(request.getMethod())) {
			// Have to consider doing this asynchronously in future
			log.info("Reindex requested");
			indexingAttempted = true;
			long start = System.currentTimeMillis();
			synchronized(databases) {
				for (Database database: databases) {
					if (database instanceof LarqIndexedDatabase) {
						attempted++;
						try { ((LarqIndexedDatabase) database).reindex(); }
						catch (Throwable e) { log.error("Error reindexing " + database, e); errors++; }
					} else {
						log.warn("Not indexed: " + database);
					}
				}
			}
			log.info("Reindex completed. (" + (System.currentTimeMillis() - start) + "ms)");
		}
		mav.addObject("indexRan", indexingAttempted);
		mav.addObject("attempted", attempted);
		mav.addObject("errors", errors);
		return mav;
	}
	
	
	
}
