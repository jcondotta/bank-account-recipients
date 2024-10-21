# Recipients Project v.1.0

This project is part of a microservice architecture responsible for managing bank account recipients. The service provides RESTful APIs for creating, fetching, and deleting recipient information. The system leverages various AWS services and modern software development practices to ensure scalability, security, and maintainability.

## Tech Stack

### Languages & Frameworks:
- **Java 17:** Core programming language.
- **Micronaut 4.5.0+:** Framework used to build the microservice with lightweight, fast startup times and cloud-native capabilities.

### Infrastructure:
- **Amazon DynamoDB:** NoSQL database used for storing recipient information.
- **AWS Lambda:** Serverless compute platform for running the microservice.
- **AWS API Gateway:** Gateway for exposing and managing the API endpoints.
- **AWS Parameter Store:** Secure storage for environment configuration and secrets such as the JWT secret key.
- **Terraform:** Infrastructure as Code (IaC) tool used for managing AWS resources like DynamoDB, Lambda, and API Gateway.
- **LocalStack:** A fully functional local AWS cloud stack used for local testing of AWS services like DynamoDB and Lambda.

### Authentication:
- **JSON Web Token (JWT):** Used for authentication and authorization to secure API endpoints.

### CI/CD & Containerization:
- **GitHub Actions:** Automated pipeline for building, testing, and deploying the microservice.
- **Docker:** Used to containerize the application for local development and deployment.
- **Docker Hub:** The project’s Docker image is registered in Docker Hub for future deployment and scaling needs.

### Testing:
- **JUnit 5:** Framework for unit and integration testing.
- **Mockito:** Framework for mocking dependencies in tests.
- **AssertJ:** Library for fluent assertion statements.
- **TestContainers:** Library used to spin up containers for integration testing with services like DynamoDB, SQS, and LocalStack.

### Documentation:
- **Swagger API:** API documentation and testing interface to explore the RESTful endpoints.

## Features

- **Recipient Management:** Create, fetch, and delete recipients linked to a bank account.
- **JWT Authentication:** Secure endpoints with JSON Web Tokens.
- **Infrastructure as Code:** AWS infrastructure is managed and deployed using Terraform.
- **Local Testing:** Fully local development setup using JUnit 5, Mockito, AssertJ, LocalStack and TestContainers.
- **CI/CD Pipeline:** GitHub Actions for continuous integration and deployment.

## Prerequisites

Before running the microservices or working with the Terraform scripts using LocalStack, make sure you have the following installed:

### Explanation of Key Tools:

- **Java 17**:  
  Required to run your Java-based microservices.  
  You can download and install Java 17 from the [Oracle website](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) or 
  use an OpenJDK distribution such as: [AdoptOpenJDK](https://adoptium.net/temurin/releases/?version=17).  
  To verify installation, run the following command:
  ```bash
  java -version

- **Maven**:  
  Required to build and manage dependencies for the Java microservices.  
  You can download and install Maven from the [official Maven website](https://maven.apache.org/install.html).  
  To verify Maven installation, run:
  ```bash
  mvn -v

- **Docker**:  
  Needed to containerize your microservices and run them in isolated environments.
  You can download and install Docker from the [Docker website](https://docs.docker.com/get-started/get-docker/).
  To verify Docker installation, run:
  ```bash
  docker --version

- **Docker Compose**:  
  Helps manage multi-container Docker applications. It's used to orchestrate and run your microservices together locally.
  Docker Compose comes bundled with Docker Desktop, or you can install it separately by following instructions [here](https://docs.docker.com/compose/install/).
  To verify Docker Compose installation, run:
  ```bash
  docker-compose --version

- **Terraform**:
  An Infrastructure as Code (IaC) tool used for managing cloud infrastructure resources, such as AWS services.
  You can download and install Terraform from the [official Terraform website](https://developer.hashicorp.com/terraform/install?product_intent=terraform).
  To verify Terraform installation, run:
  ```bash
  terraform -v

- **tflocal**:  
  `tflocal` is a wrapper that simplifies using Terraform with LocalStack, making it easier to manage infrastructure locally.  
  You can install `tflocal` via pip by running the following command:
  ```bash
  pip install terraform-local
  ```
  
  To verify that tflocal is installed correctly, run:
  ```bash
  tflocal --version
