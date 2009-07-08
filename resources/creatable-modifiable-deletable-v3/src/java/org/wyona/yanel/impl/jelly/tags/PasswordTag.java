package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.wyona.yanel.impl.jelly.TextualInputItemSupport;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.log4j.Logger;

public class PasswordTag extends YanelTag {
    private static Logger log = Logger.getLogger(PasswordTag.class);
    private TextualInputItemSupport item = null;
    
    public void setItem(TextualInputItemSupport resourceInputItem) {
        this.item = resourceInputItem;
    }

    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException {
        try {
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(XHTML_NAMESPACE, "", "type", "CDATA", "password");
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
