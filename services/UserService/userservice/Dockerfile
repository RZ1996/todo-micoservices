# --- C:\ToDo\UserService\Dockerfile ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# po mvn package bude jar v target/
COPY target/*.jar app.jar

# uprav podle skutečného portu služby
EXPOSE 9092
ENTRYPOINT ["java","-jar","/app/app.jar"]
