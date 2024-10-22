### Backend configuration
#terraform {
#  backend "s3" {
#    bucket         = "terraform-recipients-state-bucket"
#    key            = "prod/terraform.tfstate"
#    region         = "us-east-1"
#    encrypt        = true
#    dynamodb_table = "terraform-recipients-lock-table"
#    profile        = "jcondotta"
#  }
#}

provider "aws" {
  region  = var.aws_region
  profile = var.aws_profile
}

# Use data source to fetch the AWS account ID dynamically
data "aws_caller_identity" "current" {

}

#module "ecs-fargate" {
#  source = "./modules/ecs-fargate"
#
#  aws_region  = var.aws_region
#  environment = var.environment
#  tags        = merge(var.tags, { "environment" = var.environment })
#
#  ecs_cluster_name = "recipients-cluster-${var.environment}"
#}

module "ssm" {
  source = "./modules/ssm"

  aws_region  = var.aws_region
  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })

  jwt_signature_secret_name        = "/jwt/signature/${var.environment}/secret"
  jwt_signature_secret_value       = var.jwt_signature_secret_value
  jwt_signature_secret_description = var.jwt_signature_secret_description
}

module "dynamodb" {
  source = "./modules/dynamodb"

  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })

  recipients_table_name     = "recipients-${var.environment}"
  recipients_billing_mode   = var.recipients_billing_mode
  recipients_read_capacity  = var.recipients_read_capacity
  recipients_write_capacity = var.recipients_write_capacity
}

module "lambda" {
  source = "./modules/lambda"

  aws_region             = var.aws_region
  current_aws_account_id = data.aws_caller_identity.current.account_id
  environment            = var.environment
  tags                   = merge(var.tags, { "environment" = var.environment })


  dynamodb_table_arn  = module.dynamodb.recipients_table_arn
  dynamodb_table_name = module.dynamodb.recipients_table_name

  lambda_function_name         = "recipients-lambda-${var.environment}"
  lambda_memory_size           = var.lambda_memory_size
  lambda_timeout               = var.lambda_timeout
  lambda_runtime               = var.lambda_runtime
  lambda_handler               = var.lambda_handler
  lambda_environment_variables = var.lambda_environment_variables

  jwt_signature_secret_arn  = module.ssm.jwt_signature_secret_arn
  jwt_signature_secret_name = module.ssm.jwt_signature_secret_name
}

module "apigateway" {
  source = "./modules/apigateway"

  aws_region  = var.aws_region
  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })

  lambda_function_arn  = module.lambda.lambda_function_arn
  lambda_function_name = module.lambda.lambda_function_name
  lambda_invoke_uri    = module.lambda.lambda_invoke_uri
}
