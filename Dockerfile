FROM openjdk:latest

COPY build/dist /usr/src/myapp
WORKDIR /usr/src/myapp

CMD ["server/server-0.1.1/:/server", "--host", "0.0.0.0", "--port", "8080"]