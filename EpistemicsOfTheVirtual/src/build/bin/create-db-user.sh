#!/bin/bash

set -e -x

BIN="$(cd "$(dirname "$0")" ; pwd)"
BUILD="$(dirname "${BIN}")"
SRC="$(dirname "${BUILD}")"
PROJECT="$(dirname "${SRC}")"
DATA="${PROJECT}/data"
MYSQL_DATA="${DATA}/mysql"

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

function try-sql() {
	echo "$@" | docker exec -i 'epistemics-mysql' mysql -pwortel
}
function do-sql() {
	try-sql "$@" || true
}

remove-container epistemics-mysql

mkdir -p "${MYSQL_DATA}"

docker run --name 'epistemics-mysql' -d --env MYSQL_ROOT_PASSWORD=wortel -v "${MYSQL_DATA}:/var/lib/mysql" mysql:5.6

N=60
while ! try-sql 'show databases;' && [ "${N}" -gt 0 ]
do
	N=$[$N-1]
	sleep 1
done

do-sql "CREATE USER 'selemca'@'localhost' IDENTIFIED BY 'selemca';"
do-sql "CREATE USER 'selemca'@'%' IDENTIFIED BY 'selemca';"
do-sql "CREATE DATABASE beliefsystem;"
do-sql "GRANT ALL PRIVILEGES ON beliefsystem.* TO 'selemca'@'localhost';"
do-sql "GRANT ALL PRIVILEGES ON beliefsystem.* TO 'selemca'@'%';"
do-sql "FLUSH PRIVILEGES;"
do-sql "show databases;"
