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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.wyona.security.gwt.accesspolicyeditor.client.AddRemoveIdentitiesWidget;

/**
 * Access Policy Editor
 */
public class AccessPolicyEditor implements EntryPoint {

    String[] users;
    String[] groups;
    String[] rights;
    User[] policyUsers;
    Group[] policyGroups;
    boolean useInheritedPolicies = true;

    IdentitiesListBoxWidget identitiesLBW;
    PolicyListBoxWidget policyLBW;

    int visibleItemCount = 10;

    /**
     *
     */
    public void onModuleLoad() {
        String identitiesURL = "DEFAULT-identities-and-usecases.xml";
        String readPolicyURL = "DEFAULT-policy.xml";
        String cancelURL = "DEFAULT-cancel.html";
        String savePolicyURL = "DEFAULT-save-policy.xml";
        // Get URLs from host/html page
        try {
            Dictionary dict = Dictionary.getDictionary("getURLs");
            identitiesURL = dict.get("identities-url");
            readPolicyURL = dict.get("policy-url");
            cancelURL = dict.get("cancel-url");
            savePolicyURL = dict.get("save-url");
        } catch (java.util.MissingResourceException e) {
            Window.alert("Exception: " + e.getMessage());
        }

        // Get data from server
        getPolicy(readPolicyURL);
        getIdentitiesAndRights(identitiesURL); // TODO: Subtract policy identities!

        // Setup GUI
        VerticalPanel vp = new VerticalPanel();
        RootPanel.get().add(vp);

        VerticalPanel searchFilterVP = new VerticalPanel();
        vp.add(searchFilterVP);

        TextBox searchTB = new TextBox();
        searchTB.setVisibleLength(30);
        searchFilterVP.add(searchTB);
        searchFilterVP.add(new Button("Search within Identities"));

        HorizontalPanel hp = new HorizontalPanel();
        vp.add(hp);
        //vp.add(new Button("Apply Policy"));

        // Save Button
        final String savePolicyUrl = savePolicyURL;
        Button saveButton = new Button("Save Policy and Exit", new ClickListener() {
            public void onClick(Widget sender) {
                final AsynchronousPolicySetter aps = new AsynchronousPolicySetter(savePolicyUrl);
                try {
                    com.google.gwt.http.client.Request request = aps.sendRequest(policyLBW.getUsers(), policyLBW.getGroups());
                } catch (Exception e) {
                    Window.alert("Exception: " + e.getMessage());
                }
            }
        });
        vp.add(saveButton);

        // Cancel Button
        final String cancelUrl = cancelURL;
        Button cancelButton = new Button("Cancel", new ClickListener() {
            public void onClick(Widget sender) {
                Window.alert("Redirect to " + cancelUrl);
                redirectTo(cancelUrl);
            }
            public native void redirectTo(String url) /*-{
                $wnd.location.href=url;
            }-*/; 
        });
        vp.add(cancelButton);

        identitiesLBW = new IdentitiesListBoxWidget(visibleItemCount, users, groups);

        policyLBW = new PolicyListBoxWidget(visibleItemCount, policyUsers, policyGroups, useInheritedPolicies);

	AddRemoveIdentitiesWidget ariw = new AddRemoveIdentitiesWidget(identitiesLBW.getListBox(), policyLBW.getListBox(), policyLBW);

        //Button removeIdentityButton = new Button("DEBUG", new AddRemoveClickListener(identitiesLB));

        hp.add(identitiesLBW);
        hp.add(ariw);
        hp.add(policyLBW);
    }

    /**
     * Get identities and rights
     */
    private void getIdentitiesAndRights(String url) {
        //Window.alert("Load identities: " + url);
        final AsynchronousIdentitiesAndRightsGetter ag = new AsynchronousIdentitiesAndRightsGetter(url);
        try {
            final com.google.gwt.http.client.Request request = ag.execute();

            // Start new thread
            Timer t = new Timer() {
                public void run() {
                    if (request.isPending()) {
                        // TODO: Show loading ...
                        scheduleRepeating(10);
                    } else {
                        users = ag.getUsers();
                        groups = ag.getGroups();
                        rights = ag.getRights();
                        // "Redraw" Listbox
                        identitiesLBW.set(visibleItemCount, users, groups);
                        this.cancel();
                        Window.alert("Identities have been loaded!");
                    }
                }
            };
            t.schedule(1);

        } catch (Exception e) {
             //if (!com.google.gwt.core.client.GWT.isScript()) {
             e.printStackTrace();
             //}
        }
    }

    /**
     * Get policy
     */
    private void getPolicy(String url) {
        //Window.alert("Load policy: " + url);
        final AsynchronousPolicyGetter apg = new AsynchronousPolicyGetter(url);
        try {
            final com.google.gwt.http.client.Request request = apg.execute();

            // Start new thread
            Timer t = new Timer() {
                public void run() {
                    if (request.isPending()) {
                        // TODO: Show loading ...
                        scheduleRepeating(10);
                    } else {
                        policyUsers = apg.getUsers();
                        policyGroups = apg.getGroups();
                        // "Redraw" Listbox
                        policyLBW.setIdentities(visibleItemCount, policyUsers, policyGroups);

                        useInheritedPolicies = apg.getUseInheritedPolicies();
                        policyLBW.setInheritRightsFlag(useInheritedPolicies);

                        this.cancel();
                        Window.alert("Policy has been loaded!");
                    }
                }
            };
            t.schedule(1);

        } catch (Exception e) {
             Window.alert("Exception: " + e.getMessage());
             //if (!com.google.gwt.core.client.GWT.isScript()) {
             e.printStackTrace();
             //}
        }
    }
}

/**
 *
 */
class AddRemoveClickListener implements ClickListener {
     private ListBox lb;

     public AddRemoveClickListener (ListBox lb) {
         this.lb = lb;
     }

     public void onClick(Widget sender) {
         Window.alert("Hello DEBUG");
     }
}
