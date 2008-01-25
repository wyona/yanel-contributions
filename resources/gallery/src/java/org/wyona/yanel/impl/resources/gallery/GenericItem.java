package org.wyona.yanel.impl.resources.gallery;

import java.text.SimpleDateFormat;

public interface GenericItem extends HasProperties{
    public static final SimpleDateFormat XML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"/*+".SSSZ"*/);
    
    public static final String ID_KEY = "id";
    public static final String PATH_KEY = "path";
    public static final String TITLE_KEY = "title";
    public static final String UPDATED_KEY = "updated";
    
    /**
     * Id property
     * */
    String getId();
    
    /**
     * Path property
     * */
    String getPath();
    
    /**
     * Title property
     * */
    String getTitle();
    
    /**
     * Updated property
     * */
    String getUpdated();
}
