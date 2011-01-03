/*
 * Copyright 2009 Wyona
 */
package org.wyona.yanel.resources.konakart;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.wyona.yarep.core.Node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * A simple Resource which extends BasicXMLResource
 */
public class KonakartResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(KonakartResource.class);
    
    /*
     * This method overrides the method to create the InputStream called by BasicXMLResource
     * Since you extend the BasicXMLResource this has to contain well-formed xml.
     * Should return a InputStream which contains XML. 
     * Use String, StingBuffer, dom, jdom, org.apache.commons.io.IOUtils and so on to generate the XML.
     */
    protected InputStream getContentXML(String viewId) {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        String language = "en";

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<konakart>");
        try {
            org.wyona.yarep.core.Repository konakartRepo = getRealm().getRepository("konakart-repository");
            Node productsNode = konakartRepo.getNode("/products/");
            Node ordersNode = konakartRepo.getNode("/orders/");

            Node[] orderNodes = ordersNode.getNodes();
            if (orderNodes != null) {
                sb.append("<orders>");
                for (int i = 0; i < orderNodes.length; i++) {
                    //sb.append("<product id=\"" + productNodes[i].getName() + "\"/>");

                    Node orderNode = orderNodes[i];
                    //Node orderNode = ordersNode.getNode(orderNodes[i].getName() + "_" + language);
                    sb.append("<order id=\"" + orderNode.getName() + "\">");
                    try {
/*
                        log.warn("DEBUG: Product ID: " + productNode.getName());
                        log.warn("DEBUG: Product name: " + productNode.getProperty("name"));
*/
/* TODO ...
                        sb.append("<name><![CDATA[" + productNode.getProperty("name") + "]]></name>");
                        sb.append("<description><![CDATA[" + productNode.getProperty("description") + "]]></description>");
*/
                    } catch(Exception e) {
                        sb.append("<exception>Trying to get property: " + e.getMessage() + "</exception>");
                    }
                    sb.append("</order>");
                }
                sb.append("</orders>");
            } else {
                sb.append("<warn>No orders found!</warn>");
            }

            String nodeID = "17";
            if (productsNode.hasNode(nodeID + "_" + language)) {
                Node singleProductNode = productsNode.getNode(nodeID + "_" + language);
                sb.append("<product id=\"" + singleProductNode.getName() + "\" language=\"" + language + "\">");
                try {
                    sb.append("<name><![CDATA[" + singleProductNode.getProperty("name") + "]]></name>");
                    sb.append("<description><![CDATA[" + singleProductNode.getProperty("description") + "]]></description>");
                } catch(Exception e) {
                    sb.append("<exception>Trying to get property: " + e.getMessage() + "</exception>");
                }
                sb.append("</product>");
            } else {
                log.warn("No such product with ID '" + nodeID + "' and of language '" + language +"'!");
            }

            Node[] productNodes = productsNode.getNodes();
            if (productNodes != null) {
                sb.append("<products>");
                for (int i = 0; i < productNodes.length; i++) {
                    //sb.append("<product id=\"" + productNodes[i].getName() + "\"/>");

                    Node productNode = productsNode.getNode(productNodes[i].getName() + "_" + language);
                    sb.append("<product id=\"" + productNode.getName() + "\">");
                    try {
/*
                        log.warn("DEBUG: Product ID: " + productNode.getName());
                        log.warn("DEBUG: Product name: " + productNode.getProperty("name"));
*/
/* TODO ...
                        sb.append("<name><![CDATA[" + productNode.getProperty("name") + "]]></name>");
                        sb.append("<description><![CDATA[" + productNode.getProperty("description") + "]]></description>");
*/
                    } catch(Exception e) {
                        sb.append("<exception>Trying to get property: " + e.getMessage() + "</exception>");
                    }
                    sb.append("</product>");
                }
                sb.append("</products>");
            } else {
                sb.append("<warn>No products found!</warn>");
            }
        } catch(Exception e) {
            log.error(e, e);
            sb.append("<exception>" + e.getMessage() + "</exception>");
        }

/* INFO: Get environment variables
        java.util.Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            sb.append("<env-var>" + envName + ", " + env.get(envName) + "</env-var>");
        }
*/
 
        sb.append("</konakart>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
