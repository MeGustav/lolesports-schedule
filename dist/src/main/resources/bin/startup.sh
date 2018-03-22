#!/bin/sh
# Assigning application classpath
OPTS="-Dloader.path=../conf,../lib"
exec java $OPTS -jar ../lib/application*.jar