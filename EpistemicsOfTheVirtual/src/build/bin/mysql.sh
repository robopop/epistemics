#!/bin/bash

set -e -x

MYSQL_PORT_3306_TCP_ADDR=mysql
MYSQL_ENV_MYSQL_ROOT_PASSWORD=wortel

docker run -it --link epistemics-mysql:mysql --rm mysql sh -c 'exec mysql -h"mysql" -uroot -pwortel'
