package org.wyona.yanel.navigation.gwt.lookuptree.client;

import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.Renderer;


class LookupGrid extends GridPanel {
    private String requestParameterType; 
    private String currentPath = "/"; 
    
    public LookupGrid(String requestParameterType) {
        this.requestParameterType = requestParameterType;
        
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
         
         ColumnConfig colConfCls = new ColumnConfig("", "cls", 20, true);
         colConfCls.setRenderer(renderer);
         ColumnModel columnModel = new ColumnModel(new ColumnConfig[]{  
                 colConfCls,
                 new ColumnConfig("Text", "text", 220, true),
         });  
         
         final RecordDef recordDef = new RecordDef(new FieldDef[]{  
                 new StringFieldDef("id", "id"),  
                 new StringFieldDef("text", "text"),  
                 new StringFieldDef("cls", "cls"),  
         });  
         JsonReader reader = new JsonReader(recordDef);  
         reader.setRoot("data");  
         reader.setTotalProperty("totalCount");  
         
         Store store = new Store(new HttpProxy("?"), reader, false);
         store.load(new UrlParam[]{
                 new UrlParam("yanel.resource.viewid", "json-node-grid"),
                 new UrlParam("type",getType()),
                 new UrlParam("node",currentPath)});
         this.setStore(store);
         
         this.setColumnModel(columnModel);  
         this.setFrame(false);  
         this.stripeRows(true);  
         this.setIconCls("grid-icon");  
         
         this.setLoadMask(true);
         GridView view = new GridView();  
         this.setView(view);  
    }
    
    public void updateData() {
        this.getStore().reload(new UrlParam[]{
                new UrlParam("yanel.resource.viewid", "json-node-grid"),
                new UrlParam("type",getType()),
                new UrlParam("node",currentPath)});
        this.getView().refresh();
    }

    private String getType() {
        if (requestParameterType != null && !requestParameterType.equals("")) {
            return requestParameterType;
        }
        return "";
    }
    
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }
}