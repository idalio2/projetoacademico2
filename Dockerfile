# syntax=docker/dockerfile:1

# === estágio de build (usa Maven + JDK 23) ===
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN mvn -B -DskipTests clean package

# === estágio de runtime (JRE 23, mais leve) ===
FROM eclipse-temurin:23-jre
WORKDIR /app

# copia o jar gerado (se não for SNAPSHOT, pode usar *.jar)
COPY --from=build /workspace/target/*-SNAPSHOT.jar /app/app.jar

# Render define a PORT; direcionamos o Spring pra ela
EXPOSE 8080
CMD ["sh","-c","java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-default} -jar /app/app.jar"]
