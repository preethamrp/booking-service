FROM openjdk:17-oracle

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY build/libs/booking-service-0.0.1-SNAPSHOT.jar /app/booking-service.jar

# Expose the port your application runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "booking-service.jar"]
