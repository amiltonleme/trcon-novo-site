# syntax=docker/dockerfile:1

# ---- Stage 1: build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copia o projeto e empacota. O cache BuildKit preserva o repositório Maven entre
# builds, inclusive quando o pom muda, sem executar o caro dependency:go-offline.
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mvn -B -q package -Dmaven.test.skip=true

# ---- Stage 2: runtime ----
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Usuário não-root
RUN groupadd --system trcon && useradd --system --gid trcon --home /app trcon

COPY --from=build /build/target/site-backend-*.jar app.jar
RUN chown -R trcon:trcon /app
USER trcon

EXPOSE 8080

ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=dev

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- "http://localhost:${PORT:-8080}/actuator/health" | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
