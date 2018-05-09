@echo off

set JAR_NAME=MoneyCalcServer
set JAVA_OPTS=-Xmx1G -XX:+UseG1GC -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

for /f "delims=" %%x in ('dir /od /b /t:c %JAR_NAME%*.jar') do set LATEST_JAR=%%x

start "%LATEST_JAR%" java %JAVA_OPTS% -jar %LATEST_JAR%