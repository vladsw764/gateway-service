FROM openjdk:17

WORKDIR /app

COPY build/libs/gateway-service-0.0.1-SNAPSHOT.jar /app/gateway-service.jar

CMD ["java", "-jar", "gateway-service.jar"]
