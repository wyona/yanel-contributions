package org.wyona.yanel.impl.resources.soap;


import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

public abstract class AbstractWebService implements IWebService{
    protected static final Logger log = Logger.getLogger(AbstractWebService.class);
    
    protected AbstractWebService(){}
    
	protected final Element extractPayload(Element soapEnvelope) throws Exception{
	    
	    XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContextSupport());
        Element payload = (Element)xpath.evaluate(
                        "/"+NamespaceContextSupport.SOAP_ENV_PREFIX+":Envelope/"+
                          NamespaceContextSupport.SOAP_ENV_PREFIX+":Body/*", soapEnvelope, XPathConstants.NODE);
        
        return payload;
	}
	
	protected abstract IOperation detectOperation(MessageContext incomingMessageContext);
}
