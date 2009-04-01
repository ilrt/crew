package net.crew_vre.harvester;

import net.crew_vre.authorization.acls.GraphAcl;
import net.crew_vre.harvester.web.HarvestSourceAuthority;

import java.util.List;
import java.util.Date;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvesterSourceManagementFacade.java 1107 2009-03-17 14:39:35Z cmmaj $
 */
public interface HarvesterSourceManagementFacade {

    HarvestSource getSource(String id);

    List<HarvestSource> getAllSources();

    List<HarvestSource> getAllPermittedSources();

    void addSource(String location, String title, String description, boolean isBlocked);

    void updateSource(String location, String name, String description, Date lastVisited,
                             String lastStatus, boolean blocked);

    void removeSource(String location);

    String harvestSource(String location);

    List<GraphAcl> findAcls(String id);

    List<HarvestSourceAuthority> defaultPermissions();

    void updatePermissions(String graph, List<HarvestSourceAuthority> authorities);

    List<HarvestSourceAuthority> lookupPermissions(String graph);

    List<String> getAuthoritiesList(String graph);
}
