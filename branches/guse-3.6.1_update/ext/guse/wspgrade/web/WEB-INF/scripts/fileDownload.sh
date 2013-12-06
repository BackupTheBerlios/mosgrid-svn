#!/bin/sh -xv
#Arguments: certPath, lfchost, userId, voname, argument, filename
PORTAL_PREFIX_DIR=$1
TEMPDIR=$4
CERTPATH=$5
LFC_HOST=$6
USERID=$3
VONAME=$7
BDII=$2
# Source common things
. $PORTAL_PREFIX_DIR/file_common.sh

#PREVIOUS_DIR=$PWD
#cd $CERTPATH/../../../tomcat/webapps/szupergrid/tmp
#TMP_DIR=$PWD
#cd $PREVIOUS_DIR

#DOWNLOAD_DIR=$TMP_DIR/$USERID/fileDownloadDir

PREVIOUS_DIR=$PWD
cd $PORTAL_PREFIX_DIR/../../../../temp
TMP_DIR=$PWD
cd $PREVIOUS_DIR

DOWNLOAD_DIR=$TEMPDIR/$USERID/fileDownloadDir
echo "--- tmp dir is: " $TMP_DIR
echo "----" $DOWNLOAD_DIR


mkdir -p $DOWNLOAD_DIR

shift 7
for item in "$@"; do
    STR=$STR/$item
    DIRNAME=$FNAME
    FNAME=$item
done

echo ${FNAME}
echo ${DIRNAME}

lcg-cp -v --vo $VONAME lfn:$STR file:$DOWNLOAD_DIR/$FNAME
tar -cf $DOWNLOAD_DIR/$FNAME.tar -C $DOWNLOAD_DIR $FNAME

remove_voms_proxy
