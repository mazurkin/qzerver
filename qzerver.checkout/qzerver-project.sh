#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

BASE_DIR=$(dirname $(readlink -f $0))

# Maven options
export MAVEN_OPTS="${MAVEN_OPTS:--Xmx1024m -XX:MaxPermSize=256M}"

# Configuration folder with no overriding options in
export QZERVER_CONFIGURATION="${BASE_DIR}/configuration"

# Database directory
export QZERVER_DB="${BASE_DIR}/db"
mkdir -p "${QZERVER_DB}"

# Build function
function build_gainmatrix_artefact() {
    artefact=$1
    branches=$2

    git clone "https://mazurkin@code.google.com/p/gainmatrix.${artefact}/" "${BASE_DIR}/artefacts/${artefact}"

    for branch in ${branches}
    do
        (cd "${BASE_DIR}/artefacts/${artefact}" && git checkout "${branch}" && mvn clean install)
    done
}

# GainMatrix maven artefacts
build_gainmatrix_artefact "maven-environment" "gainmatrix-maven-environment-1.4 master"
build_gainmatrix_artefact "maven-checkstyle" "master"
build_gainmatrix_artefact "maven-log4j" "gainmatrix-maven-log4j-1.2 master"
build_gainmatrix_artefact "maven-parent" "master"

# GainMatrix library artefacts
build_gainmatrix_artefact "lib-beans" "master"
build_gainmatrix_artefact "lib-cache" "master"
build_gainmatrix_artefact "lib-spring" "master"
build_gainmatrix_artefact "lib-web" "master"
build_gainmatrix_artefact "lib-liquibase" "master"
build_gainmatrix_artefact "lib-freemarker-core" "master"
build_gainmatrix_artefact "lib-freemarker-web" "master"
build_gainmatrix_artefact "lib-jpa" "master"
build_gainmatrix_artefact "lib-log4j" "master"
build_gainmatrix_artefact "lib-validation" "master"

# Qzerver bundle
git clone "https://mazurkin@code.google.com/p/qzerver.bundle/" "${BASE_DIR}/artefacts/qzerver-bundle"

# Qzerver
git clone "https://mazurkin@code.google.com/p/qzerver/" "${BASE_DIR}/artefacts/qzerver"

(cd "${BASE_DIR}/artefacts/qzerver" && mvn clean install)
