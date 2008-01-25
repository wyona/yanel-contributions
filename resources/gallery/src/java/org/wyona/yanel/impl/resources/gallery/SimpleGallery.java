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
public class SimpleGallery extends GenericItemBase implements Gallery {
    private static final String META_XML = "meta.xml";
    private static final String ITEM_COLLECTION_PREFIX = "item-";
    
    private Repository repository = null;
    private List/*<Item>*/ imageItems = null;
    
    /**
     * Clients should initialize the object properly after calling this constructor.
     * Repository and gallery collection must be set as well as loadRepository() must be performed.
     * */
    public SimpleGallery(){
        
    }
    
    public SimpleGallery(Repository repository, String galleryCollection){
        this.repository = repository;
        setCollection(galleryCollection);
        
        loadRepository();
    }
    
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
    
    public Repository getRepository() {
        return repository;
    }
    
    public void setCollection(String path) {
        if(!path.endsWith("/")){
            path += "/";
        }
        setProperty(PATH_KEY, path);
    }
    
    public final void loadRepository(){
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
            setProperty(ID_KEY, getPath()); 
        else 
            setProperty(ID_KEY, id);
        
        Date d = new Date();
        try {
            d = new Date(getRepository().getNode(getPath()).getLastModified());
        } catch (Exception e) {
            // Do nothing
        }
        setProperty(UPDATED_KEY, d);
        
        setProperty(PATH_KEY, getPath());
        
        
        //--- Set properties in available in meta.xml
        try {
            Element meta = getMeta(getRepository().getNode(getPath()).getNode(META_XML));
            String title = getMetaValue(meta, TITLE_KEY);
            if(title == null){
                title = getPath();
            }
            setProperty(TITLE_KEY, title);
        } catch (Exception e) {
            log.debug(e, e);
            setProperty(TITLE_KEY, getPath());
        }
    }
    
    protected void initItems(){
        if(imageItems == null){
            imageItems = new ArrayList/*<Item>*/();
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
                    Node [] imageData = children[i].getNodes();
                    for (int j = 0; j < imageData.length; j++) {
                        // Detect mime type
                        String mimeType = children[i].getMimeType();
                        if(null == mimeType){
                            mimeType = MimeTypeUtil.guessMimeType(PathUtil.getSuffix(imageData[j].getName()));
                        }
                        
                        if(imageData[j].isResource() && imageTypes.contains(mimeType)){
                            LinkedImageGalleryItem item = new LinkedImageGalleryItem(mimeType, imageData[j].getPath());
                            
                            //--- Set item-X properties available in the repository
                            Date d = new Date();
                            
                            String id = children[i].getUUID();
                            if(null == id){
                                id = children[i].getPath();
                            }
                            item.setProperty(ID_KEY, id);
                            
                            try {
                                d = new Date(getRepository().getNode(getPath()).getLastModified());
                            } catch (Exception e) {
                                // Do nothing
                            }
                            item.setProperty(UPDATED_KEY, d);
                            
                            item.setProperty(PATH_KEY, children[i].getPath());
                            
                            //--- Set item properties from meta.xml
                            try {
                                Element meta = getMeta(getRepository().getNode(children[i].getPath()).getNode(META_XML));
                                String title = getMetaValue(meta, TITLE_KEY);
                                if(title == null){
                                    title = imageData[j].getPath();
                                }
                                item.setProperty(TITLE_KEY, title);
                            } catch (Exception e) {
                                log.debug(e, e);
                                item.setProperty(TITLE_KEY, imageData[j].getPath());
                            }
                            
                            imageItems.add(item);
                        }
                    }
                }
            } catch (RepositoryException e) {
                log.debug(e, e);
            }
        }
    }
    
    protected final Element getMeta(Node metaXml) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document d = dbf.newDocumentBuilder().parse(metaXml.getInputStream());
        return d.getDocumentElement();
    }
    
    protected final String getMetaValue(Element meta, String tag) throws Exception{
        String value = null;
        
        NodeList nl = meta.getElementsByTagName(TITLE_KEY);
        if(nl.getLength() > 0){
            value = nl.item(0).getFirstChild().getNodeValue();
        }
        return value;
    }
    
    public final GalleryItem getItem(int index) throws IndexOutOfBoundsException {
        if(imageItems == null){
            imageItems = new ArrayList/*<Item>*/();
            loadRepository();
        }
        return (GalleryItem)imageItems.get(index);
    }

    public final int size() {
        if(imageItems == null){
            imageItems = new ArrayList/*<Item>*/();
            loadRepository();
        }
        return imageItems.size();
    }
    
    public final boolean removeItem(String id) {
        if(imageItems == null){
            imageItems = new ArrayList/*<Item>*/();
            loadRepository();
        }
        
        for (Iterator i = imageItems.iterator(); i.hasNext();) {
            GalleryItem item = (GalleryItem) i.next();
            if(id.equals(item.getId())){
                return removeItem(item);
            }
        }
        
        return false;
    }
    
    /**
     * Override the method in order to remove differently.
     * @param the [item-X] node inside gallery
     * */
    public boolean removeItem(GenericItem item) {
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
        
        return deleteOK;
    }
    
    /**
     * Override the method in order to create differently.
     * Created [item-X] collection node. The image must be added to this node. Inside this collection
     * the meta.xml is also added.
     * @return - created [item-X] node, or null if failed to create one. 
     * */
    public Node createItem(GenericItem item) {
        if(imageItems == null){
            imageItems = new ArrayList/*<Item>*/();
            loadRepository();
        }

        Node itemNode = null;
        try {
            itemNode = getRepository().getNode(getPath()).addNode(ITEM_COLLECTION_PREFIX+size(), NodeType.COLLECTION);
            
            // Fix the system properties
            item.setProperty(ID_KEY, itemNode.getUUID());
            item.setProperty(PATH_KEY, itemNode.getPath());
            imageItems.add(item);
            
            // Create meta of the item
            Node metaXml = itemNode.addNode(META_XML, NodeType.RESOURCE);
            metaXml.setMimeType("application/xml");
            metaXml.setEncoding("UTF-8");
            
            StringBuffer sb = new StringBuffer("<?xml version='1.0'?>");
            sb.append("<meta>");
            
            sb.append("<"+TITLE_KEY+"><![CDATA[");
            sb.append(item.getProperty(TITLE_KEY));
            sb.append("]]></"+TITLE_KEY+">");
            
            // TODO: Other meta info
            
            sb.append("</meta>");
            new PrintWriter(new OutputStreamWriter(metaXml.getOutputStream())).write(sb.toString());
        } catch (Exception e) {
            log.warn("Could not create the item properly", e);
        }
        
        return itemNode;
    }
}