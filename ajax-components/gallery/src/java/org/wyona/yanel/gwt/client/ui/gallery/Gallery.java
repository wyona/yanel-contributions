package org.wyona.yanel.gwt.client.ui.gallery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.SourcesChangeEvents;

public abstract class Gallery implements SourcesChangeEvents{
	protected String title = null;
	protected int size = 0;
	
	protected Map/*<Integer, Item>*/ itemCache = new HashMap/*<Integer, Item>*/();
	
	private Integer currentIndex = new Integer(-1);
	
	private Set/*<ChangeListener>*/ listeners = new HashSet/*<ChangeListener>*/();
	
	public Gallery() {
		init();
	}
	
	protected abstract void init();
	protected abstract void retrieveItem(int index);
	
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		listeners.remove(listener);
	}
	
	public Item getCurrentItem(){
		return (Item)itemCache.get(currentIndex);
	}
	
	public void selectItem(int index){
		if(canSelect(index)){
			Integer newIndex = new Integer(index);
			if(itemCache.get(newIndex) == null){
				// Get the item from server
				retrieveItem(index);
			}
			currentIndex = newIndex;
			fireOnChange();
		}else{
			// Nothing happens
		}
	}
	
	public boolean canSelect(int index){
		boolean can = true;
		if(index < 0 || index >= getSize() || index == currentIndex.intValue()){
			can = false;
		}
			
		return can;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getCurrentIndex() {
		return currentIndex.intValue();
	}
	
	public String getTitle() {
		return title;
	}
	
	protected final void fireOnChange(){
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			ChangeListener l = (ChangeListener) i.next();
			l.onChange(null);
		}
	}
}
