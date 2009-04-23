
How to deploy the lookup-tree component within Yanel:
---------------------------------------------------------
Compile: 

ant google.compile -Dcomponent.home.dir=src/lookup-tree/ && rm -rf /home/USERNAME/src/yanel/src/contributions/resources/lookup/htdocs/js/*.* && cp -r src/lookup-tree/build/org.wyona.yanel.navigation.gwt.lookuptree.LookupTree/*.* /home/USERNAME/src/yanel/src/contributions/resources/lookup/htdocs/js/

Configuration:

JavsScript var lookupTreeConfig holds an key/value array with following config-properties:

lookup-panel-border: root panel border.
lookup-panel-padding: root panel padding.
lookup-treepanel-width: tree panel width.
lookup-treepanel-height: tree panel height.
lookup-gridpanel-width: grid panel width.
lookup-gridpanel-height: grid panel height.
lookup-root-node-label: label of the root node (tree panel)
lookup-hook: id of the div in the calling html wher the widget is attached to.
lookup-request-paramter-type: strange way to pass the request-paramter type. (TODO: could not find a way to access request parameter within the gwt.)
lookup-upload-action-url: to where should a upload request be sent.
lookup-upload-submit-button-label: the label of the submit button of the uplad form
lookup-upload-enabled: true/false to disable the upload widget.
            
Exampe lookupTreeConfig:

var lookupTreeConfig = {
              "lookup-panel-border": "false", 
              "lookup-panel-padding": "15", 
              "lookup-treepanel-width": "160", 
              "lookup-treepanel-height": "340", 
              "lookup-gridpanel-width": "240", 
              "lookup-gridpanel-height": "340", 
              "lookup-root-node-label": "test",
              "lookup-hook": "lookupHook",
              "lookup-request-paramter-type": '${resource.getParameterAsString("type")}',
              "lookup-upload-action-url": "${yanel.back2realm}create-new-page.html",
              "lookup-upload-submit-button-label": "Upload",
              "lookup-upload-enabled": "true"
          }; 