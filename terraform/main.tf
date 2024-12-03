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

module "vpc" {
  source = "./modules/vpc"

  aws_region  = var.aws_region
  environment = var.environment
  tags        = merge(var.tags, { "environment" = var.environment })
}

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
  recipients_billing_mode   = "PROVISIONED"
  recipients_read_capacity  = 2
  recipients_write_capacity = 1
}

#module "redis" {
#  source = "./modules/redis"
#
#  aws_region  = var.aws_region
#  environment = var.environment
#  tags        = merge(var.tags, { "environment" = var.environment })
#
#  subnet_ids              = module.vpc.private_subnet_ids
#  redis_security_group_id = module.vpc.redis_security_group_id
#}
#
#
#module "lambda" {
#  source = "./modules/lambda"
#
#  aws_region             = var.aws_region
#  current_aws_account_id = data.aws_caller_identity.current.account_id
#  environment            = var.environment
#  tags                   = merge(var.tags, { "environment" = var.environment })
#
#  dynamodb_table_arn  = module.dynamodb.recipients_table_arn
#  dynamodb_table_name = module.dynamodb.recipients_table_name
#
#  lambda_function_name         = "recipients-lambda-${var.environment}"
#  lambda_memory_size           = 1024
#  lambda_timeout               = 20
#  lambda_runtime               = "java17"
#  lambda_handler               = "io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction"
#  lambda_environment_variables = var.lambda_environment_variables
#
#  jwt_signature_secret_arn  = module.ssm.jwt_signature_secret_arn
#  jwt_signature_secret_name = module.ssm.jwt_signature_secret_name
#
#  subnet_ids               = module.vpc.private_subnet_ids
#  lambda_security_group_id = module.vpc.lambda_security_group_id
#  redis_host               = module.redis.redis_host
#  redis_port               = module.redis.redis_port
#}
#
#module "apigateway" {
#  source = "./modules/apigateway"
#
#  aws_region  = var.aws_region
#  environment = var.environment
#  tags        = merge(var.tags, { "environment" = var.environment })
#
#  lambda_function_arn  = module.lambda.lambda_function_arn
#  lambda_function_name = module.lambda.lambda_function_name
#  lambda_invoke_uri    = module.lambda.lambda_invoke_uri
#}
