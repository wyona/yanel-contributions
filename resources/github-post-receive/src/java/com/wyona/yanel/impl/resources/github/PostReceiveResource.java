/*
 * Copyright 2014 Wyona
 */

package com.wyona.yanel.impl.resources.github;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 */
public class PostReceiveResource extends Resource implements ViewableV2  {
    
    private static Logger log = LogManager.getLogger(PostReceiveResource.class);

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getSize()
     */
    public long getSize() throws Exception {
        return -1;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     */
    public View getView(String viewId) throws Exception {
        // TOOD: Detect mime-type, e.g. either 'application/x-www-form-urlencoded' or 'application/json' ...
        log.warn("DEBUG: Decode application/x-www-form-urlencoded ...");

        java.util.Enumeration<String> paramNames = getEnvironment().getRequest().getParameterNames();
        while(paramNames.hasMoreElements()) {
            log.warn("DEBUG: Parameter name: " + paramNames.nextElement().toString());
        }

        View view = new View();
        view.setMimeType("text/plain");
        view.setInputStream(new java.io.ByteArrayInputStream("post-receive".getBytes()));

        return view;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getViewDescriptors()
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }
}
