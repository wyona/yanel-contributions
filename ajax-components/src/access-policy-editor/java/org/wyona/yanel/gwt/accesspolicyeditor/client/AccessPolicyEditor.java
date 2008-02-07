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

    /**
     *
     */
    public void onModuleLoad() {
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
        vp.add(new Button("Apply Policy"));
        vp.add(new Button("Save Policy and Exit"));
        vp.add(new Button("Cancel"));

        int visibleItemCount = 10;

        IdentitiesListBoxWidget identitiesLBW = new IdentitiesListBoxWidget(visibleItemCount);

        PolicyListBoxWidget policyLBW = new PolicyListBoxWidget(visibleItemCount);

	AddRemoveIdentitiesWidget ariw = new AddRemoveIdentitiesWidget(identitiesLBW.getListBox(), policyLBW.getListBox());

        //Button removeIdentityButton = new Button("DEBUG", new AddRemoveClickListener(identitiesLB));

        hp.add(identitiesLBW);
        hp.add(ariw);
        hp.add(policyLBW);
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
