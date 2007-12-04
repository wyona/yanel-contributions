package org.wyona.yanel.impl.resources.soap;

import org.w3c.dom.Element;

public interface IWebService {
    /**
     * Handles an incoming call to a Web service, i.e. parses the message
     * @return a response of the service call. On some exceptions should return message with a fault
     * */    
    public Element handle(MessageContext ctx);
}
