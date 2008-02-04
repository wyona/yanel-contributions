package org.wyona.yanel.gwt.client.ui.gallery;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * .yanel-GalleryViewer
 * .yanel-GalleryViewer-Title
 * .yanel-GalleryViewer-Item
 * .yanel-GalleryViewer-Caption
 * */
public class GalleryViewer extends Composite implements ChangeListener{
	public static final String STYLE = "yanel-GalleryViewer";
	public static final String STYLE_TITLE = "yanel-GalleryViewer-Title";
	public static final String STYLE_ITEM = "yanel-GalleryViewer-Item";
	public static final String STYLE_CAPTION = "yanel-GalleryViewer-Caption";
	
	protected VerticalPanel panel = new VerticalPanel();
	
	protected Label title = new Label();
	protected SimplePanel itemPanel = new SimplePanel();
	protected Label caption = new Label();
	
	protected Gallery model = null;
	
	protected GalleryViewer(){}
	
	public GalleryViewer(Gallery gallery) {
		this.model = gallery;
		
		buildGUI();
		showCurrentItem();
		initWidget(panel);
		
		gallery.addChangeListener(this);
	}
	
	/**
	 * Override this method to layout the components as needed
	 * */
	protected void buildGUI(){
		panel.setStyleName(STYLE);
		title.setStyleName(STYLE_TITLE);
		itemPanel.setStyleName(STYLE_ITEM);
		caption.setStyleName(STYLE_CAPTION);
	}
	
	public final void onChange(Widget sender) {
		showCurrentItem();
	}
	
	private void showCurrentItem(){
		if(model.getCurrentItem() != null){
			caption.setText(model.getCurrentItem().getCaption());
			title.setText(model.getTitle());
			itemPanel.setWidget(getWidgetForItem(model.getCurrentItem()));
		}else{
			// Nothing to show
		}
	}
	
	/**
	 * Override this method to generate the visual representation of the item
	 * */
	protected Widget getWidgetForItem(Item item){
		return new Label("WIDGET:"+item.getCaption());
	}
}
