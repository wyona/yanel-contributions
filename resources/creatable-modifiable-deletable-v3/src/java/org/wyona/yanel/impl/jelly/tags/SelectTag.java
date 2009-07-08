package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.impl.jelly.InputItemWithOneSelectableOption;
import org.wyona.yanel.impl.jelly.Option;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.log4j.Logger;

public class SelectTag extends YanelTag {
    private static Logger log = Logger.getLogger(SelectTag.class);
    private InputItemWithOneSelectableOption item = null;
    
    /**
     * This is a label that means no selection
     * */
    private String none = null;
    
    public void setItem(ResourceInputItem resourceInputItem) {
        this.item = (InputItemWithOneSelectableOption)resourceInputItem;
    }
    
    public void setNone(String none){
        this.none = none;
    }

    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException {
        try {
            
            AttributesImpl selectAttributes = new AttributesImpl();
            selectAttributes.addAttribute(XHTML_NAMESPACE, "", "id", "CDATA", getId());
            selectAttributes.addAttribute(XHTML_NAMESPACE, "", "name", "CDATA", item.getName());
            
            out.startElement("select", selectAttributes);
            
            Option[] selection = item.getOptions();
            
            boolean isSelected = false;
            for (int i = 0; i < selection.length; i++) {
                   
                AttributesImpl attributes = new AttributesImpl();
               
                if (selection[i].equals(item.getValue())) {
                    attributes.addAttribute(XHTML_NAMESPACE, "", "selected", "CDATA", "selected");
                    isSelected = true;
                }
                attributes.addAttribute(XHTML_NAMESPACE, "", "value", "CDATA", selection[i].getValue());

                out.startElement("option", attributes);
                out.writeCDATA(selection[i].getLabel());
                out.endElement("option");
            }
            
            // Add no-selection label
            if(none != null){
                AttributesImpl attributes = new AttributesImpl();
                if(!isSelected){
                    attributes.addAttribute(XHTML_NAMESPACE, "", "selected", "CDATA", "selected");
                }
                attributes.addAttribute(XHTML_NAMESPACE, "", "value", "CDATA", "");
                
                out.startElement("option", attributes);
                out.writeCDATA(none);
                out.endElement("option");
            }
            
            out.endElement("select");
            
            addValidationMessage(item, out);
            
       } catch (Exception e) {
            log.error("Tag could not be rendered.", e);
        }
    }

}
