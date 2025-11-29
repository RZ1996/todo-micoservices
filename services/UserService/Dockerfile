
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace


COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -B -DskipTests dependency:go-offline


COPY src/ src/
RUN ./mvnw -q -B -DskipTests package


FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app
COPY --from=build /workspace/target/*.jar app.jar


EXPOSE 9092

ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
