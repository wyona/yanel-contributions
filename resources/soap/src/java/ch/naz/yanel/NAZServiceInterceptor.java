package ch.naz.yanel;

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

public class NAZServiceInterceptor {
//public class NAZServiceInterceptor implements org.wyona.yanel.impl.resources.sopa.ServiceInterceptor {
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
	
	public String handleRequest(Element payload) throws Exception{
		CreateRubricBean rubric = new CreateRubricBean();
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(NAMESPACE_CONTEXT);
		
		NodeList nl = (NodeList)xpath.evaluate("*", payload, XPathConstants.NODESET);
		
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element)nl.item(i);
			if(e.getLocalName().equals(Constants.TITLE_DE_TAG)){
				rubric.setTitleDe(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.TITLE_EN_TAG)){
				rubric.setTitleEn(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.TITLE_IT_TAG)){
				rubric.setTitleIt(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.TITLE_FR_TAG)){
				rubric.setTitleFr(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.AXIS_TAG)){
				rubric.setAxis(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.NAME_TAG)){
				rubric.setName(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.PARENT_NODE_PATH_TAG)){
				rubric.setParentNodePath(e.getFirstChild().getNodeValue());
			}
		}
		
		// Create rubric
		String rubricPath = "/rubric-"+rubric.getName()+".txt";
		PrintWriter fw = new PrintWriter(new FileWriter(rubricPath));
		fw.println(rubricPath.toString());
		fw.flush();
		
		// TODO: when creation is not successful a message with a fault must be returned
		
		
		String response = 
			"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" + "\n"+
			"\t"+"<soapenv:Header/>"+
			"\t"+"<soapenv:Body>" +"\n"+
				"\t\t"+"<naz:CreateRubricResponse xmlns:naz='"+NAMESPACE_CONTEXT.getNamespaceURI("naz")+"'>" +"\n"+
				"\t\t\t"+"<naz:"+Constants.NODE_PATH_TAG+">"+
							rubricPath+
						"</naz:"+Constants.NODE_PATH_TAG+">"+"\n"+
				"\t\t"+"</naz:CreateRubricResponse>" +"\n"+
			"\t"+"</soapenv:Body>" +"\n"+
			"</soapenv:Envelope>";
		
		return response;
	}
}
