package org.wyona.yanel.impl.resources.soap;

import org.w3c.dom.Element;
import org.wyona.yanel.core.Resource;

public class MessageContext {
    private Element requestSOAPMessage = null;
    private Element responseSOAPMessage = null;
    
    private Resource resource = null;

    public Element getRequestSOAPMessage() {
        return requestSOAPMessage;
    }

    public void setRequestSOAPMessage(Element requestSOAPMessage) {
        this.requestSOAPMessage = requestSOAPMessage;
    }

    public Element getResponseSOAPMessage() {
        return responseSOAPMessage;
    }

    public void setResponseSOAPMessage(Element responseSOAPMessage) {
        this.responseSOAPMessage = responseSOAPMessage;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
    
    
    
}
