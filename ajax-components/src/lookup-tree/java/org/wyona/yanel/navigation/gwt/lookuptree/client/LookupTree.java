/* * GWT-Ext Widget Library * Copyright(c) 2007-2008, GWT-Ext. * licensing@gwt-ext.com * 
 * * http://www.gwt-ext.com/license */

package org.wyona.yanel.navigation.gwt.lookuptree.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.ExtElement;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Resizable;
import com.gwtext.client.widgets.ResizableConfig;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ResizableListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtext.client.widgets.tree.AsyncTreeNode;
import com.gwtext.client.widgets.tree.TreeLoader;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

public class LookupTree implements EntryPoint {

    private String lookupPanelBorder = "false";
    private String lookupPanelPadding = "15";
    private String width = "190";
    private String height = "400";
    private String lookupRootNodeLabel = "/";
    private String lookupHook = "lookupHook";
    private String requestParameterType = "";
    private String actionUrl = "";
    private String submitLabel = "submit";
    private String currentPath = "/";
    final LookupGrid grid = new LookupGrid();

    public void onModuleLoad() {
        // Get config from host/html page via json var 'lookupTreeConfig'
        try {
            Dictionary dict = Dictionary.getDictionary("lookupTreeConfig");
            lookupPanelBorder = dict.get("lookup-panel-border");
            lookupPanelPadding = dict.get("lookup-panel-padding");
            width = dict.get("lookup-treepanel-width");
            height = dict.get("lookup-treepanel-height");
            lookupRootNodeLabel = dict.get("lookup-root-node-label");
            lookupHook = dict.get("lookup-hook");
            requestParameterType = dict.get("lookup-request-paramter-type");
            actionUrl = dict.get("lookup-upload-action-url");
            submitLabel = dict.get("lookup-upload-submit-button-label");
        } catch (java.util.MissingResourceException e) {
            // just use default values
        }
        
        final Panel panel = new Panel();
        panel.setBorder(new Boolean(lookupPanelBorder).booleanValue());
        panel.setPaddings(Integer.parseInt(lookupPanelPadding));
        
        grid.addGridRowListener(new GridRowListenerAdapter(){
            public void onRowClick(GridPanel grid,
                    int rowIndex,
                    EventObject e){
                onNodeClick(grid.getStore().getAt(rowIndex).getAsString("id"));
            }

        });

        final FormPanel form = new LookupUploadPanel();

        
        final TreePanel treePanel = new LookupTreePanel(lookupRootNodeLabel, requestParameterType);
        treePanel.setEnableDD(false);
        treePanel.setContainerScroll(true);
        treePanel.setAutoScroll(true);
        treePanel.setWidth(Integer.parseInt(width));
        treePanel.setHeight(Integer.parseInt(height));
        treePanel.addListener(new TreePanelListenerAdapter() {
            public void onClick(TreeNode node, EventObject e) {
                currentPath = node.getId();
                grid.updateData();
//                form.getElement().addFormHandler(new )
            }
        });
        

        
        final VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(form);
        panel.add(verticalPanel);
        
        
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(treePanel);
        
        horizontalPanel.add(grid);
        
        verticalPanel.add(horizontalPanel);
        panel.add(verticalPanel);
        RootPanel.get(lookupHook).add(panel);
    }

    public native void onNodeClick(final String path)/*-{
        $wnd.callback(path); 
    }-*/;
    
    class LookupTreePanel extends TreePanel {
        public LookupTreePanel(String lookupRootNodeLabel, String requestParameterType) {
            final TreeLoader loader = new TreeLoader();
            String dataUrl = "?yanel.resource.viewid=json-node&show-collections-only=true";
            if (requestParameterType != null && !requestParameterType.equals("")) {
                dataUrl += "&type="+requestParameterType;
            }
            loader.setDataUrl(dataUrl);
            loader.setMethod(com.gwtext.client.core.Connection.GET);
            AsyncTreeNode root = new AsyncTreeNode(lookupRootNodeLabel, loader);
            root.setId("/");
            setRootNode(root);
            addRiszeable();
        }
        
        private void addRiszeable() {
            
            ResizableConfig config = new ResizableConfig();  
            config.setHandles(Resizable.SOUTH_EAST);  
            
            final Resizable resizable = new Resizable(this, config);  
            final TreePanel treePanel = this;
            resizable.addListener(new ResizableListenerAdapter() {  
                public void onResize(Resizable self, int width, int height) {  
                    treePanel.setWidth(width);  
                    treePanel.setHeight(height);  
                }  
            }); 
        }
    }
    
