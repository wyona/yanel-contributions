/*
 * Copyright 2006 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.impl.resources;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Calendar;
import java.io.StringBufferInputStream;
//import java.io.StringReader;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yarep.util.YarepUtil;

/**
 * 
 */
public class ExampleResource extends Resource implements ViewableV1 {

    private static Category log = Category.getInstance(ExampleResource.class);

    /**
     * 
     */
    public ExampleResource() {
    }

    /**
     * 
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }
    
    /**
     * 
     */
    public View getView(Path path, String viewId) {
        View defaultView = new View();
        defaultView.setMimeType("application/xml");
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        defaultView.setInputStream(new java.io.StringBufferInputStream(sb
                .toString()));
        return defaultView;
    }

    /**
     * @throws Exception
     * 
     */
    public View getView(HttpServletRequest request, String viewId)
            throws Exception {
        Path path = new Path(request.getServletPath());
        View defaultView = new View();
        return plainRequest(path, defaultView);

    }

    private View plainRequest(Path path, View defaultView) throws Exception {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        sb.append("<title>Example Resource</title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div id=\"contenBody\">");
        sb.append("<h1>"+this.getTime()+"</h1>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        defaultView.setMimeType("application/xhtml+xml");
        defaultView.setInputStream(new java.io.StringBufferInputStream(sb.toString()));

        return defaultView;
    }

    /**
     *
     */
    private String getTime(){
        Calendar cal = Calendar.getInstance(java.util.TimeZone.getDefault());
      
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);

        sdf.setTimeZone(java.util.TimeZone.getDefault());          
            
        return sdf.format(cal.getTime());
    }
}
