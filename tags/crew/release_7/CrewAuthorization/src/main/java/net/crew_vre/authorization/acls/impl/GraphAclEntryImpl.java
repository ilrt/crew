package net.crew_vre.authorization.acls.impl;

import net.crew_vre.authorization.acls.GraphAclEntry;
import net.crew_vre.authorization.acls.GraphAcl;
import net.crew_vre.authorization.acls.impl.GraphAclImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GraphAclEntryImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
@Entity
@Table(name = "GRAPH_ACL_ENTRY")
public class GraphAclEntryImpl implements GraphAclEntry {

    public GraphAclEntryImpl() {
    }

    public GraphAclEntryImpl(int permission) {
        this.permission = permission;
    }

    public Long getId() {
        return id;
    }

    public int getPermission() {
        return permission;
    }

    public GraphAcl getGraphAcl() {
        return graphAcl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphAclEntryImpl that = (GraphAclEntryImpl) o;

        if (permission != that.permission) return false;
        if (!graphAcl.equals(that.graphAcl)) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + permission;
        result = 31 * result + graphAcl.hashCode();
        return result;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "PERMISSION", nullable = false)
    private int permission;

    @ManyToOne
    @JoinColumn(name = "GRAPH_ACL_ID", insertable = false, updatable = false)
    private GraphAclImpl graphAcl = null;
}
