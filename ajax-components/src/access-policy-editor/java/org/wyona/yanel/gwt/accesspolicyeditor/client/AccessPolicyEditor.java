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

import org.wyona.yanel.gwt.accesspolicyeditor.client.AddRemoveIdentitiesWidget;

/**
 * Access Policy Editor
 */
public class AccessPolicyEditor implements EntryPoint {

    String[] users;
    String[] groups;
    String[] rights;

    /**
     *
     */
    public void onModuleLoad() {
        try {
            Dictionary dict = Dictionary.getDictionary("getURLs");
            String identitiesURL = dict.get("identities-url");
            Window.alert("Identities URL: " + identitiesURL);
            String policyURL = dict.get("policy-url");
            Window.alert("Policy URL: " + policyURL);
        } catch (java.util.MissingResourceException e) {
            Window.alert("Exception: " + e.getMessage());
        }

        // Get data from server
        getIdentitiesAndRights();
        String[] policyIdentities = getPolicy();

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
        vp.add(new Button("Save Policy and Exit"));
        vp.add(new Button("Cancel"));

        int visibleItemCount = 10;

        IdentitiesListBoxWidget identitiesLBW = new IdentitiesListBoxWidget(visibleItemCount, users, groups);

        PolicyListBoxWidget policyLBW = new PolicyListBoxWidget(visibleItemCount, policyIdentities);

	AddRemoveIdentitiesWidget ariw = new AddRemoveIdentitiesWidget(identitiesLBW.getListBox(), policyLBW.getListBox());

        //Button removeIdentityButton = new Button("DEBUG", new AddRemoveClickListener(identitiesLB));

        hp.add(identitiesLBW);
        hp.add(ariw);
        hp.add(policyLBW);
    }

    /**
     * Get identities and rights
     */
    private void getIdentitiesAndRights() {
        // TODO: See src/extra/globus/image-browser/src/java/ch/globus/yanel/gwt/client/ImageBrowser.java how to use Asyn Identities and Rights Getter!

        final AsynchronousIdentitiesAndRightsGetter ag = new AsynchronousIdentitiesAndRightsGetter("sample-identities-and-usecases.xml");
        try {
            final com.google.gwt.http.client.Request request = ag.execute();
            // TODO: Implement loop until request has finished execution
            //Window.alert("Just a second to process the identities response ...");

            Timer t = new Timer() {
                public void run() {
                    if (request.isPending()) {
                        scheduleRepeating(10);
                    } else {
                        this.cancel();
                    }
                }
            };
/*
            while(request.isPending()) {
                Window.alert("Response not processed yet!");
            }
*/
        } catch (Exception e) {
             //if (!com.google.gwt.core.client.GWT.isScript()) {
             e.printStackTrace();
             //}
        }

        // TODO: Do not set them globally!
        users = ag.getUsers();
        groups = ag.getGroups();
        rights = ag.getRights();
    }

    /**
     * Get policy
     */
    private String[] getPolicy() {
        // TODO: See src/extra/globus/image-browser/src/java/ch/globus/yanel/gwt/client/ImageBrowser.java how to use Asyn Policy Getter!

        final AsynchronousPolicyGetter apg = new AsynchronousPolicyGetter("sample-policy.xml");
        try {
            final com.google.gwt.http.client.Request request = apg.execute();
            //Window.alert("Just a second to process the policy response ...");
            // TODO: Implement loop until request has finished execution
        } catch (Exception e) {
             Window.alert("Exception: " + e.getMessage());
             //if (!com.google.gwt.core.client.GWT.isScript()) {
             e.printStackTrace();
             //}
        }
        return apg.getIdentities();
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
