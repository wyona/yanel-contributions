package org.wyona.yanel.gwt.client.ui.gallery;


public class TextItem extends Item {
	private String text = null;
	public TextItem(String caption, String text){
		super(caption);
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
}
