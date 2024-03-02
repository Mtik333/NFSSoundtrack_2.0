FROM gradle:latest
WORKDIR /app
COPY . .
#RUN gradle build --no-daemon
ENTRYPOINT ["gradle", "bootRun", "--args='--spring.profiles.active=docker'"]