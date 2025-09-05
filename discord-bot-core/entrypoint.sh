#!/bin/sh
echo "JAVA_ARGS: $JAVA_ARGS" >&2
exec java -jar app.jar "$JAVA_ARGS"