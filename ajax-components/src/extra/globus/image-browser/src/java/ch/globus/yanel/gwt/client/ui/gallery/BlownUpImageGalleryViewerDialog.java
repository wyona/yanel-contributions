package ch.globus.yanel.gwt.client.ui.gallery;

import org.wyona.yanel.gwt.client.ui.GlassPanel;
import org.wyona.yanel.gwt.client.ui.gallery.AsynchronousGalleryBuilder;
import org.wyona.yanel.gwt.client.ui.gallery.Gallery;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BlownUpImageGalleryViewerDialog extends DialogBox {
    private BlownUpImageGalleryViewer galleryViewer;
    
    private final GlassPanel glassPanel = new GlassPanel();
    
    public BlownUpImageGalleryViewerDialog(Gallery gallery) {
        super(false, true);
        galleryViewer = new BlownUpImageGalleryViewer(gallery);
        setWidget(galleryViewer);
        
        galleryViewer.getCloseButton().addClickListener(new ClickListener(){
            public void onClick(Widget sender) {
                hide();
            }
        });
    }
    
    public BlownUpImageGalleryViewerDialog(final AsynchronousGalleryBuilder agb, final int selectedIndex) {
        super(false, true);
        
        try {
            final Request request = agb.execute();
            
            // When request started
            Timer t = new Timer(){
                
                public void run() {
                    if(!request.isPending()){
                        galleryViewer = new BlownUpImageGalleryViewer(agb.getGallery());
                        galleryViewer.getGallery().selectItem(selectedIndex);
                        setWidget(galleryViewer);
                        
                        galleryViewer.getCloseButton().addClickListener(new ClickListener(){
                            public void onClick(Widget sender) {
                                hide();
                            }
                        });
                        
                        // Cancel the timer when the request has ended
                        this.cancel();
                    }else{
                        // Show some UI while waiting
                        setWidget(new Label("Loading..."));
                        
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
    
    public BlownUpImageGalleryViewerDialog(AsynchronousGalleryBuilder agb) {
        this(agb, 0);
    }
    
    public void show() {
        glassPanel.show();
        super.show();
    }
    
    public void hide() {
        glassPanel.hide();
        super.hide();
    }
    
    public Gallery getGallery(){
        Gallery result = null;
        if(galleryViewer != null){
            result = galleryViewer.getGallery();
        }
        return result;
    }
}
