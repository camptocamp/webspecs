#!/bin/sh
java -XX:MaxPermSize=250m -server -Xmx512M -jar `dirname $0`/sbt-launch.jar "$@"