FROM amazoncorretto:17-alpine

WORKDIR /app

COPY target/bank-account-recipients-0.1.jar bank-account-recipients.jar

# Expose port 8080 for the application
EXPOSE 8080

# Command to run the app
ENTRYPOINT ["java", "-jar", "/app/bank-account-recipients.jar"]
