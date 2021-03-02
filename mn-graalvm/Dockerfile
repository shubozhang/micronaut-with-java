# this image is only used in building, not for runtime
FROM ghcr.io/graalvm/graalvm-ce:latest as graalvm

# gu = graal updater
RUN gu install native-image

COPY . /home/app/mn-graalvm
WORKDIR /home/app/mn-graalvm

RUN native-image --no-server -cp build/libs/mn-graalvm-*-all.jar

FROM frolvlad/alpine-glibc:alpine-3.12
RUN apk update && apk add libstdc++

# No rest api, so no port to expose
EXPOSE 8080

COPY --from=graalvm /home/app/mn-graalvm/mn-graalvm /app/mn-graalvm
ENTRYPOINT ["/app/mn-graalvm"]
