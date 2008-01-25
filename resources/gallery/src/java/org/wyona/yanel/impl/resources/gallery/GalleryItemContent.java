package org.wyona.yanel.impl.resources.gallery;

public interface GalleryItemContent {
    boolean isLinkedContent();
    String getContentType();
    /**
     * String representation of the content
     * */
    String toString();
}
