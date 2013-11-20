#!/bin/bash
#Arguments: portal_prefix_dir certPath, lfchost, voname

CERTPATH=$3
LFC_HOST=$4
VONAME=$5
PORTAL_PREFIX_DIR=$1
BDII=$2
# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

lfc-ls -l /grid/$5
RET=$?

remove_voms_proxy

return $RET
