/*
 * Copyright 2009 Wyona
 */

package org.wyona.yanel.impl.resources.timetracker;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * A time tracker resource
 */
public class TimeTrackerResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(TimeTrackerResource.class);
    
    /*
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<time-tracking xmlns=\"http://www.wyona.org/resource/time-tracking/1.0\">");
        org.wyona.security.core.api.Identity identity = getEnvironment().getIdentity();
        if (identity != null && identity.getUsername() != null) {
            org.wyona.security.core.api.User user = getRealm().getIdentityManager().getUserManager().getUser(identity.getUsername());
            sb.append("  <user id=\"" + identity.getUsername() + "\">" + user.getName() + "</user>");
        } else {
            sb.append("<no-identity-yet/>");
        }
        sb.append("</time-tracking>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
