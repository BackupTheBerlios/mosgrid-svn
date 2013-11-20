#!/bin/sh
# Arguments: certPath, lfchost, SE, voname, filepath, filename
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
SENAME=$5
VONAME=$6
LOGFILE=$CERTPATH/fileOperations.log

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

shift 5
for item in "$@"
do
	STR=${STR}/${item}
done

lcg-rep -t 20 -v -d $SENAME --vo $VONAME lfn:/grid${STR} &> $LOGFILE
RET=$?
echo $RET

remove_voms_proxy

return $RET
