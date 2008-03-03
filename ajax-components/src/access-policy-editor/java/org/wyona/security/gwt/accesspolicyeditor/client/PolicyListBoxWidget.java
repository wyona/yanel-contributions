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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Vector;

/**
 *
 */
public class PolicyListBoxWidget extends Composite implements ClickListener {

    private ListBox lb;
    private CheckBox readCB;
    private CheckBox writeCB;
    private CheckBox policyInheritanceCB;

    private VerticalPanel vp = new VerticalPanel();

    /**
     *
     */
    public PolicyListBoxWidget(int visibleItemCount, User[] users, Group[] groups, boolean useInheritedPolicies) {
        initWidget(vp);

        vp.add(new Label("Policy"));

        policyInheritanceCB = new CheckBox("Inherit rights from parent policies");
        setInheritRightsFlag(useInheritedPolicies);
        vp.add(policyInheritanceCB);

        lb = new ListBox(true);
        lb.addClickListener(this);
        setIdentities(visibleItemCount, users, groups);
        vp.add(lb);

        readCB = new CheckBox("Read");
        readCB.addClickListener(this);
        vp.add(readCB);
        writeCB = new CheckBox("Write");
        writeCB.addClickListener(this);
        vp.add(writeCB);
    }

    /**
     *
     */
    public void setIdentities(int visibleItemCount, User[] users, Group[] groups) {
        lb.clear();
        lb.setVisibleItemCount(visibleItemCount);
        if (users != null || groups != null) {
            if (users != null) {
                for (int i = 0; i < users.length; i++) {
                    String label = "u: " + users[i].getId() + " (Read,Write)";
                    String value = "u: " + users[i].getId();
                    lb.addItem(label, value);
                }
            }
            if (groups != null) {
                for (int i = 0; i < groups.length; i++) {
                    String label = "g: " + groups[i].getId() + " (Read,Write)";
                    String value = "g: " + groups[i].getId();
                    lb.addItem(label, value);
                }
            } else {
                Window.alert("No groups!");
            }
        } else {
            lb.addItem("No identities yet!");
        }
    }

    /**
     *
     */
    public void setInheritRightsFlag(boolean useInheritedPolicies) {
        //Window.alert("Set inherit rights checkbox: " + useInheritedPolicies);
        if (policyInheritanceCB != null) {
            policyInheritanceCB.setChecked(useInheritedPolicies);
        }
    }

    /**
     *
     */
    public ListBox getListBox() {
        return lb;
    }

    /**
     *
     */
    public void onClick(Widget sender) {
        if (sender == readCB || sender == writeCB) {
            String selectedIdentity = getSelectedItemValue();
            if (selectedIdentity != null) {
                if (sender == readCB) {
                    Window.alert("Add/Remove Read right from selected identity " + selectedIdentity + " from policy");
                    String[] currentRights = getRights(selectedIdentity);
                    String[] newRights;
                    if (readCB.isChecked()) {
                        newRights = addRight(currentRights, "Read");
                    } else {
                        newRights = removeRight(currentRights, "Read");
                    }
                    setListItem(newRights);
                } else if (sender == writeCB) {
                    Window.alert("Add/Remove Write right from selected identity " + selectedIdentity + " from policy");
                    String[] currentRights = getRights(selectedIdentity);
                    String[] newRights;
                    if (readCB.isChecked()) {
                        newRights = addRight(currentRights, "Write");
                    } else {
                        newRights = removeRight(currentRights, "Write");
                    }
                    setListItem(newRights);
                }
            } else {
                Window.alert("No identity has been selected! Please select an identity in order to assign rights.");
                readCB.setChecked(false);
                writeCB.setChecked(false);
            }
        } else if (sender == lb) {
            String selectedIdentity = getSelectedItemValue();

            //Window.alert("Update check boxes!");
            String[] rights = getRights(selectedIdentity);

            boolean hasReadBeenChecked = false;
            boolean hasWriteBeenChecked = false;
            for (int j = 0; j < rights.length; j++) {
                if (rights[j].equals("Read")) {
                    readCB.setChecked(true);
                    hasReadBeenChecked = true;
                } else if (rights[j].equals("Write")) {
                    writeCB.setChecked(true);
                    hasWriteBeenChecked = true;
                }
            }
            if (!hasReadBeenChecked) readCB.setChecked(false);
            if (!hasWriteBeenChecked) writeCB.setChecked(false);
        }
    }

