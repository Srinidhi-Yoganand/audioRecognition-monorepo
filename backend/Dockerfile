FROM openjdk:11-jre-slim
LABEL maintainer="srinidhi"

WORKDIR /app

COPY build/libs/audioRecognition-0.0.1-SNAPSHOT.jar /app/audiorecognition.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "audiorecognition.jar"]