package org.wyona.yanel.gwt.client.ui.gallery;



import org.wyona.yanel.gwt.client.AsynchronousAgent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class AsynchronousGalleryBuilder extends AsynchronousAgent{
    private Gallery gallery = null;
    
    public AsynchronousGalleryBuilder(String url){
        super(url);
    }
    
    public AsynchronousGalleryBuilder(String url, RequestBuilder.Method method){
        super(url, method);
    }
    
    public void onError(Request request, Throwable exception) {        
        super.onError(request, exception);
        
        gallery = new Gallery(){
            protected void init() {
            }
            protected void retrieveItem(int index) {
            }
        };
    }
    
    public void onResponseReceived(final Request request, final Response response) {
        gallery = new Gallery(){
            protected void init() {
                final Element gallery = XMLParser.parse(response.getText()).getDocumentElement();
                String feedTitle = getFirstElementTextContent(gallery.getElementsByTagName("title"));
                String feedSubtitle = getFirstElementTextContent(gallery.getElementsByTagName("subtitle"));
                String feedSummary = getFirstElementTextContent(gallery.getElementsByTagName("summary"));
                
                if(feedTitle == null){
                    feedTitle = "NO TITLE";
                }
                if(feedSubtitle == null){
                    feedSubtitle = "NO SUB TITLE";
                }
                if(feedSummary == null){
                    feedSummary = "NO SUMMARY";
                }
                
                setTitle(feedTitle);
                setSubtitle(feedSubtitle);
                setSummary(feedSummary);
                
                NodeList nl = gallery.getElementsByTagName("entry");
                for (int j = 0; j < nl.getLength(); j++) {
                    Element item = (Element)nl.item(j);
                    String caption = getFirstElementTextContent(item.getElementsByTagName("title"));
                    
                    String src = ((Element)item.getElementsByTagName("content").item(0)).getAttribute("src");
                    
                    String summary = getFirstElementTextContent(item.getElementsByTagName("summary"));
                    
                    
                    Item i = new ImageItem(caption, src);
                    i.setSummary(summary);
                    
                    itemCache.put(new Integer(j), i);
                    size++;
                }
                
                if(size <= 0){
                    itemCache.put(new Integer(0), Item.getNoItemsItem());
                    size++;
                }
                
                selectItem(0);
            }
            
            protected void retrieveItem(int index) {
                // Everything is cached during init()
            }
        };
    }
    
    private String getFirstElementTextContent(NodeList elements){
        String result = null;
        if(elements.getLength() > 0){
            result = elements.item(0).getFirstChild().getNodeValue();//.toString();
        }
        return result;
    }
    
    /**
     * Should be called after executing the request
     * */
    public Gallery getGallery(){
        return gallery;
    }
}
