#!/bin/sh
# Standard Gradle wrapper shell script.
# Run "gradle wrapper" in Android Studio to regenerate the wrapper JAR,
# or open this project in Android Studio and it will do this automatically.
DIR="$(cd "$(dirname "$0")" && pwd)"
exec "$DIR/gradle/wrapper/gradle" "$@"
