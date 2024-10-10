output "lambda_function_arn" {
  description = "The ARN of the Lambda function."
  value       = aws_lambda_function.recipients_lambda.arn
}

output "lambda_function_name" {
  description = "The name of the Lambda function."
  value       = aws_lambda_function.recipients_lambda.function_name
}

output "lambda_invoke_uri" {
  description = "The ARN for invoking the Lambda function via API Gateway"
  value       = "arn:aws:apigateway:${var.aws_region}:lambda:path/2015-03-31/functions/${aws_lambda_function.recipients_lambda.arn}/invocations"
}

output "lambda_filename" {
  description = "The path to the file(jar, zip) for the Lambda function"
  value       = aws_lambda_function.recipients_lambda.filename
}