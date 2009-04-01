package net.crew_vre.harvester.web;

import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class AbstractHarvestSourceController extends SimpleFormController {

    protected List<HarvestSourceAuthority> findAuthorities(String graph, HttpServletRequest request) {

        // hold a list of relevant keys
        List<String> keys = new ArrayList<String>();

        List<HarvestSourceAuthority> authorities = new ArrayList<HarvestSourceAuthority>();

        // find the authority keys
        Enumeration e = request.getParameterNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (key.startsWith(AUTHORITY_PREFIX)) {
                keys.add(key);
            }
        }

        // create HarvestSourceAuthority objects
        for (String key : keys) {

            // calculate the authority from the key
            String authority = key.substring(AUTHORITY_PREFIX.length(), key.length());

            HarvestSourceAuthority harvestSourceAuthority = new HarvestSourceAuthority(graph,
                    authority, request.getParameterValues(key));
            authorities.add(harvestSourceAuthority);
        }

        return authorities;

    }

    private final String AUTHORITY_PREFIX = "AUTHORITY_";
}
