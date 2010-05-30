set "DIRNAME=%~dp0%"

set CIPOLLINO_HOME=%DIRNAME%..

set JAVA_OPTS=-Dlog4j.configuration=file:%CIPOLLINO_HOME%/conf/cipollino-log4j.xml -Dcipollino.log.file=%CIPOLLINO_HOME%/log/cipollino.log

set JAVA=java

%JAVA% %JAVA_OPTS% -jar %CIPOLLINO_HOME%/lib/cipollino-agent-0.2-SNAPSHOT.jar %*

