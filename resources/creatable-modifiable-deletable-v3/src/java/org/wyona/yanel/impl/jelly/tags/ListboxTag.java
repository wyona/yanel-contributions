package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.impl.jelly.InputItemWithManySelectableOptions;
import org.wyona.yanel.impl.jelly.Option;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.log4j.Logger;

public class ListboxTag extends YanelTag {
    private static Logger log = Logger.getLogger(ListboxTag.class);
    private InputItemWithManySelectableOptions item = null;
    
    public void setItem(ResourceInputItem resourceInputItem) {
        this.item = (InputItemWithManySelectableOptions)resourceInputItem;
    }

    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException {
        try {
            
            AttributesImpl selectAttributes = new AttributesImpl();
            selectAttributes.addAttribute(XHTML_NAMESPACE, "", "name", "CDATA", item.getName());
            selectAttributes.addAttribute(XHTML_NAMESPACE, "", "id", "CDATA", getId());
            selectAttributes.addAttribute(XHTML_NAMESPACE, "", "multiple", "CDATA", "multiple");
            
            out.startElement("select", selectAttributes);
            
            Option[] selection = item.getOptions();
            
            for (int i = 0; i < selection.length; i++) {
                   
                AttributesImpl attributes = new AttributesImpl();
               
                if (isSelected(selection[i])) {
                    attributes.addAttribute(XHTML_NAMESPACE, "", "selected", "CDATA", "selected");
                }
                attributes.addAttribute(XHTML_NAMESPACE, "", "value", "CDATA", selection[i].getValue());

                out.startElement("option", attributes);
                out.writeCDATA(selection[i].getLabel());
                out.endElement("option");
            }
            
            out.endElement("select");
            
            //Add a single hidden field to make sure that the field is always
            // submitted, even when no option is checked.
            AttributesImpl hiddenAttributes = new AttributesImpl();
            hiddenAttributes.addAttribute(XHTML_NAMESPACE, "", "type", "CDATA", "hidden");
            hiddenAttributes.addAttribute(XHTML_NAMESPACE, "", "id", "CDATA", getId());
            hiddenAttributes.addAttribute(XHTML_NAMESPACE, "", "name", "CDATA", item.getName());
            hiddenAttributes.addAttribute(XHTML_NAMESPACE, "", "value", "CDATA", "");
            out.startElement("input", hiddenAttributes);
            out.endElement("input");
            
            addValidationMessage(item, out);
            
       } catch (Exception e) {
            log.error("Tag could not be rendered.", e);
        }
    }
    
    private boolean isSelected(Option vt) {
        Option[] selected = item.getValue();
        
        for (int i = 0; i < selected.length; i++) {
            if (selected[i].equals(vt)) return true;
        }
        return false;
    }

}
