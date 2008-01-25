package org.wyona.yanel.impl.resources.gallery;

import java.util.Date;

public class LinkedImageGalleryItem extends GenericItemBase implements ImageGalleryItem {
    private GalleryItemContent itemContent = null;
    
    public LinkedImageGalleryItem(final String imageType, final String src) {
        setProperty(ID_KEY, new Date());
        
        itemContent = new GalleryItemContent(){
          public String getContentType() {
                return imageType;
          }
          public boolean isLinkedContent() {
            return true;
          }
          public String toString() {
            return src;
          }
        };
    }
    
    public GalleryItemContent getContent() {
        return itemContent;
    }
}
