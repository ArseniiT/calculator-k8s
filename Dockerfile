FROM maven
WORKDIR /app
COPY target/calculatord-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
