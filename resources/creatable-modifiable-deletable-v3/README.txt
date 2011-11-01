
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


 Developing this resource:
 -------------------------

 - Comment existing dependency inside src/build/dependencies.xml and src/build/pom-core.xml

 - build clean Yanel

 - Remove libraries from local Maven Repository, e.g. rm /Users/michaelwechner/.m2/repository/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v3/

 - Set new version at yanel-contributions/resources/creatable-modifiable-deletable-v3/src/build/build.properties

 - Reference source inside conf/local/local.resource-types.xml

 - Build resource: ./build.sh -f ../yanel-contributions/resources/creatable-modifiable-deletable-v3/build.xml

 - Add this new version to src/build/pom-core.xml (NOT yet to src/build/dependencies.xml)

 - Re-build Yanel

 - Keep changing source code, rebuilding and testing of this resource

 - If ready, then create self-contained binary/jar (IMPORTANT: See above how to set copy.resource-type-configs.to.webapp=true)

 - Copy binary/jar to Maven server

 - Add ths new version to src/build/dependencies.xml

 - Build clean Yanel

 - Reference library inside conf/local/local.resource-types.xml (and remove/comment reference to source)

 - Clean local Maven repository, e.g. rm /Users/michaelwechner/.m2/repository/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v
3/

 - Re-build Yanel and make sure there is only one resource library and that it is self-contained: jar -tf local/apache-tomcat-5.5.20/webapps/yanel/WEB-INF/lib/yanel-resource-creatable-modifiable-deletable-VERSION.jar | grep resource.xml

- Commit/push changes
