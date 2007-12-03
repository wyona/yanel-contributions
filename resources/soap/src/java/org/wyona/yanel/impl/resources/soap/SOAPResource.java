package org.wyona.yanel.impl.resources.soap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import ch.naz.yanel.Constants;
import ch.naz.yanel.CreateRubricBean;

/**
 *
 */
public class SOAPResource extends Resource implements ViewableV2 {
	private static final NamespaceContext NAMESPACE_CONTEXT;
	static{
		final Map namespaces = new HashMap();
		namespaces.put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
		namespaces.put("naz", "http//www.naz.ch/eld/wyona/1.0");
		
		NAMESPACE_CONTEXT = new NamespaceContext(){
			public String getNamespaceURI(String prefix) {
				return (String)namespaces.get(prefix);
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
			
		};
	}
	
	
	private static final String SOAP_MIME_TYPE = "application/soap+xml";
	
	public boolean exists() throws Exception {
		return true;
	}
	
	public long getSize() throws Exception {
		return -1;
	}

    /**
     *
     */
    public View getView(String viewId) throws Exception {
        View v = new View();
        v.setMimeType(SOAP_MIME_TYPE);
		
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document soapMessage = dbf.newDocumentBuilder().parse(getEnvironment().getRequest().getInputStream());
		
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(NAMESPACE_CONTEXT);
        Element payload = (Element)xpath.evaluate("/soapenv:Envelope/soapenv:Body/*", soapMessage, XPathConstants.NODE);
		
        ByteArrayInputStream bais = new ByteArrayInputStream(new ServiceInterceptor().handleRequest(payload).getBytes());
        v.setInputStream(bais);
		
        return v;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        // TODO Auto-generated method stub
        return null;
    }
}
