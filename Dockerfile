ARG username
ARG password
ARG url

FROM openjdk:17.0.2-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw -B package

FROM openjdk:17.0.2
WORKDIR /app
COPY --from=build ./target/resume-0.1.jar .
COPY --from=build ./target/classes/application.yaml .
ENV USERNAME $username
ENV PASSWORD $password
ENV URL $url
CMD java -jar resume-0.1.jar
