package net.crew_vre.authorization.acls.impl.spring;

import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.events.domain.DomainObject;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: DomainObjectExitGuard.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class DomainObjectExitGuard implements AfterReturningAdvice {

    public DomainObjectExitGuard(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public void afterReturning(Object o, Method method,
                               Object[] objects, Object o1) throws Throwable {

        // get the user details
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (o instanceof List) {        // handle a list

            List results = (List) o;

            for (Iterator iter = results.iterator(); iter.hasNext();) {

                Object obj = iter.next();

                if (obj instanceof DomainObject) {

                    if (!hasAccessToObject(authentication, (DomainObject) obj)) {
                        iter.remove();
                    }
                }
            }

        } else if (o instanceof DomainObject) {     // handle a domain object
            if (!hasAccessToObject(authentication, (DomainObject) o)) {
                throw new AccessDeniedException("You cannot access the graph " +
                        ((DomainObject) o).getGraph());
            }
        }
    }


    private boolean hasAccessToObject(Authentication authentication, DomainObject obj) {
        return gateKeeper.userHasPermissionFor(authentication, Permission.READ,
                obj.getGraph());
    }

    private GateKeeper gateKeeper;
}
