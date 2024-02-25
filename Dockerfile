#
# Build stage
#
FROM maven:3.8.4-openjdk-17 AS build

# Create a default user
RUN groupadd --system automation && \
    useradd --system --create-home --gid automation  app && \
    chown --recursive app:automation /home/app

WORKDIR /home/app

COPY src/ ./src/
COPY bpmn/ ./bpmn/
COPY *.bpmn .
COPY pom.xml .


RUN chown -R app.automation /home/app
USER app

#RUN --mount=type=cache,target=/root/.m2 mvn clean package
RUN mvn package

#ENV PATH $PATH:/home/automation/.local/bin

#
# Package stage
#

FROM openjdk:17-ea-17-jdk-slim
COPY --from=build /home/app/target/pictureapp-standalone.jar /home/app/pictureapp-standalone.jar


RUN apt-get update

WORKDIR /home/app

RUN mkdir -p ./bpmn

COPY bpmn/ ./bpmn/
COPY *.bpmn .

RUN chgrp -R 0 /home/app && \
    chmod -R g+rwX /home/app

ENTRYPOINT ["java","-jar","/home/app/pictureapp-standalone.jar"]