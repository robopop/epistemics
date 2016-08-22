#!/bin/sh -e

BASEDIR=`dirname $0`
SOURCESDIR=$BASEDIR/EpistemicsOfTheVirtual
INSTALLATIONDIR=$BASEDIR/Installation

echo "Sources at $SOURCESDIR"
echo "Storing build results in $INSTALLATIONDIR"

# Belief System
echo "Start of build: BeliefSystem"
mvn -f $SOURCESDIR/BeliefSystem clean install

cp -v -f $SOURCESDIR/BeliefSystem/Rest/target/BeliefSystem.war $INSTALLATIONDIR/webapps
cp -v -f $SOURCESDIR/BeliefSystem/BeliefSystemAdmin/target/BeliefSystemAdmin.war $INSTALLATIONDIR/webapps

# Mental world
echo "Start of build: MentalWorld"
mvn -f $SOURCESDIR/MentalWorld clean install

cp -v -f $SOURCESDIR/MentalWorld/Webapp/target/MentalWorldApp.war $INSTALLATIONDIR/webapps
cp -v -f $SOURCESDIR/MentalWorld/MentalWorldAdmin/target/MentalWorldAdmin.war $INSTALLATIONDIR/webapps

echo "Server files copied to $INSTALLATIONDIR/webapps"
