output "api_gateway_invoke_url" {
  description = "The base URL for the recipients API Gateway"
  value       = module.apigateway.api_gateway_invoke_url
}

output "lambda_filename" {
  description = "The path to the file(jar, zip) for the Lambda function"
  value       = module.lambda.lambda_filename
}