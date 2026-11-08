# Stage 1: Build the application using Maven with Java 21
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/acme-hrms-backend-*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Xmx380m -jar app.jar"]
