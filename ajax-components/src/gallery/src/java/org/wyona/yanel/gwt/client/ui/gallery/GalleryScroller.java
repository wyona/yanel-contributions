package org.wyona.yanel.gwt.client.ui.gallery;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This widget looks like:<br>
 * &lt; x of y &gt;, where &lt; represents left, [x of y] - middle, &gt; - right.
 * <p>
 * .yanel-GalleryScroller
 * .yanel-GalleryScroller-Left
 * .yanel-GalleryScroller-Left-Disabled
 * .yanel-GalleryScroller-Middle
 * .yanel-GalleryScroller-Right
 * .yanel-GalleryScroller-Right-Disabled
 * */
public class GalleryScroller extends Composite implements ChangeListener{
	public static final String STYLE = "yanel-GalleryScroller";
	
	public static final String STYLE_LEFT = "yanel-GalleryScroller-Left";
	public static final String STYLE_LEFT_DISABLED = "yanel-GalleryScroller-Left-Disabled";
	
	public static final String STYLE_MIDDLE = "yanel-GalleryScroller-Middle";
	
	public static final String STYLE_RIGHT = "yanel-GalleryScroller-Right";
	public static final String STYLE_RIGHT_DISABLED = "yanel-GalleryScroller-Right-Disabled";
	
	protected Panel panel = null;
	
	protected Widget left = null;
	protected Panel middle = null;
	protected Widget right = null;
	
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
	
	private boolean showMiddle = true;
	
	public GalleryScroller(Gallery gallery){
		this(gallery, true);
	}
	
	public GalleryScroller(Gallery gallery, boolean showMiddle){
		this.model = gallery;
		this.showMiddle = showMiddle;
		
		initGUI();
		initWidget(panel);
		
		markNavigationAtCurrentItem();
		
		gallery.addChangeListener(this);
	}
	
	public final void onChange(Widget sender) {
		initGUI();
		markNavigationAtCurrentItem();
	}
	
	/**
	 * Override to create/initialize different layout
	 * */
	protected void initGUI(){
	    if(panel == null){
	        panel = new HorizontalPanel();
	    }
	    
		panel.setStylePrimaryName(STYLE);
		
		if(middle == null){
		    middle = new SimplePanel();
		    middle.setStylePrimaryName(STYLE_MIDDLE);
		}
		middle.clear();
		final int visualIndex = model.getCurrentIndex()+1;
		middle.add(new Label(visualIndex + " / " + model.getSize()));
		
		if(left == null){
			left = getLeft();
			if (left instanceof SourcesClickEvents) {
                SourcesClickEvents src = (SourcesClickEvents) left;
                src.addClickListener(navigateLeft);
            }
		}
		left.setStylePrimaryName(STYLE_LEFT);
        left.setTitle("");
		
		if(right == null){
			right = getRight();
			if (right instanceof SourcesClickEvents) {
                SourcesClickEvents src = (SourcesClickEvents) right;
                src.addClickListener(navigateRight);
			}
		}
		right.setStylePrimaryName(STYLE_RIGHT);
		right.setTitle("");
		
		panel.clear();
		
		panel.add(left);
		((HorizontalPanel)panel).setCellVerticalAlignment(left, HorizontalPanel.ALIGN_MIDDLE);
		
		if(showMiddle){
		    panel.add(middle);
		    ((HorizontalPanel)panel).setCellVerticalAlignment(middle, HorizontalPanel.ALIGN_MIDDLE);
		}
		
		panel.add(right);
		((HorizontalPanel)panel).setCellVerticalAlignment(right, HorizontalPanel.ALIGN_MIDDLE);
	}
	
	/**
	 * Override to show the navigation state.
	 * */
	protected void markNavigationAtCurrentItem(){
		if(!model.canSelect(model.getCurrentIndex()-1)){
			left.setStylePrimaryName(STYLE_LEFT_DISABLED);
			if(neverEndingEnabled){
				left.setTitle("Go to the last");
			}else{
				left.setTitle("First item showing");
			}
//			left.setVisible(false);
		}
		
		if(!model.canSelect(model.getCurrentIndex()+1)){
			right.setStylePrimaryName(STYLE_RIGHT_DISABLED);
			if(neverEndingEnabled){
				right.setTitle("Go to the first");
			}else{
				right.setTitle("Last item showing");
			}
//			right.setVisible(false);
		}
	}
	
	protected Widget getLeft(){
	    if(left == null){
	        left = new Hyperlink("&lt;", true, "");
//	        left = new Button("&lt;");
	    }
		return left;
	}
	
	protected Widget getRight(){
	    if(right == null){
	        right = new Hyperlink("&gt;", true, "");
//	        right = new Button("&gt;");
	    }
	    return right;
	}
}
