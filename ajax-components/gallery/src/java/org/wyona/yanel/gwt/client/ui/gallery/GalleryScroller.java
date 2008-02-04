package org.wyona.yanel.gwt.client.ui.gallery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * .yanel-GalleryScroller
 * .yanel-GalleryScroller-Left
 * .yanel-GalleryScroller-Left-Disabled
 * .yanel-GalleryScroller-Item
 * .yanel-GalleryScroller-Item-Disabled
 * .yanel-GalleryScroller-Right
 * .yanel-GalleryScroller-Right-Disabled
 * */
public class GalleryScroller extends Composite implements ChangeListener{
	public static final String STYLE = "yanel-GalleryScroller";
	public static final String STYLE_LEFT = "yanel-GalleryScroller-Left";
	public static final String STYLE_LEFT_DISABLED = "yanel-GalleryScroller-Left-Disabled";
	public static final String STYLE_ITEM = "yanel-GalleryScroller-Item";
	public static final String STYLE_ITEM_DISABLED = "yanel-GalleryScroller-Item-Disabled";
	public static final String STYLE_RIGHT = "yanel-GalleryScroller-Right";
	public static final String STYLE_RIGHT_DISABLED = "yanel-GalleryScroller-Right-Disabled";
	
	private HorizontalPanel panel = new HorizontalPanel();
	
	private Hyperlink left = null;
	private List/*<FocusWidget>*/ enumeration = new ArrayList/*<FocusWidget>*/();
	private Hyperlink right = null;
	
	private Gallery model = null;
	
	private boolean neverEndingEnabled = true;
	
	private class NavigateLeft implements ClickListener{
		public void onClick(Widget sender) {
			if(model.getCurrentIndex() >= 1){
				model.selectItem(model.getCurrentIndex() - 1);
			}else if(model.getSize() > 0 && neverEndingEnabled){
				model.selectItem(model.getSize()-1);
			}
		}
	}
	
	private class NavigateRight implements ClickListener{
		public void onClick(Widget sender) {
			if(model.getCurrentIndex() < model.getSize()-1){
				model.selectItem(model.getCurrentIndex() + 1);
			}else if(model.getSize() > 0 && neverEndingEnabled){
				model.selectItem(0);
			}
		}
	}
	
	private ClickListener navigateLeft = new NavigateLeft();
	private ClickListener navigateRight = new NavigateRight();
	
	private boolean showEnumeration = true;
	
	public GalleryScroller(Gallery gallery){
		this(gallery, true);
	}
	
	public GalleryScroller(Gallery gallery, boolean showEnumeration){
		this.model = gallery;
		this.showEnumeration = showEnumeration;
		
		buildGUI();
		initWidget(panel);
		
		markNavigationAtCurrentItem();
		
		gallery.addChangeListener(this);
	}
	
	public final void onChange(Widget sender) {
		buildGUI();
		markNavigationAtCurrentItem();
	}
	
	private void buildGUI(){
		panel.setStyleName(STYLE);
		
		for (int i = 0; i < model.getSize(); i++) {
			final int visualIndex = i+1;
			if(enumeration.size() < visualIndex){
				FocusWidget w = new Button(new Integer(i+1).toString(), new ClickListener(){
					public void onClick(Widget sender) {
						model.selectItem(visualIndex - 1);
					}
				});
				enumeration.add(w);
			}else{
				// widget already exists
			}
			
			FocusWidget w = (FocusWidget)enumeration.get(i);
			w.setStyleName(STYLE_ITEM);
			w.setEnabled(true);
			
		}
		
		if(left == null){
			left = new Hyperlink(getLeftHTML(), true, "");
			left.addClickListener(navigateLeft);
		}
		
		if(right == null){
			right = new Hyperlink(getRightHTML(), true, "");
			right.addClickListener(navigateRight);
		}
		
		left.setStyleName(STYLE_LEFT);
		left.setTitle("");
		right.setStyleName(STYLE_RIGHT);
		right.setTitle("");
		
		panel.clear();
		panel.add(left);
		if(showEnumeration){
			for (Iterator i = enumeration.iterator(); i.hasNext();) {
				Widget w = (Widget) i.next();
				panel.add(w);
			}
		}
		panel.add(right);
	}
	
	private void markNavigationAtCurrentItem(){
		if(!model.canSelect(model.getCurrentIndex()-1)){
			left.setStyleName(STYLE_LEFT_DISABLED);
			if(neverEndingEnabled){
				left.setTitle("Go to the last");
			}else{
				left.setTitle("First item showing");
			}
//			left.setVisible(false);
		}
		
		if(!model.canSelect(model.getCurrentIndex()+1)){
			right.setStyleName(STYLE_RIGHT_DISABLED);
			if(neverEndingEnabled){
				right.setTitle("Go to the first");
			}else{
				right.setTitle("Last item showing");
			}
//			right.setVisible(false);
		}
		
		if(showEnumeration){
			if(model.getCurrentIndex() >= 0 && model.getCurrentIndex() < enumeration.size()){
				FocusWidget item = (FocusWidget)enumeration.get(model.getCurrentIndex());
				item.setStyleName(STYLE_ITEM_DISABLED);
				item.setEnabled(false);
			}
		}
	}
	
	protected String getLeftHTML(){
		return "&lt;";
	}
	
	protected String getRightHTML(){
		return "&gt;";
	}
}
