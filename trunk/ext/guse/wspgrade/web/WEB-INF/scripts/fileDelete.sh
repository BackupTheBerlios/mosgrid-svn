#!/bin/bash
# Arguments: certPath, lfchost, voname, path, filename
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
VONAME=$5
FPATH=$6
FNAME=$7
LOGFILE=$CERTPATH/fileOperations.log

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

lcg-del --vo $VONAME -a lfn:$FPATH/$FNAME  &> $LOGFILE
RET=$?
echo $RET

remove_voms_proxy

return $RET
