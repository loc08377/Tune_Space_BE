# FROM eclipse-temurin:17-jdk-alpine
# WORKDIR /app

# # Copy file jar đã build từ target
# COPY target/Backend_FiveGiveChill-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 8080

# ENTRYPOINT ["java","-jar","/app.jar"]
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy file jar đã build từ target
COPY target/Backend_FiveGiveChill-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# ENTRYPOINT trỏ đúng tới file jar trong WORKDIR
ENTRYPOINT ["java", "-jar", "app.jar"]
