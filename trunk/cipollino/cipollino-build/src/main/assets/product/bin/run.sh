#!/bin/sh

DIRNAME=`dirname $0`

CIPOLLINO_HOME=`cd $DIRNAME/..; pwd`

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi

JAVA_OPTS="-Dlog4j.configuration=file:$CIPOLLINO_HOME/conf/cipollino-log4j.xml -Dcipollino.log.file=$CIPOLLINO_HOME/log/cipollino.log"

$JAVA $JAVA_OPTS -jar $CIPOLLINO_HOME/lib/cipollino-agent-0.2-SNAPSHOT.jar $*