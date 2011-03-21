#!/bin/sh
#

clear;
#export DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,address=8000"
#mvn clean install;
cd ./container/target/osgi-cdi-container-1.0-SNAPSHOT-all/osgi-cdi-container-1.0-SNAPSHOT/;
java $DEBUG_OPTS -jar bin/felix.jar;
