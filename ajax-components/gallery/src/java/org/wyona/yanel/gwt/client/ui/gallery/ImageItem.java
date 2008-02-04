package org.wyona.yanel.gwt.client.ui.gallery;


public class ImageItem extends Item {
	private String src = null;

	public ImageItem(String caption, String src) {
		super(caption);
		setSrc(src);
	}
	
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}
	
}
