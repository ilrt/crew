package net.crew_vre.harvester.security.spring;

import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.harvester.HarvestSource;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class HarvestSourceModifyGuard implements AfterReturningAdvice {

    public HarvestSourceModifyGuard(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public void afterReturning(Object o, Method method, Object[] objects, Object o1)
            throws Throwable {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (o instanceof List) {

            List results = (List) o;

            for (Iterator iter = results.iterator(); iter.hasNext();) {

                Object obj = iter.next();

                if (obj instanceof HarvestSource) {

                    if (!hasAccessToObject(authentication, (HarvestSource) obj)) {
                        iter.remove();
                    }
                }
            }
        } else if (o instanceof HarvestSource) {
            if (!hasAccessToObject(authentication, (HarvestSource) o)) {
                throw new AccessDeniedException("You cannot modify graph " +
                        ((HarvestSource) o).getLocation());
            }
        }

    }

    private boolean hasAccessToObject(Authentication authentication, HarvestSource harvestSource) {
        return (gateKeeper.userHasPermissionFor(authentication, Permission.WRITE,
                harvestSource.getLocation()) ||
                gateKeeper.userHasPermissionFor(authentication, Permission.DELETE,
                        harvestSource.getLocation()));
    }

    final private GateKeeper gateKeeper;

}