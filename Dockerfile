FROM eclipse-temurin:22-jdk AS builder

WORKDIR /src

# copy files
COPY mvnw .
COPY pom.xml .

COPY .mvn .mvn
COPY src src

# make mvnw executable
RUN chmod a+x mvnw && /src/mvnw package -Dmaven.test.skip=true

# Run with jdk 22
FROM eclipse-temurin:22-jre

WORKDIR /app

COPY --from=builder /src/target/atomnotes-0.0.1-SNAPSHOT.jar app.jar

# check if curl command is available
RUN apt update && apt install -y curl

ENV PORT=8080
ENV SPRING_DATA_REDIS_HOST=localhost
ENV SPRING_DATA_REDIS_PORT=6379
ENV SPRING_DATA_REDIS_DATABASE=0
ENV SPRING_DATA_REDIS_USERNAME=""
ENV SPRING_DATA_REDIS_PASSWORD=""
ENV OPENAI_API_KEY=""
ENV UNSPLASH_API_KEY=""
ENV OAUTH2_GOOGLE_CLIENT_ID=""
ENV OAUTH2_GOOGLE_CLIENT_SECRET=""

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar