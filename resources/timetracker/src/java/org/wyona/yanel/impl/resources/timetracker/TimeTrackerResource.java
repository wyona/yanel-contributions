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
    protected InputStream getContentXML(String viewId) {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<time-tracking xmlns=\"http://www.wyona.org/resource/time-tracking/1.0\">");
        sb.append("  <user id=\"" + "michi" + "\">" + "Michael Wechner" + "</user>");
        sb.append("</time-tracking>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
