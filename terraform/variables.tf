variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "aws_profile" {
  description = "The AWS profile to use for deployment."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, localstack, staging, prod)"
  type        = string

  validation {
    condition     = contains(["dev", "localstack", "staging", "prod"], var.environment)
    error_message = "Environment must be one of 'dev', 'localstack', 'staging', or 'prod'."
  }
}

variable "lambda_environment_variables" {
  description = "A key-value map of environment variables for the Lambda function, used to configure dynamic runtime settings."
  type        = map(string)
  default     = {}
}

variable "jwt_signature_secret_value" {
  description = "The secure JWT secret value stored in the SSM parameter"
  type        = string
  default     = "8YxVoboGFvI6k3XnJJ09iSXHMEmWM25Fc/8SRl7gZEo="
}

variable "jwt_signature_secret_description" {
  description = "Description for the JWT signature secret"
  type        = string
  default     = "JWT signature secret key"
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
  default = {
    "project" = "bank-account-recipients"
  }
}