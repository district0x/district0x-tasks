FROM clojure:lein-alpine as dependencies
WORKDIR /app
COPY project.clj .
RUN lein deps

FROM dependencies as dependencies-full
WORKDIR /app
COPY . .

FROM dependencies-full
WORKDIR /app
RUN lein test-doo

FROM dependencies-full as builder
WORKDIR /app
# RUN lein uberjar

FROM anapsix/alpine-java
EXPOSE 8080
WORKDIR /app
# COPY --from=builder /app/target/*-standalone.jar ./app.jar
# CMD ["java", "-cp", "/app/app.jar", "clojure.main", "-m", "fy-stock.core"]

#HEALTHCHECK --interval=5m --timeout=3s --retries=3 \
#    CMD curl --output /dev/null --silent --head --fail -k http://localhost:8080/healthcheck || exit 1