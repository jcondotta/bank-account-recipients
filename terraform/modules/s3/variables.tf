variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "terraform_state_bucket_name" {
  description = "The name of the S3 bucket for Terraform state"
  type        = string
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
}