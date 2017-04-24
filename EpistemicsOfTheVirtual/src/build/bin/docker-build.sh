#!/bin/bash

set -e -x

function log() {
    echo "$@" >&2
}

BIN="$(cd "$(dirname "$0")" ; pwd)"
BUILD="$(dirname "${BIN}")"
SRC="$(dirname "${BUILD}")"
PROJECT="$(dirname "${SRC}")"

VERSION="$(cd "${PROJECT}" ; mvn help:evaluate -Dexpression=project.version | grep -v '^[[]')"
log "VERSION=[${VERSION}]"

(
    cd "${BUILD}/docker"

    cp "${PROJECT}/BeliefSystem/Rest/target/BeliefSystem.war" "epistemics-beliefsystem-rest.war"
    cp "${PROJECT}/BeliefSystem/BeliefSystemAdmin/target/BeliefSystemAdmin.war" "epistemics-beliefsystem-webadmin.war"
    cp "${PROJECT}/MentalWorld/WebApp/target/MentalWorldApp.war" "epistemics-mentalworld-webapp.war"
    cp "${PROJECT}/MentalWorld/MentalWorldAdmin/target/MentalWorldAdmin.war" "epistemics-mentalworld-webadmin.war"

	docker build -t "jeroenvm/epistemics:${VERSION}" .
)
