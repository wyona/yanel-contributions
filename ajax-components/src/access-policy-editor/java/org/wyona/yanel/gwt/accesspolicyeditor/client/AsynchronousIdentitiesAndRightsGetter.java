/*
 * Copyright 2008 Wyona
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.wyona.yanel.gwt.accesspolicyeditor.client;

import org.wyona.yanel.gwt.client.AsynchronousAgent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.user.client.Window;

import java.util.Vector;

/**
 * http://code.google.com/p/bunsenandbeaker/wiki/DevGuideHttpRequests
 */
public class AsynchronousIdentitiesAndRightsGetter extends AsynchronousAgent {

    Vector users = new Vector();

    /**
     *
     */
    public AsynchronousIdentitiesAndRightsGetter(String url) {
        super(url);
    }

    /**
     * See src/gallery/src/java/org/wyona/yanel/gwt/client/ui/gallery/AsynchronousGalleryBuilder.java
     * Also see src/access-policy-editor/java/org/wyona/yanel/gwt/accesspolicyeditor/public/sample-identities-and-usecases.xml
     */
    public void onResponseReceived(final Request request, final Response response) {
        // TODO: Parse response XML
        // http://groups.google.com/group/Google-Web-Toolkit/msg/a6f399bc4d46f795
        // http://code.google.com/p/bunsenandbeaker/wiki/DevGuideXML
        Element rootElement = XMLParser.parse(response.getText()).getDocumentElement();
        //Window.alert("Root element: " + rootElement.getTagName());
        Element usersElement = getFirstChildElement(rootElement, "users");
        NodeList userElements = usersElement.getElementsByTagName("user");
        for (int i = 0; i < userElements.getLength(); i++) {
            users.add(((Element) userElements.item(i)).getAttribute("id"));
            //Window.alert("User: " + (String) users.elementAt(i));
        }
        //Window.alert("Identities response processed!");
    }

    /**
     * Get users
     */
    public String[] getUsers() {
        //Window.alert("Number of users: " + users.size());
        String[] u = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            u[i] = (String) users.elementAt(i);
            //Window.alert("User: " + u[i]);
        }
        return u;
    }

    /**
     * Get groups
     */
    public String[] getGroups() {
        String[] g = new String[2];
        g[0] = "login";
        g[1] = "admin";
        return g;
    }

    /**
     * Get rights
     */
    public String[] getRights() {
        String[] r = new String[3];
        r[0] = "Read";
        r[1] = "Write";
        r[2] = "Toolbar";
        return r;
    }

    /**
     * Get first child with a specific tag name
     */
    private Element getFirstChildElement(Element parent, String name) {
        NodeList nl = parent.getElementsByTagName(name);
        return (Element) nl.item(0);
    }
}
