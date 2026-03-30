#
# Native build
#
FROM ghcr.io/graalvm/native-image-community:25 AS buildtime

WORKDIR /build
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY .git .git
COPY src src

RUN chmod +x ./mvnw && ./mvnw -Pnative -DskipTests native:compile

#
# Native runtime
#
FROM gcr.io/distroless/base-debian12:nonroot AS runtime

WORKDIR /app
COPY --from=buildtime /build/target/idpay-timeline /app/idpay-timeline

EXPOSE 8080

# GraalVM native images cannot attach JVM agents such as Application Insights.
ENTRYPOINT ["/app/idpay-timeline"]
