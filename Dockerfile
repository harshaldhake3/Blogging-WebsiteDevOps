# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline
COPY src ./src
RUN mvn -q -e -B clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
ENV SPRING_PROFILES_ACTIVE=prod
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Volume for uploads & H2 (if ever used in container)
VOLUME ["/data/uploads"]
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
