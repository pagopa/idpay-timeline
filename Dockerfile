#
# Native build
#
FROM ghcr.io/graalvm/native-image-community:25@sha256:0d936f32bb8acb5bc60c41b33e05f064d7a6aaf36b726538296c54949bd4a3c0 AS buildtime

WORKDIR /build
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY .git .git
COPY src src

RUN chmod +x ./mvnw && ./mvnw -Pnative -DskipTests native:compile

#
# Native runtime dependencies
# Distroless Debian 13 does not ship zlib, but the GraalVM native executable links to libz.so.1.
#
FROM debian:trixie-slim@sha256:26f98ccd92fd0a44d6928ce8ff8f4921b4d2f535bfa07555ee5d18f61429cf0c AS runtimelibs

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
FROM gcr.io/distroless/base-debian13:nonroot@sha256:a696c7c8545ba9b2b2807ee60b8538d049622f0addd85aee8cec3ec1910de1f9 AS runtime

WORKDIR /app
COPY --from=runtimelibs /out/ /
COPY --from=buildtime /build/target/idpay-timeline /app/idpay-timeline

EXPOSE 8080

# GraalVM native images cannot attach JVM agents such as Application Insights.
ENTRYPOINT ["/app/idpay-timeline"]
