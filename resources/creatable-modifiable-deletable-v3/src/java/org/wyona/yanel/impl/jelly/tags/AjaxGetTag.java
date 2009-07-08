package org.wyona.yanel.impl.jelly.tags;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.log4j.Logger;

/**
 * in order to use this tag, prototype.js must be included in the page.
 * 
 */
public class AjaxGetTag extends YanelTag {
    private static Logger log = Logger.getLogger(AjaxGetTag.class);
    private String export;

    private String url;
    
    /**
     * Needed to get input from tag attribute export. Export is the name of the
     * JavaScript function which will be generated.
     * 
     * @param export
     */
    public void setExport(String export) {
        this.export = export;
    }

    /**
     * Needed to get input from tag attribute url.
     * 
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /* 
     * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException {
        try {
            if(getId() == null){
                throw new NullPointerException("The id must be specified for the tag");
            }
            
            AttributesImpl divAttributes = new AttributesImpl();
            divAttributes.addAttribute(XHTML_NAMESPACE, "", "id", "CDATA", getId());
            out.startElement("div", divAttributes);
            out.endElement("div");
            // add the javascript function
            // It should be outside the div, because the updater would remove it otherwise
            out.startElement("script");
            out.writeCDATA(getJavaScriptFunction());
            out.endElement("script");
        } catch (SAXException e) {
            log.error("Tag could not be rendered.", e);
        }

    }

    /*
     * put together javascript function for ajax call.
     */
    private String getJavaScriptFunction() {
        StringBuffer sb = new StringBuffer();
        
        //sb.append("<![CDATA[\n");
        
        sb.append(export + " = function(params) { \n");
        sb.append("if(typeof Ajax == 'undefined'){window.alert('prototype.js is not included to the page'); return;}"+"\n");
        
        sb.append("new Ajax.Updater('" + getId() + "'");
        sb.append(",\n");
        sb.append("'" + url + "'");
        sb.append(",\n");
        sb.append("{" + " method: 'get', parameters: params, evalScripts: true" + "})\n");
        sb.append("}");
        
        //sb.append("\n]]>");
        return sb.toString();
    }

}
