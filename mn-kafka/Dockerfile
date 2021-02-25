# this image is only used in building, not for runtime
FROM ghcr.io/graalvm/graalvm-ce:latest as graalvm

# gu = graal updater
RUN gu install native-image

COPY . /home/app/mn-kafka
WORKDIR /home/app/mn-kafka

RUN native-image --no-server -cp build/libs/mn-kafka-*-all.jar

FROM frolvlad/alpine-glibc:alpine-3.12
RUN apk update && apk add libstdc++

# No rest api, so no port to expose
# EXPOSE 8080

COPY --from=graalvm /home/app/mn-kafka/mn-kafka /app/mn-kafka
ENTRYPOINT ["/app/mn-kafka"]
