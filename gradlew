#!/bin/sh
set -e

# Gradle wrapper script for Unix
DIRNAME="$(dirname "$0")"

CLASSPATH=""
GRADLE_BIN="gradle"

if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

echo "Downloading Gradle..."
GRADLE_VERSION="8.10.2"
GRADLE_ZIP="/tmp/gradle-${GRADLE_VERSION}-bin.zip"
GRADLE_DIR="/tmp/gradle-${GRADLE_VERSION}"

if [ ! -d "$GRADLE_DIR" ]; then
    curl -sSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "$GRADLE_ZIP"
    unzip -q -o "$GRADLE_ZIP" -d /tmp/
fi

exec "$GRADLE_DIR/bin/gradle" "$@"
