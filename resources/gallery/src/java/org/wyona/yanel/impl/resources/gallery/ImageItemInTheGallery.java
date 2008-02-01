package org.wyona.yanel.impl.resources.gallery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;
import org.wyona.commons.io.MimeTypeUtil;
import org.wyona.commons.io.PathUtil;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryException;

public class ImageItemInTheGallery extends ItemInTheRepositorySupport implements ImageGalleryItem {
    private class LinkedImageItemContent implements GalleryItemContent{
        private String contentType = null;
        private String link = null;
        
        public LinkedImageItemContent(String contentType, String link){
            this.contentType = contentType;
            this.link = link;
        }
        public String getContentType() {
            return contentType;
        }
        
        public boolean isLinkedContent() {
            return true;
        }
        public String toString() {
            return link;
        }
    }
    
    public ImageItemInTheGallery(Repository repository, String collection)throws RepositoryException{
        super(repository, collection);
    }
    
    protected void init() throws RepositoryException{
        List/* <String> */imageTypes = new ArrayList/* <String> */();
        imageTypes.add(ImageGalleryItem.BMP_TYPE);
        imageTypes.add(ImageGalleryItem.JPEG_TYPE);
        imageTypes.add(ImageGalleryItem.PNG_TYPE);
        imageTypes.add(ImageGalleryItem.GIF_TYPE);

        Node itemNode = getRepository().getNode(getPath());
        Node[] imageData = itemNode.getNodes();
        for (int j = 0; j < imageData.length; j++) {
            // Detect mime type
            String mimeType = imageData[j].getMimeType();
            if (null == mimeType) {
                mimeType = MimeTypeUtil.guessMimeType(PathUtil
                        .getSuffix(imageData[j].getName()));
            }

            if (imageData[j].isResource() && imageTypes.contains(mimeType)) {
                content = new LinkedImageItemContent(mimeType, imageData[j].getPath());

                // --- Set item-X properties available in the repository
                Date d = new Date();

                String id = itemNode.getUUID();
                if (null == id) {
                    id = itemNode.getPath();
                }
                properties.setProperty(ID_KEY, id);

                try {
                    d = new Date(getRepository().getNode(getPath())
                            .getLastModified());
                } catch (Exception e) {
                    // Do nothing
                }
                properties.setProperty(UPDATED_KEY, XML_DATE_FORMAT.format(d));

                properties.setProperty(PATH_KEY, itemNode.getPath());

                // --- Set item properties from meta.xml
                try {
                    Element meta = getMeta(getRepository().getNode(itemNode.getPath()).getNode(META_XML));
                    String title = getMetaValue(meta, TITLE_KEY);
                    if (title == null) {
                        title = imageData[j].getPath();
                    }
                    properties.setProperty(TITLE_KEY, title);
                } catch (Exception e) {
                    log.debug(e, e);
                    properties.setProperty(TITLE_KEY, imageData[j].getPath());
                }
            }
        }
    }

    public GalleryItemContent getContent() {
        return content;
    }
}
