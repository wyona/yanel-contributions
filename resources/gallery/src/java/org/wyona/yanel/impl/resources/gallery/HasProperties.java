package org.wyona.yanel.impl.resources.gallery;

public interface HasProperties {
    /**
     * Every implementation have custom properties
     * */
    Object getProperty(String key);
    void setProperty(String key, Object value);
}
