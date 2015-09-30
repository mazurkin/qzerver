@ECHO OFF

SET CURRENT_DIR=%~dp0

CALL %CURRENT_DIR%\qzerver-env.bat

IF EXIST %CATALINA_OUT% (
    DEL /Q %CATALINA_OUT%
)

%CATALINA_HOME%\bin\startup.bat
