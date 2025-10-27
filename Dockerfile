# Use an official JDK base image
FROM eclipse-temurin:21-jdk

# Set working directory inside container
WORKDIR /app

# Copy your source code into the container
COPY src ./src

# Compile the Java file
RUN javac src/main/java/com/example/Main.java -d out

# Set the working directory to compiled output
WORKDIR /app/out

# Run the program
CMD ["java", "com.example.Main"]
