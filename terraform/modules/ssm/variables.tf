variable "aws_region" {
  description = "The aws region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment the Lambda function is deployed to (e.g., dev, prod or staging)"
  type        = string
}

variable "jwt_signature_secret_name" {
  description = "The name of the SSM parameter for JWT signature secret"
  type        = string
}

variable "jwt_signature_secret_value" {
  description = "The secure JWT secret value stored in the SSM parameter"
  type        = string
}

variable "jwt_signature_secret_description" {
  description = "Description for the JWT signature secret"
  type        = string
  default     = "JWT signature secret key"
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
}