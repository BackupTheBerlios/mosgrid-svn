#!/bin/sh
# Arguments: certPath, lfchost, voname, filemode, filename
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
VONAME=$5
FILEMODE=$6
FILENAME=$7

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

lfc-chmod $FILEMODE $FILENAME
RET=$?
echo $RET

remove_voms_proxy

return $RET
