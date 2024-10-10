variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "lambda_function_arn" {
  description = "The ARN of the Lambda function to integrate with the API Gateway"
  type        = string
}

variable "lambda_function_name" {
  description = "The name of the Lambda function to integrate with the API Gateway"
  type        = string
}

variable "lambda_invoke_uri" {
  description = "The URI for invoking the Lambda function via API Gateway"
  type        = string
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
}