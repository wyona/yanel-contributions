package org.wyona.yanel.impl.resources.soap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;

public abstract class AbstractWebService implements IWebService{
    protected final class NamespaceContextSupport implements NamespaceContext{
        public static final String SOAP_ENV_PREFIX = "soapenv";
        
        private final Map namespaces = new HashMap();

        public NamespaceContextSupport() {
            namespaces.put(SOAP_ENV_PREFIX, "http://schemas.xmlsoap.org/soap/envelope/");
        }
        
        public String getPrefix(String namespaceURI) {
            Set s = namespaces.entrySet();
            for (Iterator i = s.iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry)i.next();
                if(namespaceURI.equals(e.getValue())){
                    return (String)e.getKey();
                }
            }
            return null;
        }
        
        public Iterator getPrefixes(String namespaceURI) {
            return new HashSet(namespaces.keySet()).iterator();
        }
        
        public String getNamespaceURI(String prefix) {
            return (String)namespaces.get(prefix);
        }
        
        public void registerNamespace(String prefix, String nsURI){
            namespaces.put(prefix, nsURI);
        }
    }
    
	protected final NamespaceContextSupport NAMESPACE_CONTEXT = new NamespaceContextSupport();
	
	protected final Element extractPayload(Element soapEnvelope) throws Exception{
	    
	    XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(NAMESPACE_CONTEXT);
        Element payload = (Element)xpath.evaluate(
                        "/"+NamespaceContextSupport.SOAP_ENV_PREFIX+":Envelope/"+
                          NamespaceContextSupport.SOAP_ENV_PREFIX+":Body/*", soapEnvelope, XPathConstants.NODE);
        
        return payload;
	}
}
