@ECHO OFF

SET CURRENT_DIR=%~dp0

CALL %CURRENT_DIR%\qzerver-env.bat

%CATALINA_HOME%\bin\shutdown.bat
