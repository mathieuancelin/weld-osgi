#!/bin/sh
#

export JAVA_HOME=/home/mathieu/tools/jdk1.6.0_21/

clear

cd /home/mathieu/Bureau/weld-osgi/

#/usr/bin/mvn clean install

cd /home/mathieu/Bureau/weld-osgi/container/target/osgi-cdi-container-1.0-SNAPSHOT-all/osgi-cdi-container-1.0-SNAPSHOT/

java -jar bin/felix.jar

