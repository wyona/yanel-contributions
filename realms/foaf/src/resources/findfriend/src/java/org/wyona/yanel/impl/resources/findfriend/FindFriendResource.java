/*
 * Copyright 2007 Wyona
 */

package org.wyona.yanel.impl.resources.findfriend;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.meguni.util.ResultSet;

/**
 *
 */
public class FindFriendResource extends Resource implements ViewableV2 {

    /**
     *
     */
    public FindFriendResource() {
    }

    /**
     *
     */
    public boolean exists() {
        return true;
    }

    /**
     *
     */
    public String getMimeType(String viewId) {
        return "application/xml";
    }

    /**
     *
     */
    public long getSize() {
        return -1;
    }

    /**
     *
     */
    public View getView(String viewId) {
        ResultSet resultSet = getSearchResults();
        View view = new View();
        view.setInputStream(new java.io.StringBufferInputStream(new StringBuffer("<foaf/>").toString()));
        view.setMimeType(getMimeType(viewId));
        return view;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }

    /**
     *
     */
    private ResultSet getSearchResults() {
        return null;
    }
}
