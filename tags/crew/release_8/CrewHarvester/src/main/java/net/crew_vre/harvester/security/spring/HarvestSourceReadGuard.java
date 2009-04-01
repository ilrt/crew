package net.crew_vre.harvester.security.spring;

import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.harvester.HarvestSource;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Iterator;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSourceReadGuard.java 1092 2009-03-11 19:01:38Z cmmaj $
 */
public class HarvestSourceReadGuard implements AfterReturningAdvice {

    public HarvestSourceReadGuard(GateKeeper gateKeeper) {
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
                throw new AccessDeniedException("You cannot access graph " +
                        ((HarvestSource) o).getLocation());
            }
        }

    }

    private boolean hasAccessToObject(Authentication authentication, HarvestSource harvestSource) {
        return gateKeeper.userHasPermissionFor(authentication, Permission.READ,
                harvestSource.getLocation());
    }

    private GateKeeper gateKeeper;
}
