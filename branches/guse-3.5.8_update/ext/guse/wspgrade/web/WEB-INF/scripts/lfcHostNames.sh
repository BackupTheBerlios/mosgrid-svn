#!/bin/sh
# Arguments: voname
PORTAL_PREFIX_DIR=$1
BDII=$2
VONAME=$3

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

lcg-infosites --vo $VONAME lfc
RET=$?

remove_voms_proxy

return $RET
