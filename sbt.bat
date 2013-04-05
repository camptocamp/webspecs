REM @echo off

set SBT_OPTS=-Dfile.encoding=UTF8 -Xmx1536M -Xss10M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m
REM set SBT_OPTS=%SBT_OPTS%  -Dadmin.user=admin -Dadmin.pass=Hup9ieBe  -Dwebspecs.config="/Users/jeichar/ScalaProject/webspecs/geonetwork/geocat/src/test/resources/geocat/jesse_geocat_config.properties"
set SBT_OPTS=%SBT_OPTS% -Dadmin.user=admin -Dadmin.pass=admin -Dtest.server=localhost:8190


set SCRIPT_DIR=%~dp0
java %SBT_OPTS% -jar "%SCRIPT_DIR%sbt-launch.jar" %*