    /**
     *
     */
    private String[] getRights(String identity) {
        if (identity.indexOf("(") > 0) {
            String rights = identity.substring(identity.indexOf("(") + 1, identity.indexOf(")"));
            //Window.alert("Rights: " + rights);
            return rights.split(",");
        } else {
            return new String[0];
        }
    }

    /**
     *
     */
    private String getIdentityWithoutRights(String identity) {
        if (identity.indexOf("(") > 0) {
            return identity.substring(0, identity.indexOf("(")).trim();
        } else {
            return identity.trim();
        }
    }

    /**
     *
     */
    private String getSelectedItemValue() {
        int i = lb.getSelectedIndex();
        if (i >= 0) {
            return lb.getItemText(i);
        }
        return null;
    }

    /**
     *
     */
    private String[] addRight(String[] currentRights, String right) {
        boolean hasRight = false;
        Vector newRights = new Vector();
        for (int i = 0; i < currentRights.length; i++) {
            if (currentRights[i].equals(right)) {
                hasRight = true;
            } else {
                newRights.add(currentRights[i]);
            }
        }
        if (!hasRight) newRights.add(right);

        String[] nRights = new String[newRights.size()];
        for (int i = 0; i < nRights.length; i++) {
            nRights[i] = (String) newRights.elementAt(i);
        }
        return nRights;
    }

    /**
     *
     */
    private String[] removeRight(String[] currentRights, String right) {
        Vector newRights = new Vector();
        for (int i = 0; i < currentRights.length; i++) {
            if (!currentRights[i].equals(right)) {
                newRights.add(currentRights[i]);
            }
        }

        String[] nRights = new String[newRights.size()];
        for (int i = 0; i < nRights.length; i++) {
            nRights[i] = (String) newRights.elementAt(i);
        }
        return nRights;
    }

    /**
     *
     */
    private void setListItem(String[] rights) {
        int index = lb.getSelectedIndex();
        if (index >= 0) {
            StringBuffer sb = new StringBuffer(getIdentityWithoutRights(getSelectedItemValue()));
            if (rights.length > 0) {
                sb.append(" (" + rights[0]);
                for (int j = 1; j < rights.length; j++) {
                sb.append("," + rights[j]);
                }
                sb.append(")");
            }
            lb.setItemText(index, sb.toString());
        } else {
            Window.alert("Exception: No list item selected!");
        }
    }

    /**
     *
     */
    public User[] getUsers() {
        Vector users = new Vector();
        for (int i = 0; i < lb.getItemCount(); i++) {
            String itemText = lb.getItemText(i);
            String[] rights = getRights(itemText);
            String id = getIdentityWithoutRights(itemText);
            if (id.startsWith("u:")) {
                users.add(new User(id.substring(2).trim(), rights));
            }
        }

        User[] u = new User[users.size()];
        for (int i = 0; i < u.length; i++) {
            u[i] = (User) users.elementAt(i);
        }
        return u;
    }

    /**
     *
     */
    public Group[] getGroups() {
        Vector groups = new Vector();
        for (int i = 0; i < lb.getItemCount(); i++) {
            String itemText = lb.getItemText(i);
            String[] rights = getRights(itemText);
            String id = getIdentityWithoutRights(itemText);
            if (id.startsWith("g:")) {
                groups.add(new Group(id.substring(2).trim(), rights));
            }
        }

        Group[] g = new Group[groups.size()];
        for (int i = 0; i < g.length; i++) {
            g[i] = (Group) groups.elementAt(i);
        }
        return g;
    }
}
