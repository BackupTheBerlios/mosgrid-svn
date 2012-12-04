#!/bin/bash
#Arguments: certPath, lfchost, voname, path, dirname
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
VONAME=$5
DIRNAME=$6/$7
LOGFILE=$CERTPATH/fileOperations.log

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

lfc-mkdir -p $DIRNAME &> $LOGFILE
RET=$?
echo $RET

remove_voms_proxy

return $RET
