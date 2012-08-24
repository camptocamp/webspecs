#!/bin/sh
SBT_OPTS="$SBT_OPTS -Dfile.encoding=UTF8 -Xmx1536M -Xss10M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m"
#SBT_OPTS="$SBT_OPTS  -Dadmin.user=admin -Dadmin.pass=admin -Dtest.server=localhost:9624"
exec java ${SBT_OPTS} -jar ./sbt-launch.jar "$@"
