#
# Build
#
FROM maven:3.9.6-amazoncorretto-17-al2023 AS buildtime

WORKDIR /build
COPY . .

RUN mvn clean package -DskipTests

#
# Docker RUNTIME
#
FROM amazoncorretto:17-alpine AS runtime

RUN apk update && apk upgrade --no-cache
RUN apk --no-cache add shadow
RUN useradd --uid 10000 runner

VOLUME /tmp
WORKDIR /app

COPY --from=buildtime /build/target/*.jar /app/app.jar

# The agent is enabled at runtime via JAVA_TOOL_OPTIONS.
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/latest/download/applicationinsights-agent.jar /app/applicationinsights-agent.jar

RUN chown -R runner:runner /app

USER 10000

ENTRYPOINT ["java","-jar","/app/app.jar"]