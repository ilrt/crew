package net.crew_vre.db;

import com.hp.hpl.jena.rdf.model.Model;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * Spring AOP advice that sits in front of a database implementation allowing the data to
 * be cached by an in memory implementation - MemoryCacheDatabase.
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: CacheUpdateModelAdvice.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class CacheUpdateModelAdvice implements AfterReturningAdvice {

    public CacheUpdateModelAdvice(MemoryCacheDatabase database) {
        this.database = database;
    }

    public void afterReturning(Object o, Method method, Object[] objects, Object o1)
            throws Throwable {

        if (method.getName().equals("addModel")) {

            String uri = (String) objects[0];
            Model model = (Model) objects[1];
            database.updateGraph(uri, model);

        } else if (method.getName().equals("deleteModel") || method.getName().equals("deleteAll")) {

            String uri = (String) objects[0];

            if (uri != null) {
                database.removeGraph(uri);
            }

        }

    }

    private MemoryCacheDatabase database;
}
