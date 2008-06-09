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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.wyona.security.gwt.accesspolicyeditor.client.AddRemoveIdentitiesWidget;
import org.wyona.yanel.gwt.client.AsynchronousAgent;

/**
 * Access Policy Editor
 */
public class AccessPolicyEditor implements EntryPoint {

    User[] policyUsers;
    Group[] policyGroups;
    boolean useInheritedPolicies = true;

    IdentitiesListBoxWidget identitiesLBW;
    PolicyListBoxWidget policyLBW;
    Button saveButton;

    String[] identitiesAllUsers;
    String[] identitiesAllGroups;
    Right[] allRights;
    
    int visibleItemCount = 20;

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
        getIdentitiesAndRights(identitiesURL); // TODO: Subtract policy identities!

        identitiesLBW = new IdentitiesListBoxWidget(visibleItemCount);
        
        identitiesLBW.set(visibleItemCount, identitiesAllUsers, identitiesAllGroups);
        
        policyLBW = new PolicyListBoxWidget(visibleItemCount, policyUsers, policyGroups, useInheritedPolicies);

        
        getPolicy(readPolicyURL);

        // Setup GUI
        VerticalPanel vp = new VerticalPanel();
        RootPanel.get("access-policy-editor-hook").add(vp);

        VerticalPanel searchFilterVP = new VerticalPanel();
        vp.add(searchFilterVP);

        final TextBox searchTB = new TextBox();
        searchTB.setVisibleLength(30);
        searchFilterVP.add(searchTB);

        searchTB.addKeyboardListener(
                new KeyboardListenerAdapter() {
                    public void onKeyUp(Widget sender, char keyCode, int modifiers) {
                        ListBox idlb = identitiesLBW.getListBox();
                        ArrayList tmpUsers = new ArrayList();
                        ArrayList tmpGroups = new ArrayList();
                        idlb.clear();
                        int itemCountU = identitiesAllUsers.length;
                        for (int i = 0; i < itemCountU; i++) {
                            String itemText = identitiesAllUsers[i];
                            if (itemText.indexOf(searchTB.getText()) >= 0) {
                                tmpUsers.add(itemText);
                            }
                        }
                        int itemCountG = identitiesAllGroups.length;
                        for (int i = 0; i < itemCountG; i++) {
                            String itemText = identitiesAllGroups[i];
                            if (itemText.indexOf(searchTB.getText()) >= 0) {
                                tmpGroups.add(itemText);
                            }
                        }
                        
                        String tmpUsersStr [] = new String [tmpUsers.size ()];
                        tmpUsers.toArray(tmpUsersStr);
                        String tmpGroupStr [] = new String [tmpGroups.size ()];
                        tmpGroups.toArray(tmpGroupStr);
                        
                        identitiesLBW.set(visibleItemCount, tmpUsersStr, tmpGroupStr);
                        // filterList(list, filter.getText());
                    }
                });
        
        
//        Button searchButton = new Button("Search User or Group", new ClickListener() {
//            public void onClick(Widget sender) {
//                int itemCount = identitiesLBW.getListBox().getItemCount();
//                for (int i = 0; i < itemCount; i++) {
//                    String itemText = identitiesLBW.getListBox().getItemText(i);
//                    if (itemText.indexOf(searchTB.getText()) >= 0) Window.alert("Result: " + itemText);
//                    
//                }
//            }
//  
//        });
//        searchFilterVP.add(searchButton);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        vp.add(hp);

	
        HorizontalPanel buttonHP = new HorizontalPanel();
        vp.add(buttonHP);
        //buttonHP.add(new Button("Apply Policy"));

        // Save Button
        final String savePolicyUrl = GWT.getHostPageBaseURL() + savePolicyURL.replaceAll("&amp;", "&");
        //saveButton = new Button("Save Policy and Exit", new ClickListener() {
        saveButton = new Button("Save Policy", new ClickListener() {
            public void onClick(Widget sender) {
                final AsynchronousPolicySetter aps = new AsynchronousPolicySetter(savePolicyUrl);
                try {
                    com.google.gwt.http.client.Request request = aps.sendRequest(policyLBW.getUsers(), policyLBW.getGroups(), policyLBW.getUseInheritedPolicies());
                    // TODO: Disable button during save (start a timer in order to enable when response has been received)
                    //saveButton.setEnabled(false);
                } catch (Exception e) {
                    Window.alert("Exception: " + e.getMessage());
                }
            }
        });
        saveButton.setStyleName("gwt-wyona-SaveButton");
        buttonHP.add(saveButton);

