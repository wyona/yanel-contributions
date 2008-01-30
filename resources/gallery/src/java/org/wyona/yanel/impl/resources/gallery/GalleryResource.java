/*
 * Copyright 2007 Wyona
 */

package org.wyona.yanel.impl.resources.gallery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.RepositoryException;

/**
 * 
 */
public class GalleryResource extends BasicXMLResource implements ModifiableV2{
    protected static final Logger log = Logger.getLogger(GalleryResource.class);
    
    /**
     * On delete this parameter gives the item id for deletion
     * */
    public static final String PARAM_REMOVE_ITEM_ID = "remove.item.id";
    public static final String ATOM_VIEW_ID = "atom";
    
    /**
     * Property name for the collection where galleries are situated
     * */
    public static final String GALLERIES_COLLECTION_KEY = "galleries-collection";
    
    
    /**
     * Takes the path from property, or respective to the requested resource.
     * The path should end with '/'
     * */
    public String getGalleryPath(){
        String collection = null;
        try {
            collection = getResourceConfigProperty(GALLERIES_COLLECTION_KEY);
        } catch (Exception e) {
            log.warn(e, e);
        }
        
        if(collection != null){
            String galleryId = PathUtil.getName(PathUtil.getParent(getPath()));
            
            collection = collection.endsWith("/") ? collection : collection + "/";
            collection += galleryId;
        }else {
            // Assume that we are already in the gallery
            collection = PathUtil.getParent(getPath());
        }
        
        if(!collection.endsWith("/")){
            collection += "/";
        }
        
        return collection;
    }
    
    //--- Viewable
    
    protected InputStream getContentXML(String viewId) throws Exception {
        if(viewId == null || viewId.equals(DEFAULT_VIEW_ID) || viewId.equals(SOURCE_VIEW_ID)){
            return super.getContentXML(viewId);
        }
        
        // Generate content
        String content = null;
        
        if(viewId.equals(ATOM_VIEW_ID)){
            Gallery gallery = new SimpleGallery(getRealm().getRepository(), getGalleryPath());
            content = generateGalleryAtom(gallery);
            
            // Avoid caching of the atom feed
            // TODO: maybe cache/no-cache should be configurable for a resource?
            /*
             * Cache-control: no-cache
             * Cache-control: no-store
             * Pragma: no-cache
             * Expires: 0
             */
            getEnvironment().getResponse().setHeader("Cache-control", "no-cache");
            getEnvironment().getResponse().setHeader("Cache-control", "no-store");
            getEnvironment().getResponse().setHeader("Pragma", "no-cache");
            getEnvironment().getResponse().setHeader("Expires", "0");
        }
        
        return new ByteArrayInputStream(content.getBytes());
    }
    
    private String generateGalleryAtom(Gallery gallery){
        StringBuffer feed = new StringBuffer("<?xml version='1.0' ?>"+"\n");
        feed.append("<feed xmlns='http://www.w3.org/2005/Atom'>"+"\n");
        
        feed.append("<id>"+gallery.getId()+"</id>"+"\n");
        feed.append("<title>"+gallery.getTitle()+"</title>"+"\n");
        feed.append("<updated>"+gallery.getUpdated()+"</updated>"+"\n");
        
        for (int i = 0; i < gallery.size(); i++) {
            feed.append("<entry>"+"\n");
            
            GalleryItem entry = gallery.getItem(i);
            feed.append("<id>"+entry.getId()+"</id>"+"\n");
            feed.append("<title>"+entry.getTitle()+"</title>"+"\n");
            feed.append("<updated>"+entry.getUpdated()+"</updated>"+"\n");
            
            if(entry.getContent().isLinkedContent()){
                feed.append("<content type='"+entry.getContent().getContentType()+"' src='"+PathUtil.backToRealm(getPath())+entry.getContent().toString()+"'>"+"</content>"+"\n");
            }else{
                feed.append("<content type='"+entry.getContent().getContentType()+"'>"+entry.getContent().toString()+"</content>"+"\n");
            }
            
            feed.append("</entry>"+"\n");
        }
        
        feed.append("</feed>");
        return feed.toString();
    }

    
    //--- Modifiable
    
    public boolean delete() throws Exception{
        boolean deleteOK = true;
        
        String itemToDeleteId = getParameterAsString(PARAM_REMOVE_ITEM_ID);
        if(itemToDeleteId != null){
            // Remove the item
            Gallery gallery = new SimpleGallery(getRealm().getRepository(), getGalleryPath());
            gallery.removeItem(itemToDeleteId);
        }else{
            // Remove the gallery
            try {
                // Remove the images
                getRealm().getRepository().getNode(getGalleryPath()).delete();
                
                // Remove gallery related browsing
                getRealm().getRepository().getNode(PathUtil.getParent(getPath())).delete();
            } catch (NoSuchNodeException e) {
                log.debug(e, e);
                deleteOK = false;
            } catch (RepositoryException e) {
                log.debug(e, e);
                deleteOK = false;
            }
        }
        
        if(!deleteOK){
            log.info(itemToDeleteId+": The item might not have been deleted from the gallery");
        }
        
        return deleteOK;
    }
    
    // Don't understand
    public InputStream getInputStream() throws Exception {
        return getRealm().getRepository().getNode(getGalleryPath()).getInputStream();
    }
    
    // Don't understand
    public long getLastModified() throws Exception {
        return getRealm().getRepository().getNode(getGalleryPath()).getLastModified();
    }
    
    // Don't understand
    public OutputStream getOutputStream() throws Exception {
        return getRealm().getRepository().getNode(getGalleryPath()).getOutputStream();
    }
    
    // Don't understand
    public Reader getReader() throws Exception {
        return getRealm().getRepository().getReader(new Path(getGalleryPath()));
    }
    
    // Don't understand
    public Writer getWriter() throws Exception {
        return getRealm().getRepository().getWriter(new Path(getGalleryPath()));
    }
    
    // Don't understand
    public void write(InputStream in) throws Exception {
        // TODO Auto-generated method stub
        log.warn("TODO[normal]: implement!");
    }
}
