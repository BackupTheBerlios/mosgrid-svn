#!/bin/bash
# Arguments: fileMode,  certpath, lfchost, se, uploadname, file_to_be_uploaded, voname, argument
PORTAL_PREFIX_DIR=$1
BDII=$2
FILEMODE=$3
CERTPATH=$4
LFC_HOST=$5
SE=$6
UPLOADNAME=$7
FNAME=$8
VONAME=$9
LOGFILE=$CERTPATH/fileOperations.log
UPLOAD_DIR=$CERTPATH/fileUploadDir

# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

shift 8
for item in "$@"
do
        STR=${STR}/${item}
done

mv "$UPLOAD_DIR/$FNAME" $UPLOAD_DIR/upload_file
lcg-cr -v --vo $VONAME -d $SE -l lfn:/grid$STR/$UPLOADNAME file://$FNAME &> $LOGFILE
UPLOAD_OK=$?

[ $UPLOAD_OK = 0 ] && lfc-chmod $FILEMODE /grid$STR/$UPLOADNAME
echo $UPLOAD_OK

remove_voms_proxy
rm -f $UPLOAD_DIR/upload_file

return $UPLOAD_OK
