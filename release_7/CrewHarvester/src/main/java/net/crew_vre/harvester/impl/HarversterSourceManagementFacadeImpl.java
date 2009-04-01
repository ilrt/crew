package net.crew_vre.harvester.impl;

import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.acls.GraphAcl;
import net.crew_vre.authorization.acls.GraphAclEntry;
import net.crew_vre.authorization.acls.GraphAclManager;
import net.crew_vre.authorization.acls.impl.GraphAclEntryImpl;
import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.Harvester;
import net.crew_vre.harvester.HarvesterDao;
import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import net.crew_vre.harvester.web.HarvestSourceAuthority;
import org.ilrt.dibden.domain.Role;
import org.ilrt.dibden.facade.UserManagementFacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarversterSourceManagementFacadeImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class HarversterSourceManagementFacadeImpl implements HarvesterSourceManagementFacade {

    public HarversterSourceManagementFacadeImpl(HarvesterDao harvesterDao, Harvester harvester,
                                                GraphAclManager graphAclManager,
                                                UserManagementFacade userManagementFacade,
                                                String adminRole, String anonRole, String authRole,
                                                String harvesterRole) {
        this.harvesterDao = harvesterDao;
        this.harvester = harvester;
        this.graphAclManager = graphAclManager;
        this.userManagementFacade = userManagementFacade;
        this.adminRole = adminRole;
        this.anonRole = anonRole;
        this.authRole = authRole;
        this.harvesterRole = harvesterRole;

        basicAuthorities.add(adminRole);
        basicAuthorities.add(anonRole);
        basicAuthorities.add(authRole);
        basicAuthorities.add(harvesterRole);
    }

    public HarvestSource getSource(String id) {
        return harvesterDao.findSource(id);
    }

    public List<HarvestSource> getAllSources() {
        return harvesterDao.findAllSources();
    }

    public List<HarvestSource> getAllPermittedSources() {
        return harvesterDao.findAllPermittedSources();
    }

    public void addSource(String location, String name, String description, boolean isBlocked) {
        // only add if the URI doesn't already exist
        if (getSource(location) == null) {
            harvesterDao.createHarvestSource(location, name, description, isBlocked);
        }
    }

    public void updateSource(String location, String name, String description, Date lastVisited,
                             String lastStatus, boolean blocked) {
        harvesterDao.updateHarvestSource(location, name, description, lastVisited, lastStatus,
                blocked);
    }

    public void removeSource(String location) {

        if (getSource(location) != null) {
            harvesterDao.deleteSource(location); // delete source
            harvesterDao.deleteData(location);   // delete data derived from source

            // remove associated acls
            List<GraphAcl> acls = graphAclManager.findAcls(location);

            for (GraphAcl acl : acls) {
                graphAclManager.deleteAcl(acl);
            }

        }
    }

    public String harvestSource(String location) {

        List<HarvestSource> harvestSources = new ArrayList<HarvestSource>();
        harvestSources.add(harvesterDao.findSource(location));
        return harvester.harvest(harvestSources);
    }

    public List<GraphAcl> findAcls(String id) {
        return graphAclManager.findAcls(id);
    }

    public List<HarvestSourceAuthority> defaultPermissions() {

        List<HarvestSourceAuthority> harvestSourceAuthorities
                = new ArrayList<HarvestSourceAuthority>();

        harvestSourceAuthorities.add(new HarvestSourceAuthority(null, adminRole,
                new String[]{Permission.READ.toString(), Permission.WRITE.toString(),
                        Permission.DELETE.toString()}));

        harvestSourceAuthorities.add(new HarvestSourceAuthority(null, authRole,
                new String[]{Permission.READ.toString()}));

        harvestSourceAuthorities.add(new HarvestSourceAuthority(null, anonRole,
                new String[]{Permission.READ.toString()}));

        harvestSourceAuthorities.add(new HarvestSourceAuthority(null, harvesterRole,
                new String[]{Permission.READ.toString(), Permission.WRITE.toString(),
                        Permission.DELETE.toString()}));

        return harvestSourceAuthorities;

    }

    public List<HarvestSourceAuthority> lookupPermissions(String graph) {

        List<HarvestSourceAuthority> authorities =
                new ArrayList<HarvestSourceAuthority>();

        List<GraphAcl> acls = graphAclManager.findAcls(graph);


        for (GraphAcl acl : acls) {

            String[] permissions = new String[acl.getEntries().size()];

            for (int i = 0; i < acl.getEntries().size(); i++) {
                permissions[i] = String.valueOf(acl.getEntries().get(i).getPermission());
            }

            HarvestSourceAuthority harvestSourceAuthority =
                    new HarvestSourceAuthority(acl.getGraph(), acl.getAuthority(), permissions);
            authorities.add(harvestSourceAuthority);
        }

        return authorities;
    }

    public List<String> getAuthoritiesList(String graph) {

        Set<String> allAuthorities = new HashSet<String>();
        Set<String> assignedAuthorites = new HashSet<String>();

        // get a list of the authorities in the database
        List<Role> roles = userManagementFacade.getRoles();

        for (Role role : roles) {
            allAuthorities.add(role.getRoleId());
        }

        if (graph == null || graph.equals("")) {

            allAuthorities.removeAll(basicAuthorities);

        } else {

            List<GraphAcl> graphAcls = graphAclManager.findAcls(graph);

            // we just need the key value

            for (GraphAcl graphAcl : graphAcls) {
                assignedAuthorites.add(graphAcl.getAuthority());
            }

            allAuthorities.removeAll(assignedAuthorites);
        }

        return new ArrayList<String>(allAuthorities);
    }

    public void updatePermissions(String graph, List<HarvestSourceAuthority> authorities) {


        /**
         * The form might remove all permissions from an authority. This means that the authority
         * will not appear on the authorities list. We therefore need to compare the list of
         * existing authorities in the datbase against the new list. If they exist in the database
         * but do not exist in the new list then remove their permissions.
         */

        List<GraphAcl> existingAcls = graphAclManager.findAcls(graph);

        for (GraphAcl graphAcl : existingAcls) {

            boolean exists = false;

            for (HarvestSourceAuthority harvestSourceAuthority : authorities) {
                if (graphAcl.getAuthority().equals(harvestSourceAuthority.getAuthority())) {
                    exists = true;
                }
            }

            if (!exists) {
                graphAcl.getEntries().clear();
                graphAclManager.updateAcl(graphAcl);
            }
        }


        for (HarvestSourceAuthority authority : authorities) {

            GraphAcl graphAcl = graphAclManager.findAcl(authority.getGraph(),
                    authority.getAuthority());

            if (graphAcl == null) { // handle a new acl

                graphAcl = graphAclManager.createGraphAcl(authority.getGraph(),
                        authority.getAuthority());

                for (String permission : authority.getPermissions()) {
                    graphAcl.getEntries().add(new GraphAclEntryImpl(Integer.valueOf(permission)));
                }

            } else {    // editing acls


                /**
                 * For existing permissions we need to make two passes over the data. The first
                 * checks the existing acls against new ones. If a permission exists in the
                 * database but not in the list from the form, this means the permission has been
                 * revoked. The permission is therefore removed from the acl.
                 *
                 * The second pass looks for permissions that don't exist in the database but do
                 * exist in the acl from the form. A new permission has been granted and so the
                 * database must be updated.
                 */


                // first pass - look for revoked permissions
                for (Iterator<GraphAclEntry> iter = graphAcl.getEntries().iterator(); iter.hasNext();) {

                    boolean exists = false;

                    GraphAclEntry entry = iter.next();

                    for (String permission : authority.getPermissions()) {
                        if (permission.equals(String.valueOf(entry.getPermission()))) {
                            exists = true;
                        }
                    }
                    if (!exists) {
                        iter.remove();
                    }
                }

                // second pass - look for granted permissions
                for (String permission : authority.getPermissions()) {

                    boolean exists = false;

                    for (GraphAclEntry graphAclEntry : graphAcl.getEntries()) {
                        if (String.valueOf(graphAclEntry.getPermission()).equals(permission)) {
                            exists = true;
                        }
                    }

                    if (!exists) {
                        graphAcl.getEntries().add(new GraphAclEntryImpl(Integer.valueOf(permission)));
                    }

                }
            }

            graphAclManager.updateAcl(graphAcl);
        }

    }

    private final HarvesterDao harvesterDao;
    private final Harvester harvester;
    private final GraphAclManager graphAclManager;
    private final UserManagementFacade userManagementFacade;

    // these are standard roles
    private String adminRole;
    private String anonRole;
    private String authRole;
    private String harvesterRole;

    private final List<String> basicAuthorities = new ArrayList<String>();
}
