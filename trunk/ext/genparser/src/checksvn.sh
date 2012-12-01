#!/bin/bash
# This script will call ant to check out newest source, compile genparser and put it into xfs mount.

cd $1
svn -Rq revert . > revert.log
linecount=`svn st $1 -u | grep '^.......\*' | wc -l` # older svn versions
#linecount=`svn st $1 -u | grep '^........\*' | wc -l` # newer svn versions
echo $linecount
