
 README
 ======

 Building this resource
 ----------------------

 Since most realms/resources are using this resource as package one needs to build it as package:

 - Make sure to set copy.resource-type-configs.to.webapp=true within src/build/local.build.properties

 - Run ./build.sh -f /Users/michaelwechner/src/wyona/public/yanel/contributions/resources/creatable-modifiable-deletable-v3/build.xml

 - Check jar -tf local/apache-tomcat-5.5.20/webapps/yanel/WEB-INF/lib/yanel-resource-creatable-modifiable-deletable-v3-0.1-dev-r47364.jar and make sure that org/wyona/yanel/impl/resources/jellyadapterofcmdv3/resource.xml exists
