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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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

        HorizontalPanel hp = new HorizontalPanel();
        vp.add(hp);

        int visibleItemCount = 10;

        ListBox identitiesLB = new ListBox();
        identitiesLB.setVisibleItemCount(visibleItemCount);
        identitiesLB.addItem("U: michi");
        identitiesLB.addItem("U: levi");

        ListBox policyLB = new ListBox();
        policyLB.setVisibleItemCount(visibleItemCount);
        policyLB.addItem("U: alice");

        Button addIdentityButton = new Button(">", new ClickListener() {
            public void onClick(Widget sender) {
                Window.alert("Add selected identity to policy");
            }
        });

        Button removeIdentityButton = new Button("<", new ClickListener() {
            public void onClick(Widget sender) {
                Window.alert("Remove selected identity from policy");
            }
        });

        hp.add(identitiesLB);
        hp.add(removeIdentityButton);
        hp.add(addIdentityButton);
        hp.add(policyLB);
    }
}
