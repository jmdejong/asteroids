#! /bin/sh

BASEDIR="$(dirname $0)"
cd $BASEDIR
errors=$(ant >/dev/null 2>&1)
if test "$errors"
then
echo "$errors"
else
cd ./bin/
java aoop.asteroids.Asteroids
fi