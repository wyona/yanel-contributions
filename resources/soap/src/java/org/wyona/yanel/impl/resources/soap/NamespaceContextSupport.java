package org.wyona.yanel.impl.resources.soap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;

/**
 * Keeps the namespaces that are in use
 * */
public final class NamespaceContextSupport implements NamespaceContext{
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