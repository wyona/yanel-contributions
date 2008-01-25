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
import org.wyona.yanel.core.Constants;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.attributes.viewable.View;
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
    private static final String PARAM_REMOVE_ITEM_ID = "remove.item.id";
    private static final String ATOM_VIEW_ID = "atom";
    
    /**
     * Property name for the collection where galleries are situated
     * */
    public static final String GALLERIES_COLLECTION_KEY = "galleries-collection";
    
    
    /**
     * Takes the path from property, or respective to the requested resource
     * */
    protected String getGalleryPath(){
        String collection = null;
        try {
            collection = getConfiguration().getProperty(GALLERIES_COLLECTION_KEY);
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
        
        // Generate XML
        
        Gallery gallery = new SimpleGallery(getRealm().getRepository(), getGalleryPath());
        
        InputStream result = null;
        if(viewId.equals(ATOM_VIEW_ID)){
            // Create the Atom feed
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
            
            result = new ByteArrayInputStream(feed.toString().getBytes());
        }
        
        return result;
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
                getRealm().getRepository().getNode(getGalleryPath()).delete();
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
    
    public InputStream getInputStream() throws Exception {
        return getRealm().getRepository().getNode(getGalleryPath()).getInputStream();
    }
    public long getLastModified() throws Exception {
        return getRealm().getRepository().getNode(getGalleryPath()).getLastModified();
    }
    
    public OutputStream getOutputStream() throws Exception {
        return getRealm().getRepository().getNode(getGalleryPath()).getOutputStream();
    }
    
    public Reader getReader() throws Exception {
        return getRealm().getRepository().getReader(new Path(getGalleryPath()));
    }
    
    public Writer getWriter() throws Exception {
        return getRealm().getRepository().getWriter(new Path(getGalleryPath()));
    }
    
    public void write(InputStream in) throws Exception {
        // TODO Auto-generated method stub
        log.warn("TODO[normal]: implement!");
    }
}
