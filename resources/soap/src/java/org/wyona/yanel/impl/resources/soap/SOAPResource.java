/*
 * Copyright 2007 Wyona
 */

package org.wyona.yanel.impl.resources.soap;

import java.io.ByteArrayInputStream;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

public class SOAPResource extends Resource implements ViewableV2{
	private static String ENVELOPE =
		"<?xml version='1.0'?>"+
		"<soap:Envelope xmlns:soap='http://www.w3.org/2001/12/soap-envelope' soap:encodingStyle='http://www.w3.org/2001/12/soap-encoding'>"+
			"<soap:Body xmlns:o='http://www.optaros.com'>" +
				"<o:exampleResponse>" +
					" Hello World!" +
				"</o:exampleResponse>" +
			"</soap:Body>" +
		"</soap:Envelope>";
	
    public SOAPResource() {
    }
    
    public boolean exists() throws Exception {
    	return true;
    }
    
    /**
     *
     */
    private String getMimeType(String viewId) throws Exception {
        if (getResourceConfigProperty("mime-type") != null) return getResourceConfigProperty("mime-type");
    	return "application/soap+xml";
    }
    
    public long getSize() throws Exception {
    	return ENVELOPE.getBytes().length;
    }
    
    public View getView(String viewId) throws Exception {
    	
    	System.out.println(super.getConfiguration().getProperty("encoding"));
    	
    	View v = new View();
    	v.setInputStream( new ByteArrayInputStream(ENVELOPE.getBytes()));
    	// This is important!
    	v.setMimeType(getMimeType(viewId));
    	return v;
    }
    
    public ViewDescriptor[] getViewDescriptors() {
    	return null;
    }
}
