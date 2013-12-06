#!/bin/bash
# Arguments: portal_prefix_dir certPath, lfchost, replicaToDelete, voname, filepath, filename
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
REPLICA_TO_DELETE=$5
SE_NAME=$6
VONAME=$7

LOGFILE=$CERTPATH/fileOperations.log

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

shift 7
for item in "$@"
do
	STR=${STR}/${item}
done

FILE_LFN=lfn:/grid/$VONAME${STR}

lcg-del -v --vo $VONAME -s $SE_NAME $FILE_LFN
RET=$?
echo $RET

remove_voms_proxy

return $RET
