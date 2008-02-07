package ch.globus.yanel.gwt.client;


import org.wyona.yanel.gwt.client.ConfigurableComponentsAware;
import org.wyona.yanel.gwt.client.ui.gallery.AsynchronousGalleryBuilder;

import ch.globus.yanel.gwt.client.ui.gallery.BlownUpImageGalleryViewerDialog;
import ch.globus.yanel.gwt.client.ui.gallery.ImageGalleryViewer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Yanel.ImageBrowser.configurations = {id:&lt;root panel id>, gallery_provider_url : &lt;URL of the data for the component(e.g. atom data)>}
 * */
public class ImageBrowser extends ConfigurableComponentsAware implements EntryPoint{
	
    public static interface ParameterNames{
        public static final String IMAGE_LISTER_URL = "gallery_provider_url";
        public static final String BLOWN_IMAGE_LISTER_URL = "blown_gallery_provider_url";
    }
    
    public void onModuleLoad() {
    	initializeInstances();
    }
    
    protected void initializeInstances() {
    	final RootPanel [] p = getRootPanels();
    	for (int i = 0; i < p.length; i++) {
    		if(null == p[i]){
    			continue;
    		}
    		
    		String url = getConfiguration(ParameterNames.IMAGE_LISTER_URL, i);
    		final AsynchronousGalleryBuilder agb = new AsynchronousGalleryBuilder(url);
    		
    		try {
                final Request request = agb.execute();
                
                // When request started
                final int panelIndex = i;
                Timer t = new Timer(){
                    public void run() {
                        if(!request.isPending()){
                            final ImageGalleryViewer gv = new ImageGalleryViewer(agb.getGallery());
                            p[panelIndex].add(gv);
                            
                            gv.getBlowUpButton().addClickListener(new ClickListener(){
                                BlownUpImageGalleryViewerDialog dialog = null;
                                public void onClick(Widget sender) {
                                   if(dialog == null){
                                       dialog = new BlownUpImageGalleryViewerDialog(new AsynchronousGalleryBuilder(getConfiguration(ParameterNames.BLOWN_IMAGE_LISTER_URL, panelIndex)), gv.getGallery().getCurrentIndex());
                                       dialog.show();
                                   }else{
                                       dialog.getGallery().selectItem(gv.getGallery().getCurrentIndex());
                                       dialog.center();
                                   }
                                } 
                            });
                            
                            // Cancel the timer when the request has ended
                            this.cancel();
                        }else{
                            // Show some UI while waiting
                            
                            // Wait every 10 milliseconds to check if the request has finished
                            scheduleRepeating(10);
                        }
                    }
                };
                t.schedule(1);
            } catch (RequestException e) {
                if(!GWT.isScript()){
                    e.printStackTrace();
                }
            }
		}
    }
}
