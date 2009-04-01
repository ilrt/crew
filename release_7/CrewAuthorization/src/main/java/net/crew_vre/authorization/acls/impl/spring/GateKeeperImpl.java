package net.crew_vre.authorization.acls.impl.spring;

import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.acls.PermissionResolver;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: GateKeeperImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class GateKeeperImpl implements GateKeeper {

    public GateKeeperImpl(PermissionResolver permissionResolver) {
        this.permissionResolver = permissionResolver;
    }

    public boolean userHasPermissionFor(Object user, Permission permission, String graph) {

        // get the user details
        Authentication authentication = (Authentication) user;

        // get a list of their authorites
        GrantedAuthority[] authorities = authentication.getAuthorities();

        // does an authority provide access? we only need one to give access
        for (GrantedAuthority authority : authorities) {
            if (permissionResolver.authorityHasPermissionForGraph(graph, authority.getAuthority(),
                    permission)) {
                return true;
            }
        }

        return false;
    }

    PermissionResolver permissionResolver;
}
