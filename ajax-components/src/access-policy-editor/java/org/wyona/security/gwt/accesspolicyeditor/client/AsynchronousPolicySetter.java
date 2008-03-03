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
package org.wyona.security.gwt.accesspolicyeditor.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

/**
 * http://code.google.com/p/bunsenandbeaker/wiki/DevGuideHttpRequests
 * http://code.google.com/p/bunsenandbeaker/wiki/DevGuideXML
 */
public class AsynchronousPolicySetter implements RequestCallback {

    private RequestBuilder requestBuilder = null;

    /**
     *
     */
    public AsynchronousPolicySetter(String url) {
        Window.alert("Save policy to: " + url);
        requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
    }

    /**
     *
     */
    public Request sendRequest(User[] users) throws RequestException {
        StringBuffer data = new StringBuffer("<?xml version=\"1.0\"?>");
	data.append("<policy>");
        if (users != null) {
            for (int i = 0; i < users.length; i++) {
                data.append("<user id=\"" + users[i].getId() + "\">");
                String[] rights = users[i].getRights();
                if (rights != null) {
                    for (int k = 0; k < rights.length; k++) {
                        data.append("<right id=\"" + rights[k] + "\">" + rights[k] + "</right>");
                    }
                }
                data.append("</user>");
            }
        }
	data.append("</policy>");
        return requestBuilder.sendRequest(data.toString(), this);
    }

    /**
     *
     */
    public void onResponseReceived(Request request, Response response) {
        Window.alert("Response received!");
    }

    /**
     *
     */
    public void onError(Request request, Throwable exception) {
        Window.alert("Exception: " + exception.getMessage());
    }
}
