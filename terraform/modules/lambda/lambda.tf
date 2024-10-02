# Define the AWS Lambda function for recipients service
resource "aws_lambda_function" "recipients_lambda" {
  function_name = var.recipients_lambda_function_name
  runtime       = var.lambda_runtime
  handler       = var.lambda_handler
  role          = aws_iam_role.recipients_lambda_role_exec.arn
  filename      = "${path.module}/../../../target/bank-account-recipients-0.1.jar" #var.lambda_jar_file
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout
  architectures = ["arm64"]

  environment {
    variables = {
      AWS_DYNAMODB_ENDPOINT              = var.environment == "dev" ? "http://host.docker.internal:4566" : ""
      AWS_DYNAMODB_RECIPIENTS_TABLE_NAME = "recipients-${var.environment}"
    }
  }

  tags = var.tags
}