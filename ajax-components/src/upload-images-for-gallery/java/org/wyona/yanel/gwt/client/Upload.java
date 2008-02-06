package org.wyona.yanel.gwt.client;

import java.util.Iterator;

import org.wyona.yanel.gwt.client.ui.upload.UploadPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The root panel for this component must be a form
 * */
public class Upload extends ConfigurableComponentsAware implements EntryPoint{
	public static interface ParameterNames{
        public static final String IMAGE_LISTER_URL = "gallery_provider_url";
    }
	
	public void onModuleLoad() {
		initializeInstances();
	}
	
	protected void initializeInstances() {
		for (Iterator i = getRootPanelIterator(); i.hasNext();) {
			RootPanel rp = (RootPanel) i.next();
			rp.add(new UploadPanel(3));
		}
	}
}
