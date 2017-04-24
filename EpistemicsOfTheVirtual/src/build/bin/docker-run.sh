#!/bin/bash

set -e -x

BIN="$(cd "$(dirname "$0")" ; pwd)"
BUILD="$(dirname "${BIN}")"
SRC="$(dirname "${BUILD}")"
PROJECT="$(dirname "${SRC}")"
DATA="${PROJECT}/data"
ETC="${DATA}/etc"
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

mkdir -p "${ETC}"

VERSION=''
if [ -n "$1" ]
then
    cat > "${ETC}/last-version.sh" <<EOT
#!/usr/bin/false

VERSION='$1'
EOT
    shift
fi

if [ -r "${ETC}/last-version.sh" ]
then
    . "${ETC}/last-version.sh"
fi
if [ -z "${VERSION}" ]
then
    echo "Missing version. Use: $(basename "$0") <version>" >&2
    exit 1
fi

IMAGE_NAME="jeroenvm/epistemics:${VERSION}"
IMAGE_ID="$(docker images "${IMAGE_NAME}" --format '{{.ID}}')"
if [ -z "${IMAGE_ID}" ]
then
    echo "Docker image '${IMAGE_NAME}' not found. Build an image or specify another version." >&2
    exit 1
fi

remove-container epistemics
remove-container epistemics-mysql

"${BIN}/create-db-user.sh"

if [ \! -d "${SELEMCA}" ]
then
    mkdir -p "${SELEMCA}"
    cp "${BUILD}/resources/selemca"/*.properties "${SELEMCA}"
    gunzip -c "${BUILD}/resources/selemca/wordnet_2_0.rdf.gz" > "${SELEMCA}/wordnet_2_0.rdf"
fi

docker run --name 'epistemics' -p 8888:8080 -d -v "${SELEMCA}:/usr/local/selemca" --link epistemics-mysql "${IMAGE_NAME}"

sleep 10
docker logs epistemics
