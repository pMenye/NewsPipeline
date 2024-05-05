FROM openjdk:17-jdk
ARG AGGGRATOR_VERSION=1.0.0
ENV APPDIR=/myapp

RUN mkdir ${APPDIR}
WORKDIR ${APPDIR}

RUN mkdir -p resources/main

ARG JAR_FILE=build/libs/JarforExecProject.jar
ARG RESOURCES_FILE=build/resources/main/*

COPY ${JAR_FILE} ${APPDIR}
COPY ${RESOURCES_FILE} resources/main

ENTRYPOINT exec java -cp JarforExecProject.jar aggregateur.ProducerAgg