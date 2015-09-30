#!/bin/sh

CURRENT_DIR=$(dirname $(readlink -f $0))

. "${CURRENT_DIR}/qzerver-env.sh"

"${CATALINA_HOME}/bin/shutdown.sh"