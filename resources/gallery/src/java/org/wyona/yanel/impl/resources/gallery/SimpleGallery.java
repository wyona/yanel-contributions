package org.wyona.yanel.impl.resources.gallery;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wyona.commons.io.MimeTypeUtil;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.NodeType;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryException;
import org.xml.sax.SAXException;

/**
 * Data model:
 * <p>
 * There is a collection that holds all the galleries. It can be set as a resource property (e.g. /galleries)
 * every gallery is a specific subcollection, e.g. (/galleries/mygallery). Each gallery has a meta.xml that describes it.
 * A gallery contains items (e.g. images) each of them has its own meta.xml for info. For instance the whole data model looks like this:
 * <ul>
 * <li> /galleries
 * <li> /galleries/mygallery
 * <li> /galleries/mygallery/index.html
 * <li> /galleries/mygallery/meta.xml
 * <li> /galleries/mygallery/feed.xml - generatable
 * <li> /galleries/mygallery/item-0/girlfriend.jpg
 * <li> /galleries/mygallery/item-0/meta.xml
 * <li> /galleries/mygallery/item-x/image.jpg
 * <li> /galleries/mygallery/item-x/meta.xml
 * </ul>
 * In general it is [galleries-root][gallery-id]/[item-X]
 * </p>
 * <p>
 * The contents of meta.xml are wrapped into 'meta' tag, where every property key is a subelement that contains necessary data. E.g.:
 * <pre>
 *  &lt;meta>
 *      &lt;title>&lt;![CDATA[The title of the item]]>&lt;/title>
 *  &lt;/meta>
 * </pre>
 * </p>
 */
public class SimpleGallery extends ItemInTheRepositorySupport implements Gallery {
    private static final String ITEM_COLLECTION_PREFIX = "item-";
    
    /**
     * Clients should initialize the object properly after calling this constructor.
     * Repository and gallery collection must be set as well as loadRepository() must be performed.
     * */
    public SimpleGallery(Repository repository, String collection) throws RepositoryException{
        super(repository, collection);
    }

    private SimpleGallery(){}
    
    public final void init(){
        initGallery();
        initItems();
    }
    
    protected void initGallery() {
        //--- Set properties available in the repository
        
        String id = null;
        try {
            id = getRepository().getNode(getPath()).getUUID();
        } catch (Exception e) {
            log.debug(e, e);
        }
        if(id == null)
            properties.setProperty(ID_KEY, getPath()); 
        else 
            properties.setProperty(ID_KEY, id);
        
        Date d = new Date();
        try {
            d = new Date(getRepository().getNode(getPath()).getLastModified());
        } catch (Exception e) {
            // Do nothing
        }
        properties.setProperty(UPDATED_KEY, XML_DATE_FORMAT.format(d));
        
        properties.setProperty(PATH_KEY, getPath());
        
        
        //--- Set properties if available in meta.xml
        try {
            Element meta = getMeta(getRepository().getNode(getPath()).getNode(META_XML));
            String title = getMetaValue(meta, TITLE_KEY);
            if(title == null){
                title = getPath();
            }
            properties.setProperty(TITLE_KEY, title);
        } catch (Exception e) {
            log.debug(e, e);
            properties.setProperty(TITLE_KEY, getPath());
        }
    }
    
    protected void initItems(){
        if(subitems == null){
            subitems = new ArrayList/*<Item>*/();
        }
        
        List/*<String>*/ imageTypes = new ArrayList/*<String>*/();
        imageTypes.add(ImageGalleryItem.BMP_TYPE);
        imageTypes.add(ImageGalleryItem.JPEG_TYPE);
        imageTypes.add(ImageGalleryItem.PNG_TYPE);
        imageTypes.add(ImageGalleryItem.GIF_TYPE);
        
        String galleryPath = getPath();
        
        
        // Get all the images in the same collection
        Node [] children = new Node[0];
        try {
            children = getRepository().getNode(galleryPath).getNodes();
        } catch (Exception e) {
            log.warn(e, e);
        }
        
        for (int i = 0; i < children.length; i++) {
            try {
                if(!children[i].isCollection() && !children[i].getName().startsWith(ITEM_COLLECTION_PREFIX)){
                    // Don't care about the weired files
                    continue;
                }else{
                    subitems.add(new ImageItemInTheGallery(getRepository(), children[i].getPath()));
                }
            } catch (RepositoryException e) {
                log.debug("Could not load a gallery item", e);
            }
        }
    }
    
    public final GalleryItem getItem(int index) throws IndexOutOfBoundsException {
        return (GalleryItem)subitems.get(index);
    }
    
    public GalleryItem getItem(String id){
        for (Iterator i = subitems.iterator(); i.hasNext();) {
            GalleryItem item = (GalleryItem) i.next();
            if(item.getId().equals(id)){
                return item;
            }
        }
        return null;
    }

    public final int size() {
        return subitems.size();
    }
    
    public final GalleryItem removeItem(String id) {
        for (Iterator i = subitems.iterator(); i.hasNext();) {
            GalleryItem item = (GalleryItem) i.next();
            if(id.equals(item.getId())){
                return removeItem(item);
            }
        }
        
        return null;
    }
    
    /**
     * Override the method in order to remove differently.
     * @param the [item-X] node inside gallery
     * */
    public GalleryItem removeItem(GalleryItem item) {
        boolean deleteOK = true;
        
        Node n = null;
        
        try {
            n = getRepository().getNode(item.getPath());
        } catch (Exception e) {
            log.debug(e, e);
        }
        
        if(n != null){
            try {
                n.delete();
            } catch (RepositoryException e) {
                log.debug(e, e);
                deleteOK = false;
            }
        }else{
            deleteOK = false;
        }
        
        if(deleteOK){
            return item;
        }else{
            return null;
        }
    }
    
    /**
     * Override the method in order to create differently.
     * Created [item-X] collection node. The image must be added to this node. Inside this collection
     * the meta.xml is also added.
     * @return - created [item-X] node, or null if failed to create one. 
     * */
    public Node createItem(GenericItem item) {
        Node itemNode = null;
        try {
            Node galleryNode = getRepository().getNode(getPath());
            String itemNodeName = null;
            for (int i = size(); i < Integer.MAX_VALUE; i++) {
                itemNodeName = ITEM_COLLECTION_PREFIX+i;
                if(!galleryNode.hasNode(itemNodeName)){
                    break;
                }
            }
            itemNode = galleryNode.addNode(itemNodeName, NodeType.COLLECTION);
            
            // Create meta of the item
            Node metaXml = itemNode.addNode(META_XML, NodeType.RESOURCE);
            metaXml.setMimeType("application/xml");
            metaXml.setEncoding("UTF-8");
            
            StringBuffer sb = new StringBuffer("<?xml version='1.0'?>");
            sb.append("<meta>");
            
            if(item.getProperty(TITLE_KEY) != null){
                sb.append("<"+TITLE_KEY+"><![CDATA[");
                sb.append(item.getProperty(TITLE_KEY));
                sb.append("]]></"+TITLE_KEY+">");
            }
            
            // TODO: Other meta info
            
            sb.append("</meta>");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(metaXml.getOutputStream()));
            pw.write(sb.toString());
            pw.flush();
            
            // Fix the system properties
            GalleryItem gi = new ImageItemInTheGallery(getRepository(), itemNode.getPath());
            subitems.add(gi);
        } catch (Exception e) {
            log.warn("Could not create the item properly", e);
        }
        
        return itemNode;
    }
}