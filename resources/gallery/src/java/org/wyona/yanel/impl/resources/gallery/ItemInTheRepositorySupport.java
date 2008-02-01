package org.wyona.yanel.impl.resources.gallery;

import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryException;

public abstract class ItemInTheRepositorySupport extends GenericItemBase{
    public static final String META_XML = "meta.xml";
    
    private Repository repository = null;
    protected List/*<Item>*/ subitems = null;
    protected GalleryItemContent content = null;
    
    public ItemInTheRepositorySupport(Repository repository, String collection)throws RepositoryException{
        this.repository = repository;
        setCollection(collection);
        init();
    }
    
    /**
     * Does nothing
     * */
    protected ItemInTheRepositorySupport(){}
    
    /**
     * Write property to the repository (update meta.xml that must be already available) 
     * */
    public void setProperty(String key, Object value) {
        Element meta = null;
        try {
            meta = getMeta(getRepository().getNode(getPath()).getNode(META_XML));
        } catch (Exception e) {
            log.info(e, e);
            
            // Failed to set the property
            return;
        }
        
        if(key.equals(TITLE_KEY)){
            NodeList nl = meta.getElementsByTagName(TITLE_KEY);
            while(nl.getLength() > 0){
                nl.item(0).getParentNode().removeChild(nl.item(0));
            }
            Element title = meta.getOwnerDocument().createElement(TITLE_KEY);
            title.appendChild(meta.getOwnerDocument().createTextNode(String.valueOf(value)));
            meta.appendChild(title);
        }
        // TODO: write other properties
        
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(meta), new StreamResult(getRepository().getNode(getPath()).getNode(META_XML).getOutputStream()));
            // Update the instance
            super.setProperty(key, value);
        } catch (Exception e) {
            log.info(e, e);
        }
    }
    
    public void setRepository(Repository repository){
        this.repository = repository;
    }
    
    public Repository getRepository(){
        return repository;
    }
    
    protected abstract void init() throws RepositoryException;
    
    public void setCollection(String path) {
        if(!path.endsWith("/")){
            path += "/";
        }
        properties.setProperty(PATH_KEY, path);
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
}
