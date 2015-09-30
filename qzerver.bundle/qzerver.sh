#!/bin/sh

CURRENT_DIR=$(dirname $(readlink -f $0))

. "${CURRENT_DIR}/qzerver-env.sh"

[ -r "${CATALINA_PID}" ] && echo "PID file is found. Tomcat seems to be already running." && exit 1

[ -r "${CATALINA_OUT}" ] && rm "${CATALINA_OUT}"

"${CATALINA_HOME}/bin/catalina.sh" run