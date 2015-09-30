@ECHO OFF

SET CURRENT_DIR=%~dp0

CALL %CURRENT_DIR%\qzerver-env.bat

IF EXIST %CATALINA_PID% (
    ECHO "PID file is found. Tomcat seems to be already running."
    EXIT 1
)

IF EXIST %CATALINA_OUT% (
    DEL /Q %CATALINA_OUT%
)

%CATALINA_HOME%\bin\catalina.bat run
