package net.crew_vre.harvester.security.spring;

import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.AccessDeniedException;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import java.lang.reflect.Method;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSourceWriteGuard.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class HarvestSourceWriteGuard implements MethodBeforeAdvice {

    public HarvestSourceWriteGuard(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public void before(Method method, Object[] objects, Object o) throws Throwable {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // the first argument should be the location
        String location = (String) objects[0];

        if (!gateKeeper.userHasPermissionFor(authentication, Permission.WRITE, location)) {
            throw new AccessDeniedException("You cannot update source " + location);
        }

    }

    final private GateKeeper gateKeeper;
}
