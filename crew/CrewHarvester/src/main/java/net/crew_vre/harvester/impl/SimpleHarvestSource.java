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

import net.crew_vre.harvester.HarvestSource;

import java.util.Date;

/**
 * @author Damian Steer (d.steer@bristol.ac.uk)
 * @author Mioke Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: SimpleHarvestSource.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class SimpleHarvestSource implements HarvestSource {


    public SimpleHarvestSource(String location, boolean isBlocked, String name,
                               String description) {
        this.location = location;
        this.isBlocked = isBlocked;
        this.lastVisited = new Date(0);
        this.lastStatus = null;
        this.name = name;
        this.description = description;
    }

    public SimpleHarvestSource(String location, String name, String description) {
        this(location, false, name, description);
    }

    public SimpleHarvestSource(String location) {
        this(location, false, null, null);
    }

    public String getLocation() {
        return location;
    }

    public Date getLastVisited() {
        return lastVisited;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    private String location;
    private boolean isBlocked;
    private Date lastVisited;
    private String lastStatus;
    private String name;
    private String description;
}
