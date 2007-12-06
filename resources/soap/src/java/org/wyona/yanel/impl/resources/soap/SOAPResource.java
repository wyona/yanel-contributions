package org.wyona.yanel.impl.resources.soap;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

public class SOAPResource extends Resource implements ViewableV2 {
	private static final String SOAP_MIME_TYPE = "application/soap+xml";
	
	public boolean exists() throws Exception {
		return true;
	}
	
	public long getSize() throws Exception {
		return -1;
	}

    /**
     * The returned view is actually a SOAP response message
     */
    public View getView(String viewId) throws Exception {
        View v = new View();
        v.setMimeType(SOAP_MIME_TYPE);
		
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document soapMessage = dbf.newDocumentBuilder().parse(getEnvironment().getRequest().getInputStream());
		
        MessageContext ctx = new MessageContext();
        ctx.setRequestSOAPMessage(soapMessage.getDocumentElement());
        ctx.setResource(this);
        
        Element response = ((IWebService)Class.forName(getResourceConfigProperty("ws-impl")).newInstance()).handle(ctx);
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        StringWriter sw = new StringWriter();
        t.transform(new DOMSource(response), new StreamResult(sw));
        
        ByteArrayInputStream bais = new ByteArrayInputStream(sw.toString().getBytes());
        v.setInputStream(bais);
		
        return v;
    }

    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }
}
