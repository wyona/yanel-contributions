package org.wyona.yanel.impl.resources.gallery;

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;


public interface Gallery extends GenericItem{
    /**
     * 0 to size()-1
     * */
    GalleryItem getItem(int index)throws IndexOutOfBoundsException;
    int size();
    
    /**
     * Currently only Yarep repository is there. One may need adapters for other kinds of repositories.
     * */
    void setRepository(Repository repository);
    
    /**
     * @param path - Full path to the collection (context is the data collection of the realm). 
     * E.g. /galleries/mygallery/
     * */
    String getPath();
    
    /**
     * @return - created item
     * */
    Node createItem(GenericItem item);
    
    /**
     * @return - success or failure
     * */
    boolean removeItem(GenericItem item);
    
    /**
     * @return - success or failure
     * */
    boolean removeItem(String id);
}
