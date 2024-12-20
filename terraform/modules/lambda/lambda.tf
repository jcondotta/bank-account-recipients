# Define the AWS Lambda function for recipients service
resource "aws_lambda_function" "recipients_lambda" {
  function_name = var.lambda_function_name
  runtime       = var.lambda_runtime
  handler       = var.lambda_handler
  role          = aws_iam_role.recipients_lambda_role_exec.arn
  filename      = var.lambda_file
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout
  architectures = ["arm64"]

  # Configure VPC settings
  vpc_config {
    subnet_ids         = var.subnet_ids
    security_group_ids = [var.lambda_security_group_id]
  }

  environment {
    variables = merge({
      AWS_DYNAMODB_RECIPIENTS_TABLE_NAME = var.dynamodb_table_name
      AWS_SSM_JWT_SIGNATURE_SECRET_NAME = var.jwt_signature_secret_name,
      REDIS_HOST = var.redis_host,
      REDIS_PORT = var.redis_port
    },
      var.lambda_environment_variables
    )
  }

  tags = var.tags
}