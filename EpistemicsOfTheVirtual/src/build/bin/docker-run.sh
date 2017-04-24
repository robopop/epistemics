#!/bin/bash

set -e -x

BIN="$(cd "$(dirname "$0")" ; pwd)"
BUILD="$(dirname "${BIN}")"
SRC="$(dirname "${BUILD}")"
PROJECT="$(dirname "${SRC}")"
DATA="${PROJECT}/data"
SELEMCA="${DATA}/selemca"

function remove-container() {
	local NAME="$1"
	local RUNNING_FLAG="$(docker inspect --format '{{.State.Running}}' "${NAME}" 2>/dev/null || true)"
	if [ -n "${RUNNING_FLAG}" ]
	then
       		if "${RUNNING_FLAG}"
		then
			docker stop "${NAME}"
		fi
		docker rm "${NAME}"
	fi
}

remove-container epistemics
remove-container epistemics-mysql

mkdir -p "${DATA}"

"${BIN}/create-db-user.sh"

if [ \! -d "${SELEMCA}" ]
then
    mkdir -p "${SELEMCA}"
    cp "${BUILD}/resources/selemca"/*.properties "${SELEMCA}"
    gunzip -c "${BUILD}/resources/selemca/wordnet_2_0.rdf.gz" > "${SELEMCA}/wordnet_2_0.rdf"
fi

docker run --name 'epistemics' -p 8888:8080 -d -v "${SELEMCA}:/usr/local/selemca" --link epistemics-mysql jeroenvm/epistemics

sleep 10
docker logs epistemics
