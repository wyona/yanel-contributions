package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.log4j.Logger;
import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Superclass for all Tags in the library. Adds functionality for id attribute
 * and the XHTML - Namespace.
 * 
 */
public abstract class YanelTag extends TagSupport {
    private static final Logger log = Logger.getLogger(YanelTag.class);
    protected static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

    private String id = null;
    
    /**
     * The id is set in the tag as attribute and just rendered throug. The doTag
     * Method should attach this id to the tag generated.
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * @return id of the tag.
     */
    public String getId() {
        return id;
    }

    
    /**
     * Adds a [div class='validation-error'] to the out 
     * */
    protected void addValidationMessage(ResourceInputItem item, XMLOutput out) throws Exception {
        final String className = "validation-error";
        if (item.getValidationMessage() != null && !item.getValidationMessage().isValidationOK()) {
            
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(XHTML_NAMESPACE, "", "class", "CDATA", className);
            
            out.startElement("div", attributes);
            out.write(item.getValidationMessage().getMessage());
            out.endElement("div");
        }
    }
}
