#!/bin/sh
# Arguments: portal_prefix_dir certpath, lfchost, voname, path, dirname
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
VONAME=$5
DIR=$6/$7
LOGFILE=$CERTPATH/fileOperations.log

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

lfc-rm -r $DIR &> $LOGFILE
RET=$?
echo $RET

remove_voms_proxy

return $RET
