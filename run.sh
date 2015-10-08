#! /bin/sh

BASEDIR="$(dirname $0)"
cd $BASEDIR
java -cp lib/javax.persistence_2.0.0.jar:lib/objectdb.jar:bin/ aoop.asteroids.Asteroids
