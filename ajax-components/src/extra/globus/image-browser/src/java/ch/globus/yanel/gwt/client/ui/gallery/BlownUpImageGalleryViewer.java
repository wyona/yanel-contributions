package ch.globus.yanel.gwt.client.ui.gallery;

import org.wyona.yanel.gwt.client.ui.gallery.Gallery;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryScroller;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryViewer;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
            gs = new GalleryScroller(model, true){
                protected Widget getLeft() {
                    return new Button("");
                }
                
                protected Widget getRight() {
                    return new Button("");
                }
            };
        }
        if(closeButton == null){
            closeButton = new Button("X");
            closeButton.setStylePrimaryName(STYLE_CLOSE);
        }   
        
        Grid p = new Grid(1, 3);
        
        p.setWidget(0, 1, gs);
        p.setWidget(0, 2, closeButton);
        
        p.getCellFormatter().setAlignment(0, 0, HorizontalPanel.ALIGN_CENTER, VerticalPanel.ALIGN_MIDDLE);
        p.getCellFormatter().setAlignment(0, 1, HorizontalPanel.ALIGN_CENTER, VerticalPanel.ALIGN_MIDDLE);
        p.getCellFormatter().setAlignment(0, 2, HorizontalPanel.ALIGN_RIGHT, VerticalPanel.ALIGN_MIDDLE);
        
//      p.setBorderWidth(1);
        
        // We need left and right to be of the same size in order to centralize the middle part
        // When the width of the button does not exceed 50px, then everything is fine
        p.getCellFormatter().setWidth(0, 0, closeButton.getOffsetWidth()+"50px");
        p.getCellFormatter().setWidth(0, 2, closeButton.getOffsetWidth()+"50px");
        
        p.setStylePrimaryName(STYLE_NAVIBAR);
        
        panel.add(p);
        
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
