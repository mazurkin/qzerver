#!/bin/sh

CURRENT_DIR=$(dirname $(readlink -f $0))

. "${CURRENT_DIR}/qzerver-env.sh"

[ -r "${CATALINA_OUT}" ] && rm "${CATALINA_OUT}"

"${CATALINA_HOME}/bin/startup.sh"