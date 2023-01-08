FROM openjdk:8-jdk-alpine
VOLUME /tmp
# 指定维护人
MAINTAINER tianqingzhao
# 指定镜像作者、版本
#LABEL author="tianqingzhao" version="1.0.0"
ADD account-service-1.0.0.jar app.jar
#RUN bash -c 'touch /app.jar'
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]