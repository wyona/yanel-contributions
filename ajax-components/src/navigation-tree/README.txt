
How to deploy the navigation-tree component within Yanel:
---------------------------------------------------------

ant google.compile -Dcomponent.home.dir=src/navigation-tree/ && rm -rf /home/USERNAME/src/yanel-projects/src/contributions/resources/data-repo-sitetree/htdocs/js/* && cp -r src/navigation-tree/build/org.wyona.yanel.navigation.gwt.navigationtree.NavigationTree/* /home/USERNAME/src/yanel-projects/src/contributions/resources/data-repo-sitetree/htdocs/js/
