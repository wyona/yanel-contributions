package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.wyona.yanel.impl.jelly.InputItemWithOneSelectableOption;
import org.wyona.yanel.impl.jelly.Option;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.log4j.Logger;

public class RadioTag extends YanelTag {
    private static Logger log = Logger.getLogger(RadioTag.class);
    private InputItemWithOneSelectableOption item = null;
    
    public void setItem(InputItemWithOneSelectableOption resourceInputItem) {
        this.item = resourceInputItem;
    }

    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException {
        try {
            Option[] options = item.getOptions();
            
            for (int i = 0; i < options.length; i++) {
                   
                AttributesImpl attributes = new AttributesImpl();
                attributes.addAttribute(XHTML_NAMESPACE, "", "type", "CDATA", "radio");
                attributes.addAttribute(XHTML_NAMESPACE, "", "name", "CDATA", item.getName());
                attributes.addAttribute(XHTML_NAMESPACE, "", "value", "CDATA", options[i].getValue());
                
                if (options[i].equals(item.getValue())) {
                    attributes.addAttribute(XHTML_NAMESPACE, "", "checked", "CDATA", "true");
                }

                out.startElement("input", attributes);
                out.writeCDATA(options[i].getLabel());
                out.endElement("input");
            }
            
            addValidationMessage(item, out);
            
       } catch (Exception e) {
            log.error("Tag could not be rendered.", e);
        }
    }

}
