package org.wyona.yanel.gwt.client.ui.upload;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This must be inside a form
 * */
public class UploadPanel extends Composite{
	private static final String FILE_UPLOAD_FIELD_NAME_PREFIX = "uploaded-";
	private VerticalPanel uploadItemsPanel = new VerticalPanel();
	
	private int initialUploadItems = 3;
	
	private UploadPanel(){
		
	}
	
	public UploadPanel(int initialUploadItems) {
		this();
		this.initialUploadItems = initialUploadItems < 0 && initialUploadItems > 20 ? this.initialUploadItems : initialUploadItems;
		
		for (int i = 0; i < initialUploadItems; i++) {
			uploadItemsPanel.add(createUploadItem());
		}
		
		Button uploadAnother = new Button("Upload another file");
		uploadAnother.addClickListener(new ClickListener(){
			public void onClick(Widget sender) {
				uploadItemsPanel.add(createUploadItem());
			}
		});
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(uploadItemsPanel);
		vp.add(uploadAnother);
		
		initWidget(vp);
	}
	
	private HorizontalPanel createUploadItem(){
		final HorizontalPanel hp = new HorizontalPanel();
		
		FileUpload fu = new FileUpload();
		fu.setName(FILE_UPLOAD_FIELD_NAME_PREFIX+String.valueOf(uploadItemsPanel.getWidgetCount()));
		hp.add(fu);
		
		if(uploadItemsPanel.getWidgetCount() >= initialUploadItems){
			Button remove = new Button("remove");
			hp.add(remove);
			
			remove.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					uploadItemsPanel.remove(hp);
				}
			});
		}
		return hp;
	}
}
