#!/bin/bash -xv
#Arguments: certPath, lfchost, voname, path, filename
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
VONAME=$5

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

shift 4
for item in "$@"
do
        STR=${STR}/${item}
	FNAME=${item}
done

lfc-ls -l /grid${STR}
lcg-lg --vo $VONAME lfn:/grid${STR}

remove_voms_proxy
