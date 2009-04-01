package net.crew_vre.harvester.security.spring;

import net.crew_vre.authorization.AccessDeniedException;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;

import java.lang.reflect.Method;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: HarvestSourceCreateGuard.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class HarvestSourceCreateGuard implements MethodBeforeAdvice {

    public HarvestSourceCreateGuard(String harvesterGroupRole) {
        this.harvesterGroupRole = harvesterGroupRole;
    }

    public void before(Method method, Object[] objects, Object o) throws Throwable {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (!create(authentication.getAuthorities())) {
            throw new AccessDeniedException("You are not authorized to create harvest sources");
        }
    }

    private boolean create(GrantedAuthority[] authorities) {

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(harvesterGroupRole)) {
                return true;
            }
        }
        return false;
    }


    private final String harvesterGroupRole;

}