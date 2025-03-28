# JRE만 있는 가벼운 이미지 사용
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY app/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]