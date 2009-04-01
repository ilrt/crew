package net.crew_vre.harvester.web;

public class HarvestSourceAuthority {

    public HarvestSourceAuthority(String graph, String authority, String[] permissions) {
        this.graph = graph;
        this.authority = authority;
        this.permissions = permissions;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    private String graph;
    private String authority;
    private String[] permissions;
}
