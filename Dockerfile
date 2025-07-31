FROM openjdk:17-jdk-slim

WORKDIR /app

# Maven ile derle
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean install -DskipTests

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# SQLite için veritabanı dosyasının tutulacağı dizini oluştur
RUN mkdir -p /app/data

EXPOSE 8080

# Uygulamayı çalıştır (artık profile ihtiyacımız yok)
ENTRYPOINT ["java", "-jar", "app.jar"]