    class LookupUploadPanel extends FormPanel {
        Hidden savePath = new Hidden();
        public LookupUploadPanel(){
            final FormPanel form = this;
            form.setEncoding(FormPanel.ENCODING_MULTIPART);
            form.setMethod(FormPanel.METHOD_POST);
            
            final HorizontalPanel formHolder = new HorizontalPanel();
            
            //FileUpload widget.
            FileUpload upload = new FileUpload();
            upload.setName("rp.data");
            
            Hidden resourceType = new Hidden();
            resourceType.setName("resource-type");
            resourceType.setValue("http://www.wyona.org/yanel/resource/1.0::file");
            
            
            savePath.setName("lookin");
            savePath.setValue(currentPath);
            
            Hidden saveParameter = new Hidden();
            saveParameter.setName("save");
            saveParameter.setValue("save");
            
            formHolder.add(resourceType);
            formHolder.add(savePath);
            formHolder.add(saveParameter);
            formHolder.add(upload);
            
            form.add(formHolder);
            form.setAction(actionUrl);
            
            // Add a 'submit' button.
            formHolder.add(new Button(submitLabel, new ClickListener() {
              public void onClick(Widget sender) {
                form.submit();
              }
            }));

            // Add an event handler to the form.
            form.addFormHandler(new FormHandler() {
              public void onSubmit(FormSubmitEvent event) {
                // This event is fired just before the form is submitted. We can take
                // this opportunity to perform validation.
                  savePath.setValue(currentPath);
              }

              public void onSubmitComplete(FormSubmitCompleteEvent event) {
                // When the form submission is successfully completed, this event is
                // fired. Assuming the service returned a response of type text/html,
                // we can get the result text here (see the FormPanel documentation for
                // further explanation).
                  grid.updateData();
              }
            });            
        }
    }
    
    class LookupGrid extends GridPanel {
        private String dataUrl; 
        public LookupGrid() {
            
            Renderer renderer = new Renderer() {


                public String render(Object value, CellMetadata cellMetadata, Record record,
                        int rowIndex, int colNum, Store store) {
                    store.getAt(rowIndex).getAsString("cls");
                    if (store.getAt(rowIndex).getAsString("cls").equals("folder")) {
                        return "<div class=\"x-tree-node-collapsed\"><div class=\"x-tree-node-icon\"></div></div>";
                    }
                    return "<div class=\"x-tree-node-leaf\"><div class=\"x-tree-node-icon\"></div></div>";
                }
             };
             
            //setup column model  
             ColumnConfig colConfCls = new ColumnConfig("", "cls", 20, true);
             colConfCls.setRenderer(renderer);
             
            ColumnModel columnModel = new ColumnModel(new ColumnConfig[]{  
                    colConfCls,
                    new ColumnConfig("Text", "text", 220, true),
            });  
            
            this.setStore(getGridStore());
            
            this.setColumnModel(columnModel);  
            this.setFrame(false);  
            this.setWidth(375);  
            this.setHeight(350);  
            this.stripeRows(true);  
            this.setIconCls("grid-icon");  
            
            GridView view = new GridView();  
            view.setEmptyText("Press the Load button to load the Local Json data.");  
            this.setView(view);  
            
            addRiszeable();
        }
        
        private void addRiszeable() {
            
            ResizableConfig config = new ResizableConfig();  
            config.setHandles(Resizable.SOUTH_EAST);  
            
            final Resizable resizable = new Resizable(this, config);  
            final GridPanel gridPanel = this;
            resizable.addListener(new ResizableListenerAdapter() {  
                public void onResize(Resizable self, int width, int height) {  
                    gridPanel.setWidth(width);  
                    gridPanel.setHeight(height);  
                }  
            }); 
        }      
        
//        public void updateData(String path) {
        public void updateData() {
            String type = "";
            if (requestParameterType != null && !requestParameterType.equals("")) {
                type = requestParameterType;
            }
            
            this.getStore().load(new UrlParam[]{
                    new UrlParam("yanel.resource.viewid", "json-node-grid"),
                    new UrlParam("type",type),
                    new UrlParam("node",currentPath)});
            this.getStore().reload();
            this.getView().refresh();
        }
        
//        private Store getStore(String path) {
        private Store getGridStore() {
//          String type = "";
//          if (requestParameterType != null && !requestParameterType.equals("")) {
//              type = requestParameterType;
//          }            
            dataUrl = "?yanel.resource.viewid=json-node-grid";
            if (requestParameterType != null && !requestParameterType.equals("")) {
                dataUrl += "&type="+requestParameterType;
            }
            dataUrl += "&node=" + currentPath;
//            return new HttpProxy(dataUrl);  
            final RecordDef recordDef = new RecordDef(new FieldDef[]{  
                    new StringFieldDef("id", "id"),  
                    new StringFieldDef("text", "text"),  
                    new StringFieldDef("cls", "cls"),  
            });  
            JsonReader reader = new JsonReader(recordDef);  
            reader.setRoot("data");  
            reader.setTotalProperty("totalCount");  
            Store store = new Store(new HttpProxy("?"), reader, false);

            return store;
        }
    }
}