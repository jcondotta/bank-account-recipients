Recipients Project

This project is part of a microservice architecture responsible for managing bank account recipients. The service provides RESTful APIs for creating, fetching, and deleting recipient information. The system leverages various AWS services and modern software development practices to ensure scalability, security, and maintainability.

Tech Stack

Languages & Frameworks:
Java 17: Core programming language.
Micronaut 4.5.0+: Framework used to build the microservice with lightweight, fast startup times and cloud-native capabilities.
Infrastructure:
Amazon DynamoDB: NoSQL database used for storing recipient information.
AWS Lambda: Serverless compute platform for running the microservice.
AWS API Gateway: Gateway for exposing and managing the API endpoints.
AWS Parameter Store: Secure storage for environment configuration and secrets such as the JWT secret key.
Terraform: Infrastructure as Code (IaC) tool used for managing AWS resources like DynamoDB, Lambda, and API Gateway.
LocalStack: A fully functional local AWS cloud stack used for local testing of AWS services like DynamoDB and Lambda.
Authentication:
JSON Web Token (JWT): Used for authentication and authorization to secure API endpoints.
CI/CD & Containerization:
GitHub Actions: Automated pipeline for building, testing, and deploying the microservice.
Docker: Used to containerize the application for local development and deployment.
Docker Hub: The project’s Docker image is registered in Docker Hub for future deployment and scaling needs.
Testing:
JUnit 5: Framework for unit and integration testing.
Mockito: Framework for mocking dependencies in tests.
AssertJ: Library for fluent assertion statements.
TestContainers: Library used to spin up containers for integration testing with services like DynamoDB, SQS, and LocalStack.
Documentation:
Swagger API: API documentation and testing interface to explore the RESTful endpoints.
Features

Recipient Management: Create, fetch, and delete recipients linked to a bank account.
JWT Authentication: Secure endpoints with JSON Web Tokens.
Infrastructure as Code: AWS infrastructure is managed and deployed using Terraform.
Local Testing: Fully local development setup using LocalStack and TestContainers.
CI/CD Pipeline: GitHub Actions for continuous integration and deployment