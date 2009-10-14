  

  AJAX Components based on GWT (Google Web Toolkit)
  --------------------------------------------------

    0) Download and install Java and Apache Ant

    1) Download GWT from http://code.google.com/webtoolkit/download.html

    2) Copy build.properties to local.build.properties and reconfigure
          - the path to the downloaded (and unpacked) GWT package
          - the operating system specific gwt dev lib name
       within local.build.properties

    3) Start building components (check JAVA_HOME), e.g.
          ant google.compile -Dcomponent.home.dir=src/hello-world
       A build directory should be created within the component directory, e.g.
          src/hello-world/build

    4) a) Test components (e.g. open a browser and open one of the test files, for example src/hello-world/build/org.wyona.yanel.gwt.helloworld.HelloWorld/TestHelloWorld.html)
       b) run target google.hostmode to open the gwt host mode browser (e.g. "ant google.hostmode -Dcomponent.home.dir=src/hello-world")
          IMPORTANT: add add a property hostmode.hostpage.name in your components build property file. (see component access-policy-editor)
