/*
 * Copyright 2007 Wyona
 */

package org.wyona.yanel.impl.resources.findfriend;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.meguni.parser.Parser;
import org.wyona.meguni.util.ResultSet;

/**
 *
 */
public class FindFriendResource extends Resource implements ViewableV2 {

    /**
     *
     */
    public FindFriendResource() {
    }

    /**
     *
     */
    public boolean exists() {
        return true;
    }

    /**
     *
     */
    public String getMimeType(String viewId) {
        return "application/xml";
    }

    /**
     *
     */
    public long getSize() {
        return -1;
    }

    /**
     *
     */
    public View getView(String viewId) {
        StringBuffer sb = new StringBuffer("<foaf>");
        try {
            String qs = getRequest().getParameter("q");
            ResultSet resultSet = getSearchResults(qs.replaceAll(" ", "+") + "+FOAF");
        if (resultSet != null && resultSet.size() > 0) {
            sb.append("<provider source-name=\"" + resultSet.getSourceName() + "\" source-domain=\"" + resultSet.getSourceDomain() + "\" numberOfResults=\""+resultSet.size()+"\">");
            for (int k = 0;k < resultSet.size(); k++) {
                sb.append("<result number=\"" + (k+1) + "\" source-name=\"" + resultSet.getSourceName() + "\">");
                sb.append("<title><![CDATA[" + resultSet.get(k).title + "]]></title>");
                sb.append("<excerpt><![CDATA[" + resultSet.get(k).excerpt + "]]></excerpt>");
                sb.append("<url><![CDATA[" + resultSet.get(k).url + "]]></url>");
                sb.append("<last-modified><![CDATA[" + resultSet.get(k).lastModified + "]]></last-modified>");
                sb.append("</result>");
            }
            sb.append("</provider>");
        }
        } catch (Exception e) {
            sb.append("<exception>" + e.getMessage() + "</exception>");
        }
        sb.append("</foaf>");

        View view = new View();
        view.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
        view.setMimeType(getMimeType(viewId));
        return view;
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
    private ResultSet getSearchResults(String queryString) throws Exception {
        String className = getResourceConfigProperty("parser");
        if (className == null) className = "org.wyona.meguni.parser.impl.MSNParser";
        Parser parser = (Parser) Class.forName(className).newInstance();
        return parser.parse(queryString);
    }
}
