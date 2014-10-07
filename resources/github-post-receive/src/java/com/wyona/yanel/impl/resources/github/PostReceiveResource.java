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
 * See https://developer.github.com/webhooks/
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

        String xHubSignature = getEnvironment().getRequest().getHeader("X-Hub-Signature"); // INFO: https://developer.github.com/webhooks/
        log.warn("DEBUG: X-Hub-Signature: " + xHubSignature);

        String contentType = getEnvironment().getRequest().getContentType();

        if ("application/x-www-form-urlencoded".equals(contentType)) {
            log.warn("DEBUG: Decode application/x-www-form-urlencoded ...");

            java.util.Enumeration<String> paramNames = getEnvironment().getRequest().getParameterNames();
            if (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement().toString();
                if ("payload".equals(name)) {
                    String paramValue = java.net.URLDecoder.decode(getEnvironment().getRequest().getParameter("payload"));
                    log.warn("DEBUG: Key-value pairs: " + paramValue);

                    // TOOD: Get value of 'commits/modified' and 'repository/name' and 'repository/html_url'
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
