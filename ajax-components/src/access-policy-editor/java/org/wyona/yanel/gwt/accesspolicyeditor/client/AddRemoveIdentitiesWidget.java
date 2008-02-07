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

/**
 *
 */
public class AddRemoveIdentitiesWidget extends Composite implements ClickListener {

    private ListBox identitiesLB;
    private ListBox policyLB;

    private FlowPanel fp = new FlowPanel();

    private Button addButton;
    private Button removeButton;

    /**
     *
     */
    public AddRemoveIdentitiesWidget(ListBox identitiesListBox, ListBox policyListBox) {
        initWidget(fp);

        removeButton = new Button("<", this);
        fp.add(removeButton);
        addButton = new Button(">", this);
        fp.add(addButton);

        this.identitiesLB = identitiesListBox;
        this.policyLB = policyListBox;
    }

    /**
     *
     */
    public void onClick(Widget sender) {
        if (sender == addButton) {
            int i = identitiesLB.getSelectedIndex();
            String selectedIdentity = identitiesLB.getValue(i);
            Window.alert("Add selected identity " + selectedIdentity + " to policy");
            identitiesLB.removeItem(i);
            policyLB.addItem(selectedIdentity);
        } else if (sender == removeButton) {
            int i = policyLB.getSelectedIndex();
            String selectedIdentity = policyLB.getValue(i);
            Window.alert("Remove selected identity " + selectedIdentity + " from policy");
            policyLB.removeItem(i);
            identitiesLB.addItem(removeRights(selectedIdentity));
        }
    }

    /**
     * Remove rights from string, e.g. "U: alice (Read, Write)" -> "U: alice"
     */
    private String removeRights(String identity) {
        if (identity.indexOf("(") > 0) {
            return identity.substring(0, identity.indexOf("("));
        } else {
            return identity;
        }
    }
}
