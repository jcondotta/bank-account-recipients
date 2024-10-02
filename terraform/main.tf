## Backend configuration
#terraform {
#  backend "s3" {
#    bucket         = "terraform-recipients-state-bucket-${var.environment}"
#    key            = "${var.environment}/terraform.tfstate"
#    region         = var.aws_region
#    encrypt        = true
##    dynamodb_table = module.dynamodb.dynamodb_lock_table_name  # Use DynamoDB lock table from the module
#  }
#}
#
provider "aws" {
  region  = var.aws_region
  profile = var.aws_profile
  #
  #  # If running in dev environment, use LocalStack endpoints
  #  endpoints {
  #    dynamodb = var.environment == "dev" ? "http://localhost:4566" : null
  #    lambda   = var.environment == "dev" ? "http://localhost:4566" : null
  #  }
}

# Use data source to fetch the AWS account ID dynamically
data "aws_caller_identity" "current" {}

# Invoke the S3 module
module "s3" {
  source = "./modules/s3"

  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })

  terraform_state_bucket_name = "terraform-recipients-state-bucket-${var.environment}"
}

module "dynamodb" {
  source = "./modules/dynamodb"

  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })

  recipients_table_name     = "recipients-${var.environment}"
  recipients_billing_mode   = var.recipients_billing_mode
  recipients_read_capacity  = var.recipients_read_capacity
  recipients_write_capacity = var.recipients_write_capacity

  terraform_lock_table_name = "terraform-recipients-lock-table-${var.environment}"

}

module "lambda" {
  source = "./modules/lambda"

  aws_region             = var.aws_region
  current_aws_account_id = data.aws_caller_identity.current.account_id
  environment            = var.environment
  tags                   = merge(var.tags, { "environment" = var.environment })

  recipients_lambda_function_name = "recipients-lambda-${var.lambda_runtime}-${var.environment}"
  dynamodb_table_arn              = module.dynamodb.recipients_table_arn # Reference the DynamoDB table ARN from the dynamodb module
  lambda_memory_size              = var.lambda_memory_size
  lambda_timeout                  = var.lambda_timeout
  lambda_runtime                  = var.lambda_runtime
  lambda_handler                  = var.lambda_handler
}

module "apigateway" {
  source = "./modules/apigateway"

  aws_region  = var.aws_region
  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })

  lambda_function_arn  = module.lambda.lambda_function_arn
  lambda_function_name = module.lambda.lambda_function_name

}
