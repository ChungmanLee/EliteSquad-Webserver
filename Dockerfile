# Use an OpenJDK base image for Java 20
FROM openjdk:20-jdk-slim

# Set the working directory
WORKDIR /app

# Install necessary packages including PHP and php-cgi
RUN apt-get update && \
    apt-get install -y php php-cgi && \
    rm -rf /var/lib/apt/lists/*


# Copy the Java application JAR file into the container
COPY target/elitesquad-webserver-0.0.1-SNAPSHOT.jar /app/elitesquad-webserver-0.0.1-SNAPSHOT.jar

# Copy the application.properties file into the container
COPY src/main/resources/application.properties /app/application.properties

# Copy the JKS file into the container
COPY keystore.jks /etc/ssl/keystore.jks

# Copy the WEB_ROOT directory into the container
COPY WEB_ROOT /app/WEB_ROOT/

# Copy the script to update /etc/hosts
COPY update-hosts.sh /app/update-hosts.sh
RUN chmod +x /app/update-hosts.sh # && ls -l /app/update-hosts.sh

# Expose ports 8080 and 8443
EXPOSE 8080 8443

# Run the script and the Java application
ENTRYPOINT ["sh", "-c", "/app/update-hosts.sh && exec java -jar /app/elitesquad-webserver-0.0.1-SNAPSHOT.jar"]

# Keep the container running
CMD ["tail", "-f", "/dev/null"]