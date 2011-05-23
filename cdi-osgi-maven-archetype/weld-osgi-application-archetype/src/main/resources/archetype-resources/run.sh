#!/bin/sh
#

clear;

cd ./${containerArtifactId}/target/${containerArtifactId}-${containerVersion}-all/${containerArtifactId}-${containerVersion}/;
java -jar bin/felix.jar;