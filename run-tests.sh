#!/usr/bin/env bash
# Compiles the game and JUnit tests, then runs the tests.
# Run from anywhere; game data is read relative to the repo root.
# Usage: ./run-tests.sh [extra JUnit console options]
set -euo pipefail

cd "$(dirname "$0")"

JUNIT_VERSION="1.10.2"
JUNIT_SHA1="552303cce67dc3485ded393d744c0f6b5cae355b"
JUNIT_NAME="junit-platform-console-standalone-${JUNIT_VERSION}.jar"
JUNIT_URL="https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/${JUNIT_VERSION}/${JUNIT_NAME}"
JUNIT_JAR="lib/${JUNIT_NAME}"

if [ ! -f "$JUNIT_JAR" ]; then
    echo "Downloading ${JUNIT_NAME}..."
    mkdir -p lib
    curl -fsSL "$JUNIT_URL" -o "${JUNIT_JAR}.tmp"
    if [ "$(shasum -a 1 "${JUNIT_JAR}.tmp" | cut -d' ' -f1)" != "$JUNIT_SHA1" ]; then
        rm -f "${JUNIT_JAR}.tmp"
        echo "Checksum mismatch for ${JUNIT_NAME}" >&2
        exit 1
    fi
    mv "${JUNIT_JAR}.tmp" "$JUNIT_JAR"
fi

rm -rf build
mkdir -p build/classes build/test-classes

find src -name '*.java' > build/main-sources.txt
find test -name '*.java' > build/test-sources.txt

javac -nowarn -encoding UTF-8 -d build/classes @build/main-sources.txt
javac -nowarn -encoding UTF-8 -cp "build/classes:${JUNIT_JAR}" -d build/test-classes @build/test-sources.txt

# The game prints heavily to the console, so keep it in a file and show only the results.
status=0
java -jar "$JUNIT_JAR" execute \
    --class-path "build/test-classes:build/classes" \
    --scan-class-path build/test-classes \
    --details summary \
    --disable-banner \
    "$@" > build/test-output.txt 2>&1 || status=$?

awk '/^Failures \(|^Test run finished/ { show = 1 } show' build/test-output.txt
echo "Full output: build/test-output.txt"
exit "$status"
