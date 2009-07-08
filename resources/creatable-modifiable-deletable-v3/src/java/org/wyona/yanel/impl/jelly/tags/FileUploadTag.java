package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.wyona.yanel.impl.jelly.FileInputItem;
import org.wyona.yanel.impl.jelly.FileItem;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.log4j.Logger;

public class FileUploadTag extends YanelTag {
    private static Logger log = Logger.getLogger(FileUploadTag.class);
    private static final String FILE_UPLOADED_INFO_STYLE_NAME = "Yanel-fileUploadedInfo";
    private static final String FILE_NOT_UPLOADED_INFO_STYLE_NAME = "Yanel-fileNotUploadedInfo";
    private FileInputItem item = null;
    
    private boolean attachInfo = false;
    private String size = "50";
    
    public void setAttachInfo(boolean attachInfo) {
        this.attachInfo = attachInfo;
    }
    
    /**
     * When true, then attempts to show the file path.
     * Styled with the certain class name
     * */
    public boolean isAttachInfo() {
        return attachInfo;
    }
    
    public void setItem(FileInputItem resourceInputItem) {
        this.item = resourceInputItem;
    }
    
    public void setSize(String size) {
        this.size = (size == null || "".equals(size.trim()) ? "50" : size.trim());
    }
    
    public String getSize() {
        return size;
    }

    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException {
        try {
            
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(XHTML_NAMESPACE, "", "type", "CDATA", "file");
            attributes.addAttribute(XHTML_NAMESPACE, "", "id", "CDATA", getId());
            attributes.addAttribute(XHTML_NAMESPACE, "", "name", "CDATA", item.getName());
            attributes.addAttribute(XHTML_NAMESPACE, "", "size", "CDATA", getSize());
            
            out.startElement("input", attributes);
            out.endElement("input");
            
            // Add an info field
            if(isAttachInfo()){
                AttributesImpl spanAttrs = new AttributesImpl();
                String infoMessage = null;
                if(item != null && item.getValue() != null){
                    spanAttrs.addAttribute(XHTML_NAMESPACE, "", "class", "CDATA", FILE_UPLOADED_INFO_STYLE_NAME);
                    infoMessage = ((FileItem)item.getValue()).getFileName();
                    if(infoMessage == null){
//                        infoMessage = ((FileItem)item.getValue()).getUrl();
                    }
                }else{
                    spanAttrs.addAttribute(XHTML_NAMESPACE, "", "class", "CDATA", FILE_NOT_UPLOADED_INFO_STYLE_NAME);
                    infoMessage = "No file uploaded";
                }
                
                if(infoMessage != null){
                    out.startElement("span", spanAttrs);
                    out.write(infoMessage);
                    out.endElement("span");
                }
            }
            
            addValidationMessage(item, out);
            
       } catch (Exception e) {
            log.error("Tag could not be rendered.", e);
        }
    }

}
