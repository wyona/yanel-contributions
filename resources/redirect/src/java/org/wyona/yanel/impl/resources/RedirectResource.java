/*
 * Copyright 2006 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.impl.resources;

import org.w3c.dom.Document;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.Yanel;

import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yanel.core.map.Realm;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.IdentityMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;

import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

/**
 *
 */
public class RedirectResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(RedirectResource.class);
    
    public static String IDENTITY_MAP_KEY = "identity-map";
    
    /**
     *
     */
    public RedirectResource() {
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[1];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType(null);
        return vd;
    }

    public View getView(String viewId) throws Exception {
        return getView(viewId, null);
    }

    /**
     * Generates view
     */
    public View getView(String viewId, String revisionName) throws Exception {
        View view = new View();
        view.setResponse(false); // this resource writes the response itself
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();

        String defaultHref = getResourceConfigProperty("href");
        
        String currentUser = null;
        Identity identity = getIdentity(request);
        if (identity != null) {
            currentUser = identity.getUsername();
        }
        
        ResourceConfiguration rc = getConfiguration();
        Document customConfigDoc = rc.getCustomConfiguration();
        if (customConfigDoc != null) {
            Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());
            Configuration[] redirectConfigs = config.getChildren("user");
                if (redirectConfigs.length == 0) return null;
                for (int i = 0; i < redirectConfigs.length; i++) {
                    try {
                        if (redirectConfigs[i].getAttribute("name") == currentUser || (currentUser).equals(redirectConfigs[i].getAttribute("name"))) {
                            response.setStatus(307);
                            response.setHeader("Location", redirectConfigs[i].getAttribute("href"));
                        } else {
                            response.setStatus(307);
                            response.setHeader("Location", defaultHref);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }
        }

        return view;
    }
    
    /**
     *
     */
    public String getMimeType(String viewid) {
        return null;
    }
    
    /**
     *
     */
    public boolean exists() throws Exception {
        log.warn("Not implemented yet!");
        return true; 
    }
    
    /**
     * 
     */
    public long getSize() throws Exception {
        return -1;
    }

    /**
     *
     */
    public String getPropertyType(String name) {
        log.warn("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public Object getProperty(String name) {
        log.warn("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public String[] getPropertyNames() {
        log.warn("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public void setProperty(String name, Object value) {
        log.warn("Not implemented yet!");
    }
    
    /**
     * Gets the identity from the session associated with the given request.
     * @param request
     * @return identity or null if there is no identity in the session for the current
     *                  realm or if there is no session at all
     */
    private Identity getIdentity(HttpServletRequest request) throws Exception {
    	Realm realm = getRealm();
        HttpSession session = request.getSession(false);
        if (session != null) {
            IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
            if (identityMap != null) {
                return (Identity)identityMap.get(realm.getID());
            }
        }
        return null;
    }

}
