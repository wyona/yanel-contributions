
 README
 ======

 Building this resource
 ----------------------

 Since most realms/resources are using this resource as package one needs to build it as package:

 - Increase the 'resource.version' number within src/build/build.properties and remove the build directory

 - IMPORTANT: Make sure to set copy.resource-type-configs.to.webapp=true within YANEL_HOME/src/build/local.build.properties

 - Run ./build.sh -f /Users/michaelwechner/src/wyona/public/yanel/contributions/resources/creatable-modifiable-deletable-v3/build.xml

 - Check whether org/wyona/yanel/impl/resources/jellyadapterofcmdv3/resource.xml exists by running jar -tf local/apache-tomcat-5.5.20/webapps/yanel/WEB-INF/lib/yanel-resource-creatable-modifiable-deletable-v3-0.1-dev-r47364.jar

 - Copy to maven server (from your local maven repository, e.g. /Users/michaelwechner/.m2/repository/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v3)

 - Update src/build/dependencies.xml src/build/pom-core.xml

 - Set copy.resource-type-configs.to.webapp=false

 - build clean Yanel

 - Clean your local maven repository, e.g. rm -r /Users/michaelwechner/.m2/repository/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v3

 - build Yanel

 - Run tests

 Using this resource:
 --------------------

 - Add this <resource-type package="org.wyona.yanel.impl.resources.jellyadapterofcmdv3"/> to your resource types configuration
