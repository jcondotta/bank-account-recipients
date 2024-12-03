variable "aws_region" {
  description = "The AWS region where resources will be deployed."
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

variable "subnet_ids" {
  description = "List of subnet IDs to use for the ElastiCache Subnet Group"
  type        = list(string)
}

variable "redis_security_group_id" {
  description = "Security group ID for Redis"
  type        = string
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
}