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
        String xGithubDelivery = getEnvironment().getRequest().getHeader("X-Github-Delivery");
        log.warn("DEBUG: X-Github-Delivery: " + xGithubDelivery);

        String contentType = getEnvironment().getRequest().getContentType();

        if ("application/x-www-form-urlencoded".equals(contentType)) {
            log.warn("DEBUG: Decode application/x-www-form-urlencoded ...");

            java.util.Enumeration<String> paramNames = getEnvironment().getRequest().getParameterNames();
            if (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement().toString();
                if ("payload".equals(name)) {
                    String paramValue = getEnvironment().getRequest().getParameter("payload");
                } else {
                    log.error("POST does not contain a parameter called 'payload', but only a parameter called '" + name + "'!");
                }
            } else {
                log.error("POST does not contain any parameters!");
            }
        } else if ("application/json".equals(contentType)) {
            log.error("Content type '" + contentType + "' not supported yet!");
        } else {
            log.error("Content type '" + contentType + "' not supported yet!");
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
