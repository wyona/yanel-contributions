#!/bin/sh

SCRIPT_DIR=$PWD
#SCRIPT_DIR=`dirname $0`

echo "INFO: Build minimal Yanel version ..."

# ----- Parameters

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo "       MacOS X : setenv JAVA_HOME /System/Library/Frameworks/JavaVM.framework/Home"
  echo "       Linux   : export JAVA_HOME=/usr/local/j2sdk-..."
  echo "       Windows : Click Start ..."
  exit 1
fi

# ----- Check Java version
# TODO: ....

# ----- Set Environment Variables
unset ANT_HOME
ANT_HOME=$SCRIPT_DIR/tools/apache-ant
#echo $ANT_HOME
OUR_ANT="ant -lib $SCRIPT_DIR/tools/apache-ant_extras -f build.xml"

unset CATALINA_HOME

PATH=$PWD/tools/maven-2.0.4/bin:$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Build Yanel Minimal
#mvn --version
$OUR_ANT -version
# One might want to use the option "-f" for building resources, e.g. "./build.sh -f src/resources/xml/build.xml" instead having to build everything
if [ "$1" = "-f" ];then
  $OUR_ANT -f $2
  exit 0
fi
# Build everything by default
$OUR_ANT -f build.xml "$@"
