#!/bin/sh
#Arguments portal_prefix_dir certpath, lfchost, voname, directorypath
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
VONAME=$5

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

shift 5
for item in "$@"
do
        STR=${STR}/${item}
	FNAME=${item}
done

lfc-getacl /grid/$VONAME${STR}
RET=$?

remove_voms_proxy

return $RET
