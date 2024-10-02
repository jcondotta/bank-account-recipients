variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "recipients_table_name" {
  description = "The name of the DynamoDB recipients table."
  type        = string
}

variable "recipients_billing_mode" {
  description = "recipients DynamoDB billing mode (PROVISIONED or PAY_PER_REQUEST)"
  type        = string
}

variable "recipients_read_capacity" {
  description = "recipients DynamoDB read capacity units"
  type        = number
}

variable "recipients_write_capacity" {
  description = "recipients DynamoDB write capacity units"
  type        = number
}

variable "terraform_lock_table_name" {
  description = "The name of the DynamoDB table for Terraform lock"
  type        = string
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
}
