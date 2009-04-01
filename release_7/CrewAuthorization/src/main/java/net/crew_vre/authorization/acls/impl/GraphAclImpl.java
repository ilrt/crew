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
 * @version $Id: GraphAclImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
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
