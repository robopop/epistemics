#!/bin/bash

set -e -x

BIN="$(cd "$(dirname "$0")" ; pwd)"
BUILD="$(dirname "${BIN}")"
SRC="$(dirname "${BUILD}")"
PROJECT="$(dirname "${SRC}")"
DATA="${PROJECT}/data"

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
rm -rf "${DATA}"
