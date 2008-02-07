package ch.globus.yanel.gwt.client.ui.gallery;

import org.wyona.yanel.gwt.client.ui.gallery.Gallery;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryScroller;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryViewer;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;


/**
 * .yanel-GalleryViewer-NavigationBar
 * .yanel-GalleryViewer-BlowButton
 * */
public class ImageGalleryViewer extends GalleryViewer {
    public static final String STYLE_NAVIBAR = "yanel-GalleryViewer-NavigationBar";
    public static final String STYLE_BLOW = "yanel-GalleryViewer-BlowButton";
    
	// Don't initialize the variables here, leave it to JVM
	private GalleryScroller gs;
	private Button blowUpButton;
	
	public ImageGalleryViewer(Gallery gallery) {
		super(gallery);
	}
	
	protected void initGUI() {
		super.initGUI();
		
		panel.add(itemPanel);
		panel.add(summary);
		
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
		
		if(blowUpButton == null){
		    blowUpButton = new Button("[-]");
		    blowUpButton.setStylePrimaryName(STYLE_BLOW);
		    
		}
		
		Grid p = new Grid(1, 3);
		
		p.setWidget(0, 1, gs);
		p.setWidget(0, 2, blowUpButton);
		
		p.getCellFormatter().setAlignment(0, 0, HorizontalPanel.ALIGN_CENTER, VerticalPanel.ALIGN_MIDDLE);
		p.getCellFormatter().setAlignment(0, 1, HorizontalPanel.ALIGN_CENTER, VerticalPanel.ALIGN_MIDDLE);
		p.getCellFormatter().setAlignment(0, 2, HorizontalPanel.ALIGN_RIGHT, VerticalPanel.ALIGN_MIDDLE);
		
//		p.setBorderWidth(1);
		
		// We need left and right to be of the same size in order to centralize the middle part
		// When the width of the button does not exceed 50px, then everything is fine
		p.getCellFormatter().setWidth(0, 0, blowUpButton.getOffsetWidth()+"50px");
		p.getCellFormatter().setWidth(0, 2, blowUpButton.getOffsetWidth()+"50px");

		p.setStylePrimaryName(STYLE_NAVIBAR);
		
		panel.add(p);
	}
	
	public Button getBlowUpButton(){
	    return blowUpButton;
	}
}
