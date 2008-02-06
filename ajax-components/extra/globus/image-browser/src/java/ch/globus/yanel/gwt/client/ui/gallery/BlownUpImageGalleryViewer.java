package ch.globus.yanel.gwt.client.ui.gallery;

import org.wyona.yanel.gwt.client.ui.gallery.Gallery;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryScroller;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryViewer;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * .yanel-GalleryViewer-BlownUp
 * .yanel-GalleryViewer-NavigationBar
 * .yanel-GalleryViewer-CloseButton
 * */
class BlownUpImageGalleryViewer extends GalleryViewer{
    public static final String STYLE_DEPENDENTCY_SUFFIX = "BlownUp";
    public static final String STYLE_NAVIBAR = "yanel-GalleryViewer-NavigationBar";
    public static final String STYLE_CLOSE = "yanel-GalleryViewer-CloseButton";
    
    // Don't initialize the variables here, leave it to JVM
    private GalleryScroller gs;
    private Button closeButton;
    
    public BlownUpImageGalleryViewer(Gallery gallery) {
        super(gallery);
    }
    
    protected void initGUI() {
        super.initGUI();
        
        if(gs == null){
            gs = new GalleryScroller(model, true);
        }
        if(closeButton == null){
            closeButton = new Button("X");
            closeButton.setStylePrimaryName(STYLE_CLOSE);
        }   
        
        HorizontalPanel dp = new HorizontalPanel();
        dp.add(gs);
        dp.add(closeButton);
        
        dp.setCellHorizontalAlignment(gs, HorizontalPanel.ALIGN_CENTER);
        dp.setCellHorizontalAlignment(closeButton, HorizontalPanel.ALIGN_RIGHT);
        
        dp.setStylePrimaryName(STYLE_NAVIBAR);
        
        panel.add(dp);
        
        panel.add(title);
        panel.add(subtitle);
        
        panel.add(itemPanel);
        panel.add(summary);
        
        panel.addStyleDependentName(STYLE_DEPENDENTCY_SUFFIX);
    }
    
    public Button getCloseButton(){
        return closeButton;
    }
    
}
