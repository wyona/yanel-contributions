package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.impl.jelly.TextualInputItemSupport;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.log4j.Logger;

/**
 *
 */
public class TextFieldTag extends YanelTag {

    private static Logger log = Logger.getLogger(TextFieldTag.class);
    private ResourceInputItem item = null;

    /**
     * INFO: It seems like this method is somehow dynamically called by ConvertingWrapDynaBean.set(String,Object)
     */
    public void setItem(TextualInputItemSupport resourceInputItem) {
        this.item = resourceInputItem;
        if (item != null) {
            log.debug("Item name: " + item.getName() + ", Item value: " + item.getValue()); 
        } else {
            log.error("Text field item is null! Please make sure that your creatable/modifiable resource-type and corresponding jelly file are using the same item names.");
        }
    }

    /**
     * @see org.apache.commons.jelly.Tag#doTag(XMLOutput)
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException {
        try {
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(XHTML_NAMESPACE, "", "type", "CDATA", "text");
            attributes.addAttribute(XHTML_NAMESPACE, "", "id", "CDATA", getId());
            attributes.addAttribute(XHTML_NAMESPACE, "", "name", "CDATA", item.getName());
            attributes.addAttribute(XHTML_NAMESPACE, "", "value", "CDATA", (String)item.getValue());

            out.startElement("input", attributes);
            out.endElement("input");
            
            addValidationMessage(item, out);
            
       } catch (Exception e) {
            log.error("Tag could not be rendered.", e);
        }
    }

}
