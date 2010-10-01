/**
 * Copyright (c) 2008-2009 University of Bristol
 * Copyright (c) 2008-2009 University of Manchester
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
package net.crew_vre.authorization.acls.impl;

import net.crew_vre.authorization.acls.AclLookupManager;
import net.crew_vre.authorization.acls.GraphAcl;
import net.crew_vre.authorization.acls.GraphAclEntry;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.acls.PermissionResolver;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: PermissionResolverImpl.java 1186 2009-03-31 12:37:17Z cmmaj $
 */
public class PermissionResolverImpl implements PermissionResolver {

    public PermissionResolverImpl(AclLookupManager aclLookupManager) {
        this.aclLookupManager = aclLookupManager;
    }

    public boolean authorityHasPermissionForGraph(String graph, String authority,
                                                  Permission permission) {

        // ROLE_ might be prefixed for the spring role voter - remove it for checking the
        // with the database
        if (authority.startsWith(ROLE_PREFIX)) {
            authority = authority.substring(ROLE_PREFIX.length(), authority.length());
        }
        
        GraphAcl graphAcl = aclLookupManager.lookupAcl(graph, authority);

        if (graphAcl != null) {
            for (GraphAclEntry entry : graphAcl.getEntries()) {
                if (permission.intValue() == entry.getPermission()) {
                    return true;
                }
            }
        }

        return false;
    }

    private final AclLookupManager aclLookupManager;
    private final String ROLE_PREFIX = "ROLE_";
}
