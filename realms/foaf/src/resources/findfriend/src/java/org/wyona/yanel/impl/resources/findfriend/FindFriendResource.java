/*
 * Copyright 2007 Wyona
 */

package org.wyona.yanel.impl.resources.findfriend;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

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
        return null;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }
}
