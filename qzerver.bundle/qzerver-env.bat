@ECHO OFF

SET CURRENT_DIR=%~dp0

REM =======================================================
REM Search for WAR package
REM =======================================================

FOR %%A in (%CURRENT_DIR%\*.war) DO (
    SET WAR=%%A
    GOTO :WARFOUND
)

:WARFOUND

REM =======================================================
REM Tomcat options (see catalina.sh for details)
REM =======================================================

SET CATALINA_HOME=%CURRENT_DIR%\tomcat-6.0.35

SET CATALINA_BASE=%CURRENT_DIR%\tomcat-base

SET CATALINA_OUT=%CURRENT_DIR%\var\log\tomcat\stdout.log

SET CATALINA_TMPDIR=%CURRENT_DIR%\var\tmp

SET CATALINA_PID=%CURRENT_DIR%\var\run\qzerver.pid

SET CATALINA_OPTS=-Xms128M -Xmx256M -XX:MaxPermSize=200M
SET CATALINA_OPTS=%CATALINA_OPTS% -Dderby.stream.error.file=${CURRENT_DIR}/var/log/derby.log

SET JAVA_OPTS=-Dtomcat.http.listener=127.0.0.1
SET JAVA_OPTS=%JAVA_OPTS% -Dtomcat.http.port=5333
SET JAVA_OPTS=%JAVA_OPTS% -Dtomcat.shutdown.port=5334
SET JAVA_OPTS=%JAVA_OPTS% -Dqzerver.wrk=%CURRENT_DIR%\var\work
SET JAVA_OPTS=%JAVA_OPTS% -Dqzerver.log=%CURRENT_DIR%\var\log
SET JAVA_OPTS=%JAVA_OPTS% -Dqzerver.war=%WAR%

REM =======================================================
REM QZERVER embedded database folder
REM =======================================================

SET QZERVER_DB=%CURRENT_DIR%\var\db\qzerver

REM =======================================================
REM QZERVER settings directory
REM =======================================================

SET QZERVER_CONFIGURATION=%CURRENT_DIR%\configuration
