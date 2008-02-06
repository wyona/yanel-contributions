package org.wyona.yanel.gwt.client.ui.gallery;

public class HTMLItem extends Item {
    private String html = null;
    public HTMLItem(String caption, String html){
        super(caption);
        this.html = html;
    }
    
    public String getHTML(){
        return html;
    }
}
