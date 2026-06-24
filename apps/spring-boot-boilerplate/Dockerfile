FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /src

# Copy Maven wrapper and POM for dependency caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source and build
# Skip the env-parity check: scripts/ and compose.yaml are not part of the image build context.
COPY src/ src/
RUN ./mvnw package -DskipTests -Dcheckstyle.skip -DskipEnvParity=true -B

FROM eclipse-temurin:25-jre-alpine AS final

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
EXPOSE 8080

COPY --from=build --chown=appuser:appgroup /src/target/app.jar app.jar

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]