        // Cancel Button
        final String cancelUrl = cancelURL;
        Button cancelButton = new Button("Cancel", new ClickListener() {
            public void onClick(Widget sender) {
                //Window.alert("Redirect to " + cancelUrl);
                redirectTo(GWT.getHostPageBaseURL() + cancelUrl);
            }
            public native void redirectTo(String url) /*-{
                $wnd.location.href=url;
            }-*/; 
        });
        saveButton.setStyleName("gwt-wyona-CancelButton");
        buttonHP.add(cancelButton);

        

        
        AddRemoveIdentitiesWidget ariw = new AddRemoveIdentitiesWidget(identitiesLBW.getListBox(), policyLBW.getListBox(), policyLBW);
        ariw.setStyleName("gwt-wyona-AddRemoveWidget");

        //Button removeIdentityButton = new Button("DEBUG", new AddRemoveClickListener(identitiesLB));

        hp.add(identitiesLBW);
        hp.add(ariw);
        hp.add(policyLBW);
    }

    /**
     * Get identities and rights
     */
    private void getIdentitiesAndRights(String url) {
        if (identitiesAllUsers == null || identitiesAllGroups == null || allRights == null) {
            //Window.alert("Load identities: " + url);
            url = GWT.getHostPageBaseURL() + url.replaceAll("&amp;", "&");
            //Window.alert("Load IdentitiesAndRights: "+ url);
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
                            // "Redraw" Listbox
                            //identitiesLBW.set(visibleItemCount, ag.getUsers(), ag.getGroups());
                            // TODO: "Redraw" Policy Listbox
                            //policyLBW.set();
                            
                            allRights = ag.getRights();
                            identitiesAllUsers = ag.getUsers();
                            identitiesAllGroups = ag.getGroups();
                            this.cancel();
                            if (allRights.length > 0 || identitiesAllUsers.length > 0 || identitiesAllGroups.length > 0 ) {
                                policyLBW.set(allRights);
                                identitiesLBW.set(visibleItemCount, identitiesAllUsers, identitiesAllGroups);
                                //Window.alert("Identities have been loaded!" + allRights.length + " " + identitiesAllUsers.length + " " + identitiesAllGroups.length);
                            } else {
                                Window.alert("No Identities have been loaded!");
                            }
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
    }

    /**
     * Get policy
     */
    private void getPolicy(String url) {
        url = GWT.getHostPageBaseURL() + url.replaceAll("&amp;", "&");
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
                        
                        //remove policy users from users
                        ArrayList tmpUsers = new ArrayList(Arrays.asList(identitiesAllUsers));
                        ArrayList tmpGroups = new ArrayList(Arrays.asList(identitiesAllGroups));
                        
                        
                        
                        
//                        int itemCountUI = identitiesAllUsers.length;
//                        for (int j = 0; j < itemCountUI; j++) {
//                            String itemTextIAU = identitiesAllUsers[j];
//                            if (!itemText.equals(itemTextIAU) && !tmpUsers.contains(itemTextIAU)) {
//                                tmpUsers.add(itemTextIAU);
//                            } else {
//                                //Window.alert("not add: " + itemTextIAU + " = " +  itemText);
//                            }
//                        }
                        int itemCountUP = policyUsers.length;
                        for (int i = 0; i < itemCountUP; i++) {
                            String itemText = policyUsers[i].getId();
                            tmpUsers.remove(itemText);
                            
                        }

                        int itemCountGP = policyGroups.length;
                        for (int i = 0; i < itemCountGP; i++) {
                            String itemText = policyGroups[i].getId();
                            tmpGroups.remove(itemText);
                        }

//                        int itemCountUI = identitiesAllGroups.length;
//                        for (int j = 0; j < itemCountUI; j++) {
//                            String itemTextIAG = identitiesAllGroups[j];
//                            if (!itemText.equals(itemTextIAG)  && !tmpGroups.contains(itemTextIAG)) {
//                                tmpGroups.add(itemTextIAG);
//                            } else {
//                                //Window.alert("not add: " + itemTextIAG + " = " +  itemText);
//                            }
//                        }
                        
                        
                        
                        String tmpUsersStr [] = new String [tmpUsers.size ()];
                        tmpUsers.toArray(tmpUsersStr);
                        identitiesAllUsers = tmpUsersStr;
                        
                        String tmpGroupStr [] = new String [tmpGroups.size ()];
                        tmpGroups.toArray(tmpGroupStr);
                        identitiesAllGroups = tmpGroupStr;
                        
                        identitiesLBW.set(visibleItemCount, new String[0], new String[0]);
                        identitiesLBW.set(visibleItemCount, identitiesAllUsers, identitiesAllGroups);
                        //Window.alert("Policy has been loaded!");
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
