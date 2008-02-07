package org.wyona.yanel.gwt.client.ui.gallery;


public class Item {
	private String caption = null;
	private String summary = null;

	protected Item(){}
	
	public Item(String caption){
		setCaption(caption);
	}
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public static Item getNoItemsItem(){
		return new TextItem("NOTE", "No items to show");
	}
}
