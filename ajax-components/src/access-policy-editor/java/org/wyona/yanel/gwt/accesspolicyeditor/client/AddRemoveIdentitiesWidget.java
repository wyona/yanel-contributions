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

    private FlowPanel fp = new FlowPanel();

    private Button addButton;
    private Button removeButton;

    /**
     *
     */
    public AddRemoveIdentitiesWidget(ListBox identitiesListBox) {
        initWidget(fp);

        removeButton = new Button("<");
        fp.add(removeButton);
        addButton = new Button(">");
        fp.add(addButton);
        this.identitiesLB = identitiesListBox;
    }

    /**
     *
     */
    public void onClick(Widget sender) {
        if (sender == addButton) {
            Window.alert("Add selected identity to policy");
        } else if (sender == removeButton) {
            Window.alert("Remove selected identity from policy");
        }

        //String selectedIdentity = identitiesLB.getValue(identitiesLB.getSelectedIndex());
        //Window.alert("Add selected identity " + selectedIdentity + " to policy");
        Window.alert("Add selected identity to policy");
        identitiesLB.clear();
    }
}
