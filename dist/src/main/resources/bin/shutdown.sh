#!/bin/sh
CURRENT_DIR="$(cd "$(dirname "$0")" && pwd)"
PID=$(cat $CURRENT_DIR/application.pid)
kill $PID