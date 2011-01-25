#!/bin/sh
#

clear;
#mvn clean install;
cd ./container/target/osgi-cdi-container-1.0-SNAPSHOT-all/osgi-cdi-container-1.0-SNAPSHOT/;
java -jar bin/felix.jar;

