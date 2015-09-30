#!/bin/sh

CURRENT_DIR=$(dirname $(readlink -f $0))

# =========================================================
# Search for WAR package
# =========================================================

WAR=$(find ${CURRENT_DIR} -maxdepth 1 -name '*.war' | head -n 1)

# =========================================================
# Tomcat options (see catalina.sh for details)
# =========================================================

export CATALINA_HOME="${CURRENT_DIR}/tomcat-6.0.35"

export CATALINA_BASE="${CURRENT_DIR}/tomcat-base"

export CATALINA_OUT="${CURRENT_DIR}/var/log/tomcat/stdout.log"

export CATALINA_TMPDIR="${CURRENT_DIR}/var/tmp"

export CATALINA_PID="${CURRENT_DIR}/var/run/qzerver.pid"

CATALINA_OPTS="-Xms128M -Xmx256M -XX:MaxPermSize=200M"
CATALINA_OPTS="${CATALINA_OPTS} -Dderby.stream.error.file=${CURRENT_DIR}/var/log/derby.log"
export CATALINA_OPTS

JAVA_OPTS="-Dtomcat.http.listener=127.0.0.1"
JAVA_OPTS="${JAVA_OPTS} -Dtomcat.http.port=5333"
JAVA_OPTS="${JAVA_OPTS} -Dtomcat.shutdown.port=5334"
JAVA_OPTS="${JAVA_OPTS} -Dqzerver.wrk=${CURRENT_DIR}/var/work"
JAVA_OPTS="${JAVA_OPTS} -Dqzerver.log=${CURRENT_DIR}/var/log"
JAVA_OPTS="${JAVA_OPTS} -Dqzerver.war=${WAR}"
export JAVA_OPTS

# =========================================================
# QZERVER embedded database folder
# =========================================================

export QZERVER_DB="${CURRENT_DIR}/var/db"

# =========================================================
# QZERVER settings directory
# =========================================================

export QZERVER_CONFIGURATION="${CURRENT_DIR}/configuration"
