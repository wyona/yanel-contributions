package ch.informatica08.yanel.gwt.client.ui.gallery;

import org.wyona.yanel.gwt.client.ui.gallery.Gallery;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryScroller;
import org.wyona.yanel.gwt.client.ui.gallery.GalleryViewer;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * .yanel-GalleryViewer-TitleBar
 * */
public class ImageGalleryViewer extends GalleryViewer {
	public static final String STYLE_TITLEBAR = "yanel-GalleryViewer-TitleBar";
	
	// Don't initialize the variables here, leave it to JVM
	private GalleryScroller gs;
	
	public ImageGalleryViewer(Gallery gallery) {
		super(gallery);
	}
	
	protected void initGUI() {
		super.initGUI();
		
		if(gs == null){
            gs = new GalleryScroller(model, false);
        }
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(title);
		hp.add(gs);
		
		hp.setCellHorizontalAlignment(title, HorizontalPanel.ALIGN_LEFT);
		hp.setCellHorizontalAlignment(gs, HorizontalPanel.ALIGN_RIGHT);
		
		hp.setStylePrimaryName(STYLE_TITLEBAR);
		
		panel.add(hp);
		
		panel.add(caption);
		
		panel.add(itemPanel);
	}
}
