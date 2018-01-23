#!/bin/sh
# Assigning application classpath
OPTS="-Dloader.path=../conf,../lib"
java $OPTS -jar ../lib/application*.jar