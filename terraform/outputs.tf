#output "api_gateway_invoke_url" {
#  description = "The base URL for the recipients API Gateway"
#  value       = module.apigateway.api_gateway_invoke_url
#}

output "api_gateway_invoke_url" {
  value = (var.environment == "dev-localstack" ?
    "http://${module.apigateway.api_gateway_id}.execute-api.localhost.localstack.cloud:4566/${var.environment}" :
    module.apigateway.api_gateway_invoke_url)
  description = "API Gateway invoke URL based on the environment"
}
#
#output "lambda_filename" {
#  description = "The path to the file(jar, zip) for the Lambda function"
#  value       = module.lambda.lambda_filename
#}