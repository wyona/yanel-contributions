package org.wyona.yanel.impl.resources.gallery;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

public abstract class GenericItemBase implements GenericItem {
    protected static final Logger log = Logger.getLogger(GenericItemBase.class);
    
    private Properties p = new Properties();
    
    public Object getProperty(String key) {
        return p.get(key);
    }

    public void setProperty(String key, Object value) {
        p.put(key, value);
    }
    
    public final String getId() {
        return (String)getProperty(ID_KEY);
    }
    
    public final String getTitle() {
        return (String)getProperty(TITLE_KEY);
    }
    
    public String getPath() {
        return (String)getProperty(PATH_KEY);
    }
    
    public final String getUpdated() {
        Object updated = getProperty(UPDATED_KEY);
        
        String result = null;
        
        if (updated instanceof String) {
            result = (String)updated;
        }else if (updated instanceof Date) {
            Date d = (Date) updated;
            result = XML_DATE_FORMAT.format(d);
        }else if (updated instanceof Calendar) {
            Calendar d = (Calendar) updated;
            result = XML_DATE_FORMAT.format(d.getTime());
        }if (updated instanceof Long) {
            Long l = (Long) updated;
            Date d = new Date(l.longValue());
            result = XML_DATE_FORMAT.format(d);
        }else if (updated instanceof Integer) {
            Integer i = (Integer) updated;
            Date d = new Date(i.longValue());
            result = XML_DATE_FORMAT.format(d);
        }else{
            log.info("Could not recognize property value");
        }
        
        return result;
    }
}
