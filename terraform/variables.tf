variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "aws_profile" {
  description = "The AWS profile to use for deployment."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string

  validation {
    condition     = contains(["dev", "dev-localstack", "staging", "prod"], var.environment)
    error_message = "Environment must be one of 'dev', 'dev-localstack', 'staging', or 'prod'."
  }
}

variable "recipients_billing_mode" {
  description = "recipients DynamoDB billing mode (PROVISIONED or PAY_PER_REQUEST)"
  type        = string
  default     = "PROVISIONED"

  validation {
    condition     = contains(["PROVISIONED", "PAY_PER_REQUEST"], var.recipients_billing_mode)
    error_message = "recipients_billing_mode must be either 'PROVISIONED' or 'PAY_PER_REQUEST'."
  }
}

variable "recipients_read_capacity" {
  description = "recipients DynamoDB read capacity units"
  type        = number
  default     = 2

  validation {
    condition     = var.recipients_read_capacity > 0
    error_message = "recipients_read_capacity must be a positive number greater than zero."
  }
}

variable "recipients_write_capacity" {
  description = "recipients DynamoDB write capacity units"
  type        = number
  default     = 1

  validation {
    condition     = var.recipients_write_capacity > 0
    error_message = "recipients_write_capacity must be a positive number greater than zero."
  }
}

variable "lambda_memory_size" {
  description = "The memory size (in MB) for the Lambda function"
  type        = number
  default     = 1024

  validation {
    condition     = var.lambda_memory_size >= 128 && var.lambda_memory_size <= 10240
    error_message = "lambda_memory_size must be between 128 MB and 10,240 MB."
  }
}

variable "lambda_timeout" {
  description = "The timeout (in seconds) for the Lambda function"
  type        = number
  default     = 15

  validation {
    condition     = var.lambda_timeout > 0 && var.lambda_timeout <= 900
    error_message = "lambda_timeout must be a positive number and less than or equal to 900 seconds (15 minutes)."
  }
}

variable "lambda_runtime" {
  description = "The runtime for the Lambda function (e.g., java17, java21)"
  type        = string
  default     = "java17"
}

variable "lambda_handler" {
  description = "The fully qualified handler class for the Lambda function"
  type        = string
  default     = "io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction"
}

variable "lambda_environment_variables" {
  description = "A key-value map of environment variables for the Lambda function, used to configure dynamic runtime settings."
  type        = map(string)
  default     = {}
}

variable "jwt_signature_secret_name" {
  description = "The name of the SSM parameter for JWT signature secret"
  type        = string
  default     = "/jwt/signature/secret"
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