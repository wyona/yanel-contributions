package org.wyona.yanel.impl.jelly;

import org.wyona.yanel.core.api.attributes.creatable.AbstractResourceInputItem;

import org.apache.log4j.Logger;

/**
 *
 */
public abstract class TextualInputItemSupport extends AbstractResourceInputItem {

    private static Logger log = Logger.getLogger(TextualInputItemSupport.class);
    
    private String value;
    
    public TextualInputItemSupport(String name) {
        super(name);
    }

    /**
     * @param name Name of text field
     * @param value Value of text field
     */
    public TextualInputItemSupport(String name, String value) {
        super(name);
        if(value != null && "".equals(value.trim())){
            log.warn("Value is empty, hence set value to null.");
            value = null;
        }

        log.warn("DEBUG: Value: " + value);
        this.value = value;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem#getValue()
     */
    @Override
    public Object getValue() {
        return this.value;
    }

    /**
     * Set value
     */
    public void doSetValue(Object value) {
        if(value == null){
            this.value = null;
            return;
        }
        
        if (!(value instanceof String)) throw new IllegalArgumentException("Value of input item '" + getName() + "' is not a string: " + value.toString());
        
        if( "".equals(((String)value).trim())){
            value = null;
        }
        
        this.value = (String)value;
    }
}
