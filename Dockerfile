#build
FROM maven:3.9.9-sapmachine-22 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

#final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/Test_Bank_Rest-1.0.0.jar app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]