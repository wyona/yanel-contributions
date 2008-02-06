package ch.globus.yanel.gwt.client.ui.gallery;

import org.wyona.yanel.gwt.client.ui.gallery.Gallery;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryScroller;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryViewer;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
		    gs = new GalleryScroller(model, true);
		}
		
		if(blowUpButton == null){
		    blowUpButton = new Button("[-]");
		    blowUpButton.setStylePrimaryName(STYLE_BLOW);
		    
		}
		Grid g = new Grid(1, 3);
		
		g.setWidget(0, 0, new HTML("<span/>"));
		g.setWidget(0, 1, gs);
		g.setWidget(0, 2, blowUpButton);
		
		g.setStylePrimaryName(STYLE_NAVIBAR);
		
		
		panel.add(g);
	}
	
	public Button getBlowUpButton(){
	    return blowUpButton;
	}
}
