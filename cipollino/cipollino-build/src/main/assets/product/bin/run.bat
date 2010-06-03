@echo off
set "DIRNAME=%~dp0%"

set CIPOLLINO_HOME=%DIRNAME%..

set JAVA_OPTS=-Dlog4j.configuration=file:%CIPOLLINO_HOME%/conf/cipollino-log4j.xml -Dcipollino.log.file=%CIPOLLINO_HOME%/log/cipollino.log

if "x%JAVA_HOME%" == "x" (
  set  JAVA=java
  echo JAVA_HOME is not set. Unexpected results may occur.
  echo Set JAVA_HOME to the directory of your local JDK to avoid this message.
) else (
  set "JAVA=%JAVA_HOME%\bin\java"
)

"%JAVA%" %JAVA_OPTS% -jar "%CIPOLLINO_HOME%/lib/cipollino-agent.jar" %*

