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

    private String READ_RIGHT = "r";
    //private String READ_RIGHT = "Read";
    private String WRITE_RIGHT = "w";
    //private String WRITE_RIGHT = "Write";

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
                    String label = "u: ("+READ_RIGHT+","+WRITE_RIGHT+") " + users[i].getId();
                    String value = "u: " + users[i].getId();
                    lb.addItem(label, value);
                }
            }
            if (groups != null) {
                for (int i = 0; i < groups.length; i++) {
                    String label = "g: ("+READ_RIGHT+","+WRITE_RIGHT+") " + groups[i].getId();
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
            String selectedIdentity = getSelectedItemText();
            if (selectedIdentity != null) {
                if (sender == readCB) {
                    String[] currentRights = getRights(selectedIdentity);
                    String[] newRights;
                    if (readCB.isChecked()) {
                        Window.alert("Add Read right from selected identity " + selectedIdentity + " from policy");
                        newRights = addRight(currentRights, READ_RIGHT);
                    } else {
                        Window.alert("Remove Read right from selected identity " + selectedIdentity + " from policy");
                        newRights = removeRight(currentRights, READ_RIGHT);
                    }
                    setSelectedListItem(newRights);
                } else if (sender == writeCB) {
                    String[] currentRights = getRights(selectedIdentity);
                    String[] newRights;
                    if (writeCB.isChecked()) {
                        Window.alert("Add Write right from selected identity " + selectedIdentity + " from policy");
                        newRights = addRight(currentRights, WRITE_RIGHT);
                    } else {
                        Window.alert("Remove Write right from selected identity " + selectedIdentity + " from policy");
                        newRights = removeRight(currentRights, WRITE_RIGHT);
                    }
                    setSelectedListItem(newRights);
                }
            } else {
                Window.alert("No identity has been selected! Please select an identity in order to assign rights.");
                readCB.setChecked(false);
                writeCB.setChecked(false);
            }
        } else if (sender == lb) {
            String selectedIdentity = getSelectedItemText();

            //Window.alert("Update check boxes!");
            String[] rights = getRights(selectedIdentity);

            boolean hasReadBeenChecked = false;
            boolean hasWriteBeenChecked = false;
            for (int j = 0; j < rights.length; j++) {
                if (rights[j].equals(READ_RIGHT)) {
                    readCB.setChecked(true);
                    hasReadBeenChecked = true;
                } else if (rights[j].equals(WRITE_RIGHT)) {
                    writeCB.setChecked(true);
                    hasWriteBeenChecked = true;
                }
            }
            if (!hasReadBeenChecked) readCB.setChecked(false);
            if (!hasWriteBeenChecked) writeCB.setChecked(false);
        }
    }

    /**
     * Get rights from identity string, e.g. "u: (r,w) alice"
     */
    private String[] getRights(String identity) {
        if (identity.indexOf("(") > 0) {
            String[] rightsString = identity.substring(identity.indexOf("(") + 1, identity.indexOf(")")).split(",");
            //Window.alert("Rights: " + rights);
            Vector r = new Vector();
            for (int i = 0; i < rightsString.length; i++) {
                if (!rightsString[i].equals("-")) r.add(rightsString[i]);
            }
            String[] rights = new String[r.size()];
            for (int i = 0; i < rights.length; i++) {
                rights[i] = (String) r.elementAt(i);
            }
            return rights;
        } else {
            return new String[0];
        }
    }

    /**
     * @return Identity without rights, e.g. "u: alice" or "g: editors"
     */
    private String getIdentityWithoutRights(int index) {
        //Window.alert(getSelectedItemValue() + " --- " + getSelectedItemText());
        return lb.getValue(index);
    }

    /**
     *
     */
    private String getSelectedItemValue() {
        int i = lb.getSelectedIndex();
        if (i >= 0) {
            return lb.getValue(i);
        }
        return null;
    }

    /**
     *
     */
    private String getSelectedItemText() {
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
        //Window.alert("addRight(): Number of current rights: " + currentRights.length);

        // Copy all current rights
        Vector newRights = new Vector();
        for (int i = 0; i < currentRights.length; i++) {
            newRights.add(currentRights[i]);
        }

        // Add new right if it doesn't exist yet
        boolean hasRightAlready = false;
        for (int i = 0; i < currentRights.length; i++) {
            if (currentRights[i].equals(right)) {
                hasRightAlready = true;
                break;
            }
        }
        if (!hasRightAlready) newRights.add(right);

        String[] nRights = new String[newRights.size()];
        for (int i = 0; i < nRights.length; i++) {
            nRights[i] = (String) newRights.elementAt(i);
        }
        //Window.alert("addRight(): Number of new rights: " + nRights.length);
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
    private void setSelectedListItem(String[] rights) {
        int index = lb.getSelectedIndex();
        if (index >= 0) {
            String id = getIdentityWithoutRights(index);
            setListItem(id.substring(0, 1), id.substring(2).trim(), rights, index);
        } else {
            Window.alert("Exception: No list item selected!");
        }
    }

    /**
     * @param type u for user and g for group
     * @param id
     * @param rights Rights
     * @param index Position of list item
     */
    private void setListItem(String type, String id, String[] rights, int index) {
        StringBuffer sb = new StringBuffer(type + ":");

        // Set rights
        // TODO: Do not hardcode the below, because it is very specific!
        //if (rights.length > 0) {

/*
            String debug = "";
            for (int j = 0; j < rights.length; j++) {
                debug = debug + rights[j] + ", ";
            }
            Window.alert("setListItem(): Number of rights: " + rights.length + ": " + debug);
*/

            sb.append(" (");
            boolean readExists = false;
            boolean writeExists = false;
            for (int j = 0; j < rights.length; j++) {
                if (rights[j].equals(READ_RIGHT)) {
                    readExists = true;
                    //Window.alert("setListItem(): read exists");
                }
                if (rights[j].equals(WRITE_RIGHT)) {
                    writeExists = true;
                    //Window.alert("setListItem(): write exists");
                }
            }
            if (readExists) {
                sb.append(READ_RIGHT);
            } else {
                sb.append("-");
            }
            sb.append(",");
            if (writeExists) {
                sb.append(WRITE_RIGHT);
            } else {
                sb.append("-");
            }

            sb.append(")");
        //}
        sb.append(" " + id);
        lb.setItemText(index, sb.toString());
    }

    /**
     *
     */
    public User[] getUsers() {
        Vector users = new Vector();
        for (int i = 0; i < lb.getItemCount(); i++) {
            String itemText = lb.getItemText(i);
            String[] rights = getRights(itemText);
            String id = getIdentityWithoutRights(i);
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
            String id = getIdentityWithoutRights(i);
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

    /**
     *
     */
    public boolean getUseInheritedPolicies() {
        return policyInheritanceCB.isChecked();
    }
}
