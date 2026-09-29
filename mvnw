#!/bin/sh
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
export MAVEN_USER_HOME="$BASE_DIR/.m2"
exec java -Dmaven.user.home="$MAVEN_USER_HOME" -Dmaven.multiModuleProjectDirectory="$BASE_DIR" -classpath "$BASE_DIR/.mvn/wrapper/maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain "$@"
