/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.crew_vre.events.acls.impl.spring;

import net.crew_vre.authorization.AccessDeniedException;
import net.crew_vre.authorization.GateKeeper;
import net.crew_vre.authorization.Permission;
import net.crew_vre.domain.DomainObject;

import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.Person;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: DomainObjectExitGuard.java 1188 2009-03-31 13:09:20Z cmmaj $
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

            for (ListIterator iter = results.listIterator(); iter.hasNext();) {

                Object obj = iter.next();

                if (obj instanceof DomainObject) {

                    if (!hasAccessToObject(authentication, (DomainObject) obj)) {

                        if (obj instanceof Event) {
                            iter.set(new Event());
                        } else if(obj instanceof Person) {
                            iter.set(new Person());
                        }
                        else {
                            iter.remove();
                        }
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
