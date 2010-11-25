/*
 * Copyright 2007 Wyona
 */

package org.wyona.yanel.resources.konakart.image;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.apache.log4j.Logger;

import java.io.File;

/**
 *
 */
public class KonakartImageResource extends Resource implements ViewableV2  {
    
    private static Logger log = Logger.getLogger(KonakartImageResource.class);

    private final String BASE_PATH_PROPERTY_NAME = "konakart-images-base-path";

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        File file = new File(getResourceConfigProperty(BASE_PATH_PROPERTY_NAME) + File.separator + getName());
        if (file.isFile()) {
            return true;
        } else {
            log.warn("No such image: " + file + " (IMPORTANT: Please note that only the name of the image '" + getName() + "' is used, but not the path '" + getPath() + "' itself)");
            return false;
        }
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getSize()
     */
    public long getSize() throws Exception {
        return new File(getResourceConfigProperty(BASE_PATH_PROPERTY_NAME) + File.separator + getName()).length();
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     */
    public View getView(String viewId) throws Exception {
        View view = new View();
        view.setMimeType("image/jpeg");
        view.setInputStream(new java.io.FileInputStream(getResourceConfigProperty(BASE_PATH_PROPERTY_NAME) + File.separator + getName()));
        return view;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getViewDescriptors()
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vds = new ViewDescriptor[1];
        vds[0] = new ViewDescriptor("default");
        vds[0].setMimeType("image/jpeg");
        return vds;
    }

    /**
     * Get name of requested image
     */
    private String getName() {
        return new java.io.File(getPath()).getName();
    }
}
