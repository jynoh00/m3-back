# --빌드--
FROM eclipse-temurin:26-jdk AS build
WORKDIR /app

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test && \
    mv build/libs/*.jar app.jar

# --실행--
FROM eclipse-temurin:26-jre AS runtime
WORKDIR /app

RUN useradd --system --no-create-home appuser
COPY --from=build /app/app.jar ./app.jar

RUN mkdir -p /app/data /app/uploads && chown -R appuser /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]