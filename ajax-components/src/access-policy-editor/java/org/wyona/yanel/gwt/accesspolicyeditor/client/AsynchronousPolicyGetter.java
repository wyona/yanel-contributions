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
 *
 */
public class AsynchronousPolicyGetter extends AsynchronousAgent {

    Vector identities = new Vector();

    /**
     *
     */
    public AsynchronousPolicyGetter(String url) {
        super(url);
    }

    /**
     * See src/gallery/src/java/org/wyona/yanel/gwt/client/ui/gallery/AsynchronousGalleryBuilder.java
     * Also see src/access-policy-editor/java/org/wyona/yanel/gwt/accesspolicyeditor/public/sample-identities-and-usecases.xml
     */
    public void onResponseReceived(final Request request, final Response response) {
        Element rootElement = XMLParser.parse(response.getText()).getDocumentElement();
        //Window.alert("Root element: " + rootElement.getTagName());
        identities.add("u: jim (Write)");
        //Window.alert("Policy response processed!");
    }

    /**
     * Get identities from access policy
     */
    public String[] getIdentities() {
        String[] ids = new String[3];
        ids[0] = "u: alice (Read,Write)";
        ids[1] = "u: bob (Read)";
        ids[2] = "WORLD";
        return ids;
    }
}
