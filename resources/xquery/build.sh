#!/bin/sh

JAVA_HOME="$JAVA_HOME"
if ["$JAVA_HOME" = ""]; then
  echo "ERROR: No JAVA_HOME set!"
  exit 1
fi

echo "JAVE_HOME: $JAVA_HOME"

if [ $# -ne 1 ]; then
  echo "ERROR: No xquery file specified. For example: sh build.sh examples/hello-world.xq"
  exit 1
fi

echo "Start Saxon XQuery ..."

CLASSPATH=lib/saxon9-dom4j.jar:lib/saxon9-dom.jar:lib/saxon9.jar

$JAVA_HOME/bin/java -classpath $CLASSPATH net.sf.saxon.Query -q:$1
