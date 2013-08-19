#!/bin/bash -xv
# Arguments: certpath, lfchost, voname
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
VONAME=$5

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

lcg-infosites --vo $VONAME se
RET=$?

remove_voms_proxy

return $RET