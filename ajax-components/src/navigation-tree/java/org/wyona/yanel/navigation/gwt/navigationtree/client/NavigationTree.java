/* * GWT-Ext Widget Library * Copyright(c) 2007-2008, GWT-Ext. * licensing@gwt-ext.com * 
 * * http://www.gwt-ext.com/license */

package org.wyona.yanel.navigation.gwt.navigationtree.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.data.NodeTraversalCallback;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Resizable;
import com.gwtext.client.widgets.ResizableConfig;
import com.gwtext.client.widgets.event.ResizableListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.tree.AsyncTreeNode;
import com.gwtext.client.widgets.tree.TreeEditor;
import com.gwtext.client.widgets.tree.TreeLoader;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.XMLTreeLoader;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

public class NavigationTree implements EntryPoint {
    private Menu menu;
    private TreeNode ctxNode;
    private TreeEditor treeEditor;

    public void onModuleLoad() {
        Panel panel = new Panel();
        panel.setBorder(false);
        panel.setPaddings(15);
        final TreePanel treePanel = new NavigationTreePanel();
        TextField field = new TextField();
        field.setSelectOnFocus(true);
        treeEditor = new TreeEditor(treePanel, field);
        treePanel.setContainerScroll(true);
        treePanel.setAutoScroll(true);
        treePanel.setEnableDD(true);
        treePanel.setWidth(190);
        treePanel.setHeight(600);
        TreeNode root = new TreeNode();
        treePanel.addListener(new TreePanelListenerAdapter() {
            public void onContextMenu(TreeNode node, EventObject e) {
                int[] xy = e.getXY();
                showContextMenu(node, e);
            }
        });
        panel.add(treePanel);
        
        ResizableConfig config = new ResizableConfig();  
        config.setHandles(Resizable.SOUTH_EAST);  
        
        final Resizable resizable = new Resizable(treePanel, config);  
        resizable.addListener(new ResizableListenerAdapter() {  
            public void onResize(Resizable self, int width, int height) {  
                treePanel.setWidth(width);  
                treePanel.setHeight(height);  
            }  
        }); 
        
        RootPanel.get("navigation-tree-hook").add(panel);
    }

    private void showContextMenu(final TreeNode node, EventObject e) {
        if (menu == null) {
            menu = new Menu();
            
            Item editItem = new Item("Edit", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    treeEditor.startEdit(ctxNode);
                }
            });
            editItem.setId("edit-item");
            menu.addItem(editItem);
            
            Item disableItem = new Item("Disable", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    ctxNode.disable();
                    ctxNode.cascade(new NodeTraversalCallback() {
                        public boolean execute(Node node) {
                            ((TreeNode) node).disable();
                            return true;
                        }
                    });
                }
            });
            disableItem.setId("disable-item");
            menu.addItem(disableItem);
            
            Item enableItem = new Item("Enable", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    ctxNode.enable();
                    ctxNode.cascade(new NodeTraversalCallback() {
                        public boolean execute(Node node) {
                            ((TreeNode) node).enable();
                            return true;
                        }
                    });
                }
            });
            enableItem.setId("enable-item");
            menu.addItem(enableItem);
            
            Item cloneItem = new Item("Clone", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    TreeNode clone = ctxNode.cloneNode();
                    clone.setText("Copy of " + clone.getText());
                    ctxNode.getParentNode().appendChild(clone);
                    treeEditor.startEdit(clone);
                }
            });
            cloneItem.setId("clone-item");
            menu.addItem(cloneItem);
            
            Item newFolderItem = new Item("New Folder", new BaseItemListenerAdapter() {
                public void onClick(BaseItem item, EventObject e) {
                    TreeNode newFolder = new TreeNode("New Folder");
                    ctxNode.getParentNode().appendChild(newFolder);
                    treeEditor.startEdit(newFolder);
                }
            });
            newFolderItem.setId("newfolder-item");
            menu.addItem(newFolderItem);
            
        }
        if (ctxNode != null) {
            ctxNode = null;
        }
        ctxNode = node;
        if (ctxNode.isDisabled()) {
            menu.getItem("disable-item").disable();
            menu.getItem("enable-item").enable();
        } else {
            menu.getItem("disable-item").enable();
            menu.getItem("enable-item").disable();
        }
        menu.showAt(e.getXY());
    }

    class NavigationTreePanel extends TreePanel {
        public NavigationTreePanel() {

            final TreeLoader loader = new TreeLoader();
            loader.setDataUrl("?yanel.resource.viewid=json-node");
            loader.setMethod(com.gwtext.client.core.Connection.GET);

            AsyncTreeNode root = new AsyncTreeNode("Navigation", loader);
            root.setId("/");
            setRootNode(root);
        }
    }
}