
How to deploy the lookup-tree component within Yanel:
---------------------------------------------------------

ant google.compile -Dcomponent.home.dir=src/lookup-tree/ && rm -rf /home/USERNAME/src/yanel-projects/src/contributions/resources/lookup/htdocs/js/* && cp -r src/lookup-tree/build/org.wyona.yanel.navigation.gwt.lookuptree.LookupTree/* /home/USERNAME/src/yanel-projects/src/contributions/resources/lookup/htdocs/js/
