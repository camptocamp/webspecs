#!/bin/sh
test -f ~/.sbtconfig && . ~/.sbtconfig
SBT_OPTS="$SBT_OPTS -Dfile.encoding=UTF8 -Xmx1536M -Xss10M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m" 
exec java ${SBT_OPTS} -jar ./sbt-launch.jar "$@"
