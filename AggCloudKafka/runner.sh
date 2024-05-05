#!/bin/bash


find ~/.gradle -type f -name "*.lock" -delete 

rm -R build


gradle build

gradle  shadowJar

java -cp build/libs/JarforExecProject.jar aggregateur.ProducerAgg