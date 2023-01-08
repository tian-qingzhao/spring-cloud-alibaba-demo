FROM openjdk:8-jdk-alpine
VOLUME /tmp
# 镜像的作者信息，即也就是Author
MAINTAINER tianqingzhao
ADD gateway-service-1.0.0.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]