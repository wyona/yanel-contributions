package org.wyona.yanel.navigation.gwt.lookuptree.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

class LookupCreatFolderPanel extends FormPanel {
    private Hidden savePath = new Hidden();
    private String currentPath = "/";
    private LookupGrid grid;
    public LookupCreatFolderPanel(String actionUrl, String submitLabel, String defaultFolderName){
        final FormPanel form = this;
        form.setMethod(FormPanel.METHOD_POST);
        final HorizontalPanel formHolder = new HorizontalPanel();
        
        Hidden resourceType = new Hidden();
        resourceType.setName("resource-type");
        resourceType.setValue("http://www.wyona.org/yanel/resource/1.0::directory");
        
        savePath.setName("lookin");
        savePath.setValue(currentPath);
        
        Hidden saveParameter = new Hidden();
        saveParameter.setName("save");
        saveParameter.setValue("save");

        TextBox FolderName = new TextBox();
        FolderName.setName("create-name");
        FolderName.setText(defaultFolderName);
        
        formHolder.add(resourceType);
        formHolder.add(savePath);
        formHolder.add(saveParameter);
        formHolder.add(FolderName);
//        formHolder.add(upload);
        
        form.add(formHolder);
        form.setAction(actionUrl);
        
        // Add a 'submit' button.
        formHolder.add(new Button(submitLabel, new ClickListener() {
          public void onClick(Widget sender) {
            form.submit();
          }
        }));

        // Add an event handler to the form.
        form.addFormHandler(new FormHandler() {
          public void onSubmit(FormSubmitEvent event) {
            // This event is fired just before the form is submitted. We can take
            // this opportunity to perform validation.
              savePath.setValue(currentPath);
          }

          public void onSubmitComplete(FormSubmitCompleteEvent event) {
            // When the form submission is successfully completed, this event is
            // fired. Assuming the service returned a response of type text/html,
            // we can get the result text here (see the FormPanel documentation for
            // further explanation).
              final Window window = new Window();  
              window.setTitle("Window Panel");  
              window.setMaximizable(true);  
              window.setResizable(true);  
              window.setLayout(new FitLayout());  
              window.setWidth(200);  
              window.setHeight(200);  
              window.setModal(false);
              window.setAutoScroll(true);
              window.setHtml(event.getResults());
              window.show();
              grid.updateData();
          }
        });            
    }
    
    public String getCurrentPath() {
        return currentPath;
    }
    
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }
    
    public void setGrid(LookupGrid grid) {
        this.grid = grid;
    }
}