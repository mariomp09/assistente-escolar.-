#!/bin/sh
set -e

APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
WRAPPER_DIR="$APP_HOME/gradle/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/gradle-wrapper.jar"
WRAPPER_URL="https://raw.githubusercontent.com/gradle/gradle/v8.10.2/gradle/wrapper/gradle-wrapper.jar"

if [ ! -s "$WRAPPER_JAR" ]; then
    mkdir -p "$WRAPPER_DIR"
    if command -v curl >/dev/null 2>&1; then
        echo "A transferir gradle-wrapper.jar (v8.10.2)..."
        curl -fsSL "$WRAPPER_URL" -o "$WRAPPER_JAR" || rm -f "$WRAPPER_JAR"
    elif command -v wget >/dev/null 2>&1; then
        echo "A transferir gradle-wrapper.jar (v8.10.2)..."
        wget -qO "$WRAPPER_JAR" "$WRAPPER_URL" || rm -f "$WRAPPER_JAR"
    elif command -v python3 >/dev/null 2>&1; then
        echo "A transferir gradle-wrapper.jar (v8.10.2)..."
        python3 -c "import urllib.request; urllib.request.urlretrieve('$WRAPPER_URL', '$WRAPPER_JAR')" || rm -f "$WRAPPER_JAR"
    fi
fi

if [ ! -s "$WRAPPER_JAR" ]; then
    if command -v gradle >/dev/null 2>&1; then
        exec gradle "$@"
    fi
    echo "ERRO: gradle-wrapper.jar ausente. Ligue à internet e tente novamente, ou execute: gradle wrapper --gradle-version 8.10.2" >&2
    exit 1
fi

if [ -n "$JAVA_HOME" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ]; then
        echo "ERRO: JAVA_HOME aponta para um diretório inválido: $JAVA_HOME" >&2
        exit 1
    fi
else
    JAVACMD=$(command -v java || true)
    if [ -z "$JAVACMD" ]; then
        echo "ERRO: 'java' não encontrado no PATH e JAVA_HOME não definido." >&2
        exit 1
    fi
fi

exec "$JAVACMD" \
    -Xmx64m -Xms64m \
    "-Dorg.gradle.appname=gradlew" \
    -classpath "$WRAPPER_JAR" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
