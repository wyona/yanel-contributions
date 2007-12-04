package org.wyona.yanel.impl.resources.soap;

import org.w3c.dom.Element;
import org.wyona.yanel.core.Resource;

public class MessageContext {
    private Element soapMessage = null;
    private Resource resource = null;
    
    public Element getSoapMessage() {
        return soapMessage;
    }
    public void setSoapMessage(Element soapMessage) {
        this.soapMessage = soapMessage;
    }
    public Resource getResource() {
        return resource;
    }
    public void setResource(Resource resource) {
        this.resource = resource;
    }
    
    
}
