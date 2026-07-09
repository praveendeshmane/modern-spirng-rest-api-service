#!/bin/sh
BASEDIR=$(cd "$(dirname "$0")"; pwd -P)
WRAPPER_JAR="$BASEDIR/.mvn/wrapper/maven-wrapper.jar"
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper jar..."
  mkdir -p "$BASEDIR/.mvn/wrapper"
  curl -fsSL -o "$WRAPPER_JAR" "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
fi
exec java -Dmaven.multiModuleProjectDirectory="$BASEDIR" -classpath "$WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"
