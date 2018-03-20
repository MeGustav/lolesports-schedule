#!/bin/sh
# Assigning application classpath
OPTS="-Dloader.path=../conf,../lib -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
java $OPTS -jar ../lib/application*.jar