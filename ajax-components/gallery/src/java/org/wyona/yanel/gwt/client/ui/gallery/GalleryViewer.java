package org.wyona.yanel.gwt.client.ui.gallery;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * .yanel-GalleryViewer
 * .yanel-GalleryViewer-Title
 * .yanel-GalleryViewer-Subtitle
 * .yanel-GalleryViewer-Item
 * .yanel-GalleryViewer-Caption
 * .yanel-GalleryViewer-Summary
 * */
public class GalleryViewer extends Composite implements ChangeListener{
	public static final String STYLE = "yanel-GalleryViewer";
	public static final String STYLE_TITLE = "yanel-GalleryViewer-Title";
	public static final String STYLE_SUBTITLE = "yanel-GalleryViewer-Subtitle";
	public static final String STYLE_ITEM = "yanel-GalleryViewer-Item";
	public static final String STYLE_CAPTION = "yanel-GalleryViewer-Caption";
	public static final String STYLE_SUMMARY = "yanel-GalleryViewer-Summary";
	
	protected Panel panel = null;
	
	protected HTML title = null;
	protected HTML subtitle = null;
	protected HTML caption = null;
	protected SimplePanel itemPanel = null;
	protected HTML summary = null;
	
	
	protected Gallery model = null;
	
	protected GalleryViewer(){}
	
	public GalleryViewer(Gallery gallery) {
		this.model = gallery;
		
		initGUI();
		initWidget(panel);
		
		showCurrentItem();
		
		gallery.addChangeListener(this);
	}
	
	public final void onChange(Widget sender) {
        initGUI();
        showCurrentItem();
    }
	
	/**
	 * Override this method to layout the components as needed
	 * */
	protected void initGUI(){
	    if(panel == null){
	        panel = new VerticalPanel();
	    }
	    panel.clear();
	    
		panel.setStylePrimaryName(STYLE);
		
		if(title == null){
		    title = new HTML();
		}
		title.setStylePrimaryName(STYLE_TITLE);
		
		if(subtitle == null){
		    subtitle = new HTML();
        }
		subtitle.setStylePrimaryName(STYLE_SUBTITLE);
		
		if(itemPanel == null){
		    itemPanel = new SimplePanel();
		}
		itemPanel.setStylePrimaryName(STYLE_ITEM);
		
		if(caption == null){
		    caption = new HTML();
		}
		caption.setStylePrimaryName(STYLE_CAPTION);
		
		if(summary == null){
		    summary = new HTML();
        }
		summary.setStylePrimaryName(STYLE_SUMMARY);
	}
	
	protected void showCurrentItem(){
		if(model.getCurrentItem() != null){
		    if(model.getCurrentItem().getCaption() != null){
		        caption.setHTML(model.getCurrentItem().getCaption());
		    }
			if(model.getTitle() != null){
			    title.setHTML(model.getTitle());
			}
			
			if(model.getSubtitle() != null){
                subtitle.setHTML(model.getSubtitle());
            }
			
			if(model.getSummary() != null){
                summary.setHTML(model.getSummary());
            }
			itemPanel.setWidget(getWidgetForItem(model.getCurrentItem()));
		}else{
			// Nothing to show
		}
	}
	
	/**
	 * Override this method to generate the visual representation of the item
	 * */
	protected Widget getWidgetForItem(Item item) {
        if (item instanceof ImageItem) {
            ImageItem imageItem = (ImageItem) item;
            return new Image(imageItem.getSrc());
        }else if (item instanceof TextItem) {
            TextItem textItem = (TextItem)item;
            return new Label(textItem.getText());
        }else if (item instanceof HTMLItem) {
            HTMLItem htmlItem = (HTMLItem)item;
            return new Label(htmlItem.getHTML());
        }else{
            return new Label("WIDGET:"+item.getCaption());    
        }
    }
	
	public Gallery getGallery(){
	    return model;
	}
}
