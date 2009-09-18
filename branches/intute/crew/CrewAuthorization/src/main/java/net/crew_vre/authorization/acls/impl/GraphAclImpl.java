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

import net.crew_vre.authorization.acls.GraphAcl;
import net.crew_vre.authorization.acls.GraphAclEntry;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GraphAclImpl.java 1186 2009-03-31 12:37:17Z cmmaj $
 */
@Entity
@Table(name = "GRAPH_ACL")
public class GraphAclImpl implements GraphAcl {

    public GraphAclImpl() {
    }

    public GraphAclImpl(String graph, String authority) {
        this.graph = graph;
        this.authority = authority;
    }

    public Long getId() {
        return id;
    }

    public String getGraph() {
        return graph;
    }

    public String getAuthority() {
        return authority;
    }

    public List<GraphAclEntry> getEntries() {
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphAclImpl graphAcl = (GraphAclImpl) o;

        if (!authority.equals(graphAcl.authority)) return false;
        if (!graph.equals(graphAcl.graph)) return false;
        if (!id.equals(graphAcl.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + graph.hashCode();
        result = 31 * result + authority.hashCode();
        return result;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = null;

    @Column(name = "GRAPH", nullable = false)
    private String graph;

    @Column(name = "AUTHORITY", nullable = false)
    private String authority;

    @OneToMany(targetEntity = GraphAclEntryImpl.class, fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "GRAPH_ACL_ID")
    List<GraphAclEntry> entries = new ArrayList<GraphAclEntry>();
}
