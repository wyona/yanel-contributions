package org.wyona.yanel.navigation.gwt.lookuptree.client;

import com.gwtext.client.widgets.tree.AsyncTreeNode;
import com.gwtext.client.widgets.tree.TreeLoader;
import com.gwtext.client.widgets.tree.TreePanel;


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
    }
}