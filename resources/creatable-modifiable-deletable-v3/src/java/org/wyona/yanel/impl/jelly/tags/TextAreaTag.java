package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.wyona.yanel.impl.jelly.TextualInputItemSupport;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.log4j.Logger;

public class TextAreaTag extends YanelTag {
    private static Logger log = Logger.getLogger(TextAreaTag.class);
    private TextualInputItemSupport item = null;
    
    public void setItem(TextualInputItemSupport resourceInputItem) {
        this.item = resourceInputItem;
    }

    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException {
        try {
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(XHTML_NAMESPACE, "", "id", "CDATA", getId());
            attributes.addAttribute(XHTML_NAMESPACE, "", "name", "CDATA", item.getName());
            out.startElement("textarea", attributes);
            if (item.getValue() != null) {
                out.write((String)item.getValue());
            } else {
                // TODO: needs to have a blank, otherwise it renders directly
                // closing the tag. Did not find a way to suppress that yet.
                out.write(" ");
            }
            out.endElement("textarea");
            
            addValidationMessage(item, out);
            
       } catch (Exception e) {
            log.error("Tag could not be rendered.", e);
        }
    }

}
