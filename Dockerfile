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
# Native runtime dependencies
# Distroless Debian 12 does not ship zlib, but the GraalVM native executable links to libz.so.1.
#
FROM debian:bookworm-slim AS runtimelibs

RUN apt-get update \
 && apt-get install -y --no-install-recommends zlib1g \
 && rm -rf /var/lib/apt/lists/*

RUN libpath="$(find /lib /usr/lib -name 'libz.so.1' | head -n 1)" \
 && libdir="$(dirname "${libpath}")" \
 && mkdir -p "/out${libdir}" \
 && cp -a "${libdir}"/libz.so.1* "/out${libdir}/"

#
# Native runtime
#
FROM gcr.io/distroless/base-debian13:nonroot AS runtime

WORKDIR /app
COPY --from=runtimelibs /out/ /
COPY --from=buildtime /build/target/idpay-timeline /app/idpay-timeline

EXPOSE 8080

# GraalVM native images cannot attach JVM agents such as Application Insights.
ENTRYPOINT ["/app/idpay-timeline"]
