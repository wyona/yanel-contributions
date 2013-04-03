
 README
 ======

 Building this resource
 ----------------------

 Since most realms/resources are using this resource as package one needs to build it as package:

 - Increase the 'resource.version' number inside src/build/build.properties, e.g. 1.0.0-ra0ae30104db2d5407928fd1f41e94a7a1f66ac8e
   and remove the build directory

 - IMPORTANT: Make sure to set copy.resource-type-configs.to.webapp=true inside YANEL_HOME/src/build/local.build.properties

 - Run from within YANEL_HOME ./build.sh -f /Users/michaelwechner/src/wyona/public/yanel-contributions/resources/creatable-modifiable-deletable-v3/build.xml

 - Check whether org/wyona/yanel/impl/resources/jellyadapterofcmdv3/resource.xml exists by running:
   jar -tf ~/.m2/repository/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v3/1.0.0-ra0ae30104db2d5407928fd1f41e94a7a1f66ac8e/yanel-resource-creatable-modifiable-deletable-v3-1.0.0-ra0ae30104db2d5407928fd1f41e94a7a1f66ac8e.jar

 - Copy to remote maven server (from your local maven repository, e.g. scp -r ~/.m2/repository/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v3/1.0.0-ra0ae30104db2d5407928fd1f41e94a7a1f66ac8e wyona@maven2.wyona.org:www/realms/maven2/repos/data-repo/data/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v3/.)

 - Update YANEL_HOME/src/build/dependencies.xml and YANEL_HOME/src/build/pom-core.xml

 - Set copy.resource-type-configs.to.webapp=false inside YANEL_HOME/src/build/local.build.properties

 - build clean Yanel

 - Clean your local maven repository, e.g. rm -r ~/.m2/repository/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v3

 - build Yanel

 - Run tests

 Using this resource:
 --------------------

 - Add this <resource-type package="org.wyona.yanel.impl.resources.jellyadapterofcmdv3"/> to your resource types configuration


 Developing this resource:
 -------------------------

 - Comment existing dependency inside src/build/dependencies.xml and src/build/pom-core.xml

 - build clean Yanel

 - Remove libraries from local Maven Repository, e.g. rm ~/.m2/repository/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v3/

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

 - Clean local Maven repository, e.g. rm -r ~/.m2/repository/wyona-org-yanel/yanel-resource-creatable-modifiable-deletable-v
3/

 - Re-build Yanel and make sure there is only one resource library and that it is self-contained: jar -tf local/apache-tomcat-5.5.20/webapps/yanel/WEB-INF/lib/yanel-resource-creatable-modifiable-deletable-VERSION.jar | grep resource.xml

- Commit/push changes
