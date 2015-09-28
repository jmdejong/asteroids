#! /bin/sh

BASEDIR="$(dirname $0)"
cd $BASEDIR
succes=$(ant | grep "BUILD SUCCESSFUL")
if test "$succes"
then
./run.sh
else
echo BUILD FAILED
fi