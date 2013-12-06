#!/bin/sh
# Arguments: certPath, lfchost, voname, oldName, newName
PORTAL_PREFIX_DIR=$1
BDII=$2
CERTPATH=$3
LFC_HOST=$4
VONAME=$5
OLDNAME=$6
NEWNAME=$7
LOGFILE=$CERTPATH/fileOperations.log

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

lfc-rename $OLDNAME $NEWNAME &> $LOGFILE
RET=$?

echo $RET

remove_voms_proxy

return $RET
