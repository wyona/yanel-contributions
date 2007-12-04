package ch.naz.yanel;

import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.YarepUtil;
import org.wyona.yanel.impl.resources.soap.AbstractWebService;
import org.wyona.yanel.impl.resources.soap.MessageContext;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.NodeType;
import org.wyona.yarep.core.Repository;
import org.xml.sax.InputSource;

import ch.naz.eld.yanel.content.I18nString;
import ch.naz.eld.yanel.content.NAZContentManager;
import ch.naz.eld.yanel.content.impl.NAZContentManagerImpl;
import ch.naz.eld.yanel.sitetree.SiteTree;
import ch.naz.eld.yanel.sitetree.SiteTreeNode;
import ch.naz.eld.yanel.sitetree.yarep.YarepSiteTreeFactory;

public class NAZRubrikService extends AbstractWebService{
    private static Logger log = Logger.getLogger(NAZRubrikService.class);
    
    private static final String NAZ_PREFIX = "naz";
    private MessageContext recentMessageContext = null;
    private static final String RESOURCE_EXTENSION = "";
    
    public NAZRubrikService() {
        NAMESPACE_CONTEXT.registerNamespace(NAZ_PREFIX, Constants.NS_URI);
    }
    
    public Element handle(MessageContext ctx){
        recentMessageContext = ctx;
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        Element response = null;
        try {
            // TODO: Apply security constraints
            String s = handleRequest(extractPayload(ctx.getSoapMessage()));
            response = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(s))).getDocumentElement();
        } catch (Exception e) {
            recentMessageContext = null;
            String s = createFault(e);
            try {
                response = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(s))).getDocumentElement();
            } catch (Exception e1) {
                throw new IllegalStateException("Cannot perform the requested action: "+e1.getMessage());
            }
        }
       
        return response;
    }
    
    private String createFault(Throwable e){
        String response = 
            "<soapenv:Envelope " +
                " xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' " +
                " xmlns:naz='"+NAMESPACE_CONTEXT.getNamespaceURI(NAZ_PREFIX)+"'>" + 
                "<soapenv:Header/>"+
                "<soapenv:Body>" +"\n"+
                    "<naz:"+Constants.RUBRIC_FAULT_TAG+">" +
                        "<naz:"+Constants.EXCEPTION_MESSAGE_TAG+">"+
                            e.getMessage().replaceAll("<", "&lt;")+
                        "</naz:"+Constants.EXCEPTION_MESSAGE_TAG+">"+
                    "</naz:"+Constants.RUBRIC_FAULT_TAG+">" +
                "</soapenv:Body>" +
            "</soapenv:Envelope>";
        System.out.println(response);
        return response;
    }
    
    
	private String handleRequest(Element payload) throws Exception{
		CreateRubrikBean rubrik = new CreateRubrikBean();
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		NodeList nl = (NodeList)xpath.evaluate("*", payload, XPathConstants.NODESET);
		
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element)nl.item(i);
			if(e.getLocalName().equals(Constants.TITLE_DE_TAG)){
				rubrik.setTitleDe(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.TITLE_EN_TAG)){
				rubrik.setTitleEn(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.TITLE_IT_TAG)){
				rubrik.setTitleIt(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.TITLE_FR_TAG)){
				rubrik.setTitleFr(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.AXIS_TAG)){
				rubrik.setAxis(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.NAME_TAG)){
				rubrik.setName(e.getFirstChild().getNodeValue());
			}else if(e.getLocalName().equals(Constants.PARENT_NODE_PATH_TAG)){
				rubrik.setParentNodePath(e.getFirstChild().getNodeValue());
			}
		}
		
		String response = null;
		
		// Create rubrik
		if(isValid(rubrik)){
		    Resource resource = recentMessageContext.getResource();
		    
		    String siteTreePath = null;
            if ("child".equals(rubrik.getAxis())) {
                siteTreePath = rubrik.getParentNodePath() + rubrik.getName();
            } else {
                siteTreePath = PathUtil.getParent(rubrik.getParentNodePath()) + rubrik.getName();
            }
            
		    String newResourceRealmPath = siteTreePath + RESOURCE_EXTENSION;
		    
		    //TODO: remove hardcode
		    ResourceConfiguration rc = new ResourceConfiguration("rubrik", "http://naz.ch/yanel/resource/1.0", null);
		    Resource newResource = resource.getYanel().getResourceManager().getResource(resource.getEnvironment(), resource.getRealm(), "/de" + newResourceRealmPath, rc);
		    
		    createYanelRCFile(newResource, "de");
		    createYanelRCFile(newResource, "en");
		    createYanelRCFile(newResource, "fr");
		    createYanelRCFile(newResource, "it");
		    
		    I18nString title = new I18nString();
            title.setValue("de", rubrik.getTitleDe());
            title.setValue("en", rubrik.getTitleEn());
            title.setValue("fr", rubrik.getTitleFr());
            title.setValue("it", rubrik.getTitleIt());
		    
		    
            SiteTree siteTree = getSiteTree();
            SiteTreeNode originNode = siteTree.getNode(rubrik.getParentNodePath());
            int pos = 0;
            if (originNode != null) {
                pos = originNode.getParentNode().indexOf(originNode) + 1;
            }
            NAZContentManager manager = NAZContentManagerImpl.getInstance(resource.getRealm().getRepository(), siteTree);
            manager.createRubric(newResourceRealmPath, title, pos);
            
		    response = 
	            "<soapenv:Envelope " +
	                " xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' " +
	                " xmlns:naz='"+NAMESPACE_CONTEXT.getNamespaceURI(NAZ_PREFIX)+"'>" +
	                "<soapenv:Header/>"+
	                "<soapenv:Body>" +"\n"+
	                    "<naz:"+Constants.CREATE_RUBRIC_RESPONSE_TAG+">" +
	                    "<naz:"+Constants.NODE_PATH_TAG+">"+
	                            siteTreePath+
	                        "</naz:"+Constants.NODE_PATH_TAG+">"+
	                    "</naz:"+Constants.CREATE_RUBRIC_RESPONSE_TAG+">" +
	                "</soapenv:Body>" +
	            "</soapenv:Envelope>";
		}else{
		    response = createFault(new Exception("The parameters are invalid"));
		}
		
		return response;
	}
	
	private boolean isValid(CreateRubrikBean rubrik){
	    boolean valid = true;
	    
	    //TODO[et] it actually should be validated according to the schema in the WSDL
        
        // TODO: check if id exists already
        // TODO: check if parent is valid

        return valid;
	}
	
    private void createYanelRCFile(Resource resource, String language) throws Exception {
        StringBuffer rcContent = new StringBuffer("<?xml version=\"1.0\"?>\n\n");
        rcContent.append("<yanel:resource-config xmlns:yanel=\"http://www.wyona.org/yanel/rti/1.0\">\n");
        rcContent.append("<yanel:rti name=\"" + resource.getRTD().getResourceTypeLocalName() + "\" namespace=\"" + resource.getRTD().getResourceTypeNamespace() + "\"/>\n\n");
        rcContent.append("<yanel:property name=\"xslt\" value=\"/xslt/rubrik-body.xsl\"/>\n");
        rcContent.append("<yanel:property name=\"xslt\" value=\"/xslt/global.xsl\"/>\n");
        rcContent.append("<yanel:property name=\"mime-type\" value=\"text/html\"/>\n");
        rcContent.append("</yanel:resource-config>");
        
        Repository rcRepo = resource.getRealm().getRTIRepository();
        String newRCPath = PathUtil.getRCPath("/" + language + resource.getPath().substring("/de".length()));
        if (log.isDebugEnabled()) 
            log.debug(newRCPath);
        YarepUtil.addNodes(rcRepo, newRCPath, NodeType.RESOURCE);

        java.io.Writer writer = new OutputStreamWriter(rcRepo.getNode(newRCPath).getOutputStream());
        writer.write(rcContent.toString());
        writer.close();
    }
    
    private SiteTree getSiteTree() throws Exception {
        Node siteTreeNode = recentMessageContext.getResource().getRealm().getRepository().getNode("/sitetree.xml");
        return YarepSiteTreeFactory.getSiteTree(siteTreeNode);
    }

}
