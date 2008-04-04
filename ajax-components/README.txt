  

  AJAX Components based on GWT (Google Web Toolkit)
  --------------------------------------------------

    0) Download and install Apache Ant

    1) Download GWT from http://code.google.com/webtoolkit/download.html

    2) Copy build.properties to local.build.properties and reconfigure
          - the path to the downloaded (and unpacked) GWT package
          - the operating system specific gwt dev lib name
       within local.build.properties

    3) Start building components (e.g. "ant -Dcomponent.home.dir=src/hello-world". A build directory should be created within the component directory, e.g. src/hello-world/build)

    4) Test components (e.g. open a browser and open one of the test files, for example src/hello-world/build/org.wyona.yanel.gwt.helloworld.HelloWorld/TestHelloWorld.html